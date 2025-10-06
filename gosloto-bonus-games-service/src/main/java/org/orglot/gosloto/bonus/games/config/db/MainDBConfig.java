package org.orglot.gosloto.bonus.games.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MainDBConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.main")
    public DataSourceProperties mainSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource mainDataSource() {
        return mainSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Qualifier("main")
    @Bean
    public JdbcTemplate mainJdbcTemplate(@Qualifier("mainDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
