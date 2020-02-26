package com.zenika.talk.events.control;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class EventsConfiguration {

	// Ex configuration https://docs.spring.io/spring/docs/4.2.x/spring-framework-reference/html/scheduling.html
	// Beware old doc
	@Value("${events.corePoolSize}")
	int corePoolSize = 5;

    @Bean
    TaskExecutor eventsThreadExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(corePoolSize);
		threadPool.setThreadGroupName("postgres_events_");
		return threadPool;
    }
}
