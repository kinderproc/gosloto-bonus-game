package org.orglot.gosloto.bonus.games.config;

import org.gosloto.promo.config.PromoWalletGrpcConfiguration;
import org.orglot.gosloto.bonus.client.config.BonusServiceClientConfiguration;
import org.orglot.gosloto.bonus.games.properties.AppProperties;
import org.orglot.gosloto.components.log.kafka.KafkaConfig;
import org.orglot.gosloto.files.client.files.FilesFeignClient;
import org.orglot.gosloto.metrics.configurer.GoslotoMetricConfig;
import org.orglot.gosloto.starter.config.dictionaries.DictionaryConfiguration;
import org.orglot.gosloto.user.service.client.grpc.config.UserServiceGrpcClientConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({GoslotoMetricConfig.class, KafkaConfig.class, BonusServiceClientConfiguration.class, DictionaryConfiguration.class,
    PromoWalletGrpcConfiguration.class, UserServiceGrpcClientConfiguration.class, AppProperties.class})
@Configuration
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@EnableFeignClients(clients = FilesFeignClient.class)
public class GeneralConfiguration {

}
