package org.orglot.gosloto.bonus.games.client.config;

import org.orglot.gosloto.bonus.games.client.BonusGameClient;
import org.orglot.gosloto.bonus.games.client.BonusGameClientImpl;
import org.orglot.gosloto.grpc.starter.client.GrpcClient;
import org.orglot.gosloto.grpc.starter.client.GrpcCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BonusGameClientConfiguration {

    @Bean
    public GrpcCoordinates bonusGameGrpcCoordinates(@Value("${app.services.bonus-game.url:localhost}") String host,
                                                    @Value("${app.services.bonus-game.grpc.port:31002}") int port) {
        return GrpcCoordinates.builder()
                .host(host)
                .port(port)
                .name("bonus-game")
                .build();
    }

    @Bean
    public BonusGameClient bonusGameClient(@Autowired @Qualifier("bonus-game") GrpcClient grpcClient) {
        return new BonusGameClientImpl(grpcClient);
    }
}
