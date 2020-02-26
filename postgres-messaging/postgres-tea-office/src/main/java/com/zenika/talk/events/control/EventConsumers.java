package com.zenika.talk.events.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.talk.office.entity.Office;
import com.zenika.talk.office.entity.OfficeType;
import com.zenika.talk.tea.boundary.TeaSupplierService;
import com.zenika.talk.tea.entity.TeaContract;
import com.zenika.talk.tea.entity.TeaOrder;
import com.zenika.talk.tea.entity.TeaSupply;
import com.zenika.talk.tea.boundary.TeaBuyerService;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
		if (office.getType() == OfficeType.LOCAL) {
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

	// TODO : on shutdown find a way to close this conn
	private <T> void listenToEvents(String topic, Consumer<T> consumer, TypeReference<Message<T>> typeRef) {
		try(Connection conn = dataSource.getConnection()) {
			PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);

			log.info("Setting-up listener channel {}", topic);
			Statement statement = conn.createStatement();
			statement.execute("LISTEN " + topic);
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
		} catch (SQLException | JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
