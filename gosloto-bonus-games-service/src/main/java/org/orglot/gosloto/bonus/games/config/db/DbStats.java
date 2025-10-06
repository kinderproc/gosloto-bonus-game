package org.orglot.gosloto.bonus.games.config.db;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.PoolMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.Set;
import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DbStats {
    private final PrometheusMeterRegistry prometheusMeterRegistry;
    private final Set<DatabaseClient> databaseClients;

    @PostConstruct
    void init() {
        databaseClients.forEach(databaseClient -> {
            if (databaseClient.getConnectionFactory() instanceof ConnectionPool cp) {
                cp.getMetrics().ifPresent(poolMetrics -> {
                    Gauge.builder("r2dbc.pool" + ".acquiredSize", poolMetrics, PoolMetrics::acquiredSize)
                            .tag("pool", cp.getMetadata().getName())
                            .register(prometheusMeterRegistry);
                    Gauge.builder("r2dbc.pool" + ".allocatedSize", poolMetrics, PoolMetrics::allocatedSize)
                            .tag("pool", cp.getMetadata().getName())
                            .register(prometheusMeterRegistry);
                    Gauge.builder("r2dbc.pool" + ".idleSize", poolMetrics, PoolMetrics::idleSize)
                            .tag("pool", cp.getMetadata().getName())
                            .register(prometheusMeterRegistry);
                    Gauge.builder("r2dbc.pool" + ".pendingAcquireSize", poolMetrics,
                                    PoolMetrics::pendingAcquireSize)
                            .tag("pool", cp.getMetadata().getName())
                            .register(prometheusMeterRegistry);
                    Gauge.builder("r2dbc.pool" + ".maxAllocatedSize",
                                    poolMetrics, PoolMetrics::getMaxAllocatedSize)
                            .tag("pool", cp.getMetadata().getName())
                            .register(prometheusMeterRegistry);
                    Gauge.builder("r2dbc.pool" + ".maxPendingAcquireSize", poolMetrics,
                                    PoolMetrics::getMaxPendingAcquireSize)
                            .tag("pool", cp.getMetadata().getName())
                            .register(prometheusMeterRegistry);
                });
            }
        });
    }
}
