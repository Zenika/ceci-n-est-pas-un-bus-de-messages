package com.zenika.talk


import java.time.LocalDateTime

class Message<T> {
    private final String channel
    private final UUID id
    private final LocalDateTime date
    private final T payload

    Message(String channel, UUID id, LocalDateTime date, T payload) {
        this.channel = channel
        this.id = id
        this.date = date
        this.payload = payload
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Message message = (Message) o

        if (channel != message.channel) return false
        if (date != message.date) return false
        if (id != message.id) return false
        if (payload != message.payload) return false

        return true
    }

    int hashCode() {
        int result
        result = (channel != null ? channel.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (date != null ? date.hashCode() : 0)
        result = 31 * result + (payload != null ? payload.hashCode() : 0)
        return result
    }

    String getChannel() {
        return channel
    }

    UUID getId() {
        return id
    }

    LocalDateTime getDate() {
        return date
    }

    T getPayload() {
        return payload
    }
}
