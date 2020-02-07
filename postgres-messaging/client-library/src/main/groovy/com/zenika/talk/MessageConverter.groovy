package com.zenika.talk

interface MessageConverter<T> {

    T convert(Map<String, Object> payload)

}