package com.zenika.talk;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Component
public class EventConsumer {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @Value("${events.postgres.url}")
    String postgresUrl;

    @Value("${events.postgres.username}")
    String postgresUsername;

    @Value("${events.postgres.password}")
    String postgresPassword;

    @Value("${events.channel}")
    String eventChannel;

    @Autowired
    TaskExecutor executor;

    @PostConstruct
    public void connect() {
        executor.execute(eventPoller);
    }

    private final Runnable eventPoller = () -> {
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

            log.info("Setting-up listener channel {}", eventChannel);
            Statement statement = conn.createStatement();
            statement.execute("LISTEN " + eventChannel);
            statement.close();
            log.info("Listener was successfully setup");
            log.info("Listening for notifications on channel {}", eventChannel);

            while (true) {
                PGNotification[] notifications = pgConn.getNotifications(10_000);
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        log.trace("Received new notification on {}", notification.getName());
                        log.trace("Received parameter: {}", notification.getParameter());

                        Message message = jacksonObjectMapper.readValue(notification.getParameter(), Message.class);
                        log.info(message.toString());
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
    };
}
