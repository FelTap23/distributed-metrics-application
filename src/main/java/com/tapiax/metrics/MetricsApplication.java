package com.tapiax.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MetricsApplication {
	
	/*Check StreamDefinition Class in com.tapiax.metrics.kafka */
	public static void main(String[] args) {
		SpringApplication.run(MetricsApplication.class, args);
	}
}
