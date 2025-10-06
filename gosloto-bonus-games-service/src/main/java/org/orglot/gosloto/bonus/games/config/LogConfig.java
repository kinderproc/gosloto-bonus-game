package org.orglot.gosloto.bonus.games.config;

import org.orglot.gosloto.components.log.LogRepository;
import org.orglot.gosloto.components.log.kafka.KafkaLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class LogConfig {
    @Bean
    public LogRepository logRepository(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaLogRepository(kafkaTemplate);
    }
}
