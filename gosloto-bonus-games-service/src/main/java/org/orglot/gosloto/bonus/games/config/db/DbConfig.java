package org.orglot.gosloto.bonus.games.config.db;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
public class DbConfig {

    @Value("${app.name:gosloto-tournaments}")
    private String appName;

    @Bean
    public ConnectionFactory connectionFactory(R2dbcProperties dbProperties) {
        var dbUrl = ConnectionFactoryOptions.parse(dbProperties.getUrl());
        var host = (String) dbUrl.getRequiredValue(Option.valueOf("host"));
        var port = (int) dbUrl.getRequiredValue(Option.valueOf("port"));
        var database = (String) dbUrl.getRequiredValue(Option.valueOf("database"));
        var driver = (String) dbUrl.getRequiredValue(Option.valueOf("driver"));

        ConnectionFactoryOptions connectionFactoryOptions = ConnectionFactoryOptions.builder()
                .option(DRIVER, driver)
                .option(HOST, host)
                .option(PORT, port)
                .option(Option.valueOf("schema"), dbProperties.getProperties().get("schema"))
                .option(USER, dbProperties.getUsername())
                .option(PASSWORD, dbProperties.getPassword())
                .option(Option.valueOf("options"), "application_name=" + appName)
                .option(DATABASE, database)
                .build();

        var factory = ConnectionFactories.get(connectionFactoryOptions);
        return new ConnectionPool(ConnectionPoolConfiguration.builder(factory)
                .maxSize(dbProperties.getPool().getMaxSize())
                .initialSize(dbProperties.getPool().getInitialSize())
                .maxIdleTime(dbProperties.getPool().getMaxIdleTime())
                .name("bonus.game.r2dbc.pool")
                .build());
    }
}
