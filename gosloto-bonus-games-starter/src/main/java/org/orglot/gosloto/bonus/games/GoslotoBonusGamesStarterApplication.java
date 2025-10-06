package org.orglot.gosloto.bonus.games;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration;
import net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration;
import net.devh.boot.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration;
import org.orglot.gosloto.grpc.starter.server.CommonGrpcServerInterceptorsAutoConfiguration;
import org.orglot.gosloto.starter.tracing.grpc.GrpcTracingAutoConfiguration;
import org.orglot.gosloto.starter.tracing.kafka.TracingBatchKafkaAutoConfiguration;
import org.orglot.gosloto.starter.tracing.rabbit.RabbitTracingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.instrument.redis.TraceRedisAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class,
        RabbitTracingAutoConfiguration.class,
        GrpcTracingAutoConfiguration.class,
        TracingBatchKafkaAutoConfiguration.class,
        CommonGrpcServerInterceptorsAutoConfiguration.class,
        GrpcClientMetricAutoConfiguration.class,
        GrpcServerMetricAutoConfiguration.class,
        GrpcServerAutoConfiguration.class,
        GrpcServerFactoryAutoConfiguration.class,
        GrpcClientAutoConfiguration.class,
        GrpcDiscoveryClientAutoConfiguration.class,
        GrpcCommonCodecAutoConfiguration.class,
        TraceRedisAutoConfiguration.class
})
public class GoslotoBonusGamesStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoslotoBonusGamesStarterApplication.class, args);
    }

}
