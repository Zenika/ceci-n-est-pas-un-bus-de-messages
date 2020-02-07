package test

import groovy.sql.Sql

static void main(String[] args) {
    Map dbConnParams = [
            url: 'jdbc:postgresql://localhost:5432/postgres',
            user: 'postgres',
            password: 'postgres',
            driver: 'org.postgresql.Driver']

    Sql.withInstance(dbConnParams) { Sql sql ->

        def conn = sql.getConnection()
        def pgConn = conn.unwrap(org.postgresql.PGConnection.class)

        sql.execute("LISTEN messages")

        while(true) {
            def notifications = pgConn.getNotifications(10000);
            if(notifications != null) {
                notifications.each {
                    println "${it.getName()}, ${it.getParameter()}"
                    // prints this
                    // messages, {"id":"32472af4-739f-4263-9878-5530ff64057d","date":"2020-02-07T15:01:47.513735","payload":{"hello": "world"}}
                }
            }
        }
    }

}