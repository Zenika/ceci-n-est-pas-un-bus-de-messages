package com.zenika.talk

import groovy.json.JsonSlurper
import groovy.sql.Sql

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Channel<T> {

    def jsonSlurper = new JsonSlurper()

    Channel(String url,
            String username,
            String password,
            MessageHandler<T> messageHandler,
            MessageConverter<T> converter) {

        Map dbConnParams = [
                url: url,
                user: username,
                password: password,
                driver: 'org.postgresql.Driver']

        Sql.withInstance(dbConnParams) { Sql sql ->

            def conn = sql.getConnection()
            def pgConn = conn.unwrap(org.postgresql.PGConnection.class)

            sql.execute("LISTEN messages")

            while(true) {
                def notifications = pgConn.getNotifications(10000);
                if(notifications != null) {
                    notifications.each {
                        def jsonMessage = jsonSlurper.parseText(it.getParameter())
                        UUID id = UUID.fromString(jsonMessage.id)
                        LocalDateTime date = LocalDateTime.parse(jsonMessage.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        def payload = converter.convert(jsonMessage.payload)
                        def message = new Message(it.getName(), id, date, payload)

                        messageHandler.handleMessage(message)
                    }
                }
            }
        }

    }

}
