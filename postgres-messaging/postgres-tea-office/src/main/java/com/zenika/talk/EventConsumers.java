package com.zenika.talk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.function.Consumer;

@Component
public class EventConsumers {

	@Autowired
	DataSource dataSource;

	@Autowired
	TeaSupplierService teaSupplierService;

	@Autowired
	TeaBuyerService teaBuyerService;

	@Autowired
	Office office;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private static final Logger log = LoggerFactory.getLogger(EventConsumers.class);

    @Value("${events.postgres.url}")
    String postgresUrl;

    @Value("${events.postgres.username}")
    String postgresUsername;

    @Value("${events.postgres.password}")
    String postgresPassword;

    @Autowired
    TaskExecutor executor;

	/**
	 * Test SQL
	 * insert into messages (topic, payload) values ('orders', '{"office": "London", "amount": 1000, "unit": "t"}')
	 * insert into messages (topic, payload) values ('supply', '{"office": "London", "amount": 1000, "unit": "t"}')
	 * insert into messages (topic, payload) values ('contracts', '{"customer": "London", "supplier": "China", "amount": 1000, "unit": "t"}')
	 */

	@PostConstruct
    public void connect() {
		if(office.getType() == OfficeType.LOCAL) {
			executor.execute(() -> listenToEvents("orders", teaSupplierService::onTeaOrder,
				new TypeReference<Message<TeaOrder>>() {
				}));
			executor.execute(() -> listenToEvents("contracts", teaSupplierService::onTeaContract,
				new TypeReference<Message<TeaContract>>() {
				}));
		} else {
			executor.execute(() -> listenToEvents("supply", teaBuyerService::onTeaSupply,
				new TypeReference<Message<TeaSupply>>() {
				}));
		}
    }

    private <T> void listenToEvents(String topic, Consumer<T> consumer, TypeReference<Message<T>> typeRef) {
		log.info("Initializing connection to PostgresSql");
		log.info("Connection URL: {}", postgresUrl);

		Connection conn = null;

		try {
			Class.forName("org.postgresql.Driver");

			Properties props = new Properties();
			props.setProperty("user", postgresUsername);
			props.setProperty("password", postgresPassword);
			props.setProperty("ssl", "false");

			conn = DriverManager.getConnection(postgresUrl, props);
			PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);

			log.info("Setting-up listener channel {}", topic);
			Statement statement = conn.createStatement();
			statement.execute("LISTEN " + topic);
			statement.close();
			log.info("Listener was successfully setup");
			log.info("Listening for notifications on channel {}", topic);

			while (true) {
				PGNotification[] notifications = pgConn.getNotifications(10_000);
				if (notifications != null) {
					for (PGNotification notification : notifications) {
						log.trace("Received new notification on {}", notification.getName());
						log.trace("Received parameter: {}", notification.getParameter());

						Message<T> message = jacksonObjectMapper.readValue(notification.getParameter(),
							typeRef);
						consumer.accept(message.getPayload());
					}
				}
			}

		} catch (ClassNotFoundException | SQLException | JsonProcessingException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				log.info("Closing connection to postgres");
				try {
					conn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				log.info("Connection to postgres closed");
			}
		}
	}
}
