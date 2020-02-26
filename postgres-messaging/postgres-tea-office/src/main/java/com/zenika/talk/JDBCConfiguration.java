package com.zenika.talk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class JDBCConfiguration {

	// TODO should be propertized
	// Ex configuration https://docs.spring.io/spring/docs/4.2.x/spring-framework-reference/html/scheduling.html
	// Beware old doc
	int corePoolSize = 5;

    @Bean
    TaskExecutor jdbcThreadExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(corePoolSize);
		threadPool.setThreadGroupName("postgres_events_");
		return threadPool;
    }
}
