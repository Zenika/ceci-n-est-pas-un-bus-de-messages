package com.zenika.talk;

import java.time.LocalDate;

public class Message {

    private String id;
    private LocalDate date;
    private Greeting payload;

    public Message() {
    }

    public Message(String id, LocalDate date, Greeting payload) {
        this.id = id;
        this.date = date;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Greeting getPayload() {
        return payload;
    }

    public void setPayload(Greeting payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", payload=" + payload +
                '}';
    }
}
