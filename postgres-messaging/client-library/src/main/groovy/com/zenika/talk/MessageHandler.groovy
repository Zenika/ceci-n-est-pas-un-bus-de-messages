package com.zenika.talk

interface MessageHandler<T> {

    void handleMessage(Message<T> message)

}