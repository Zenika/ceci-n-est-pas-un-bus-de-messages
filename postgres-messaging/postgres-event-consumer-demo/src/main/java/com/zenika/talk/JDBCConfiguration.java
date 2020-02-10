package com.zenika.talk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class JDBCConfiguration {

    @Bean
    TaskExecutor jdbcThreadExecutor() {
        return new SimpleAsyncTaskExecutor("events");
    }
}
