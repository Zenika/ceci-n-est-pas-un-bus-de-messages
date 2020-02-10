package com.zenika.talk;

public class Greeting {

    private String hello;

    public Greeting() {
    }

    public Greeting(String hello) {
        this.hello = hello;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    @Override
    public String toString() {
        return "Greeting{" +
                "hello='" + hello + '\'' +
                '}';
    }
}
