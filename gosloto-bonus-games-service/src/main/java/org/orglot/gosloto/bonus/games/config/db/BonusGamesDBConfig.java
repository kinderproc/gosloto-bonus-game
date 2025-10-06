package org.orglot.gosloto.bonus.games.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BonusGamesDBConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.bonus-games")
    public DataSourceProperties bonusGamesSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource bonusGamesDataSource() {
        return bonusGamesSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate bonusGamesJdbcTemplate(@Qualifier("bonusGamesDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate bonusGamesNamedParameterJdbcTemplate(@Qualifier("bonusGamesDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
