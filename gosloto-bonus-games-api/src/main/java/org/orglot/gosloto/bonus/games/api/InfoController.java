package org.orglot.gosloto.bonus.games.api;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class InfoController {

  private static final Date STARTING_TIME = new Date();
  private final PrometheusMeterRegistry prometheusMeterRegistry;

  @ResponseBody
  @GetMapping(value = "/internal/k8s/metrics", produces = TextFormat.CONTENT_TYPE_004)
  public Object metrics() throws IOException {
    Writer writer = new StringWriter();
    TextFormat.write004(writer, prometheusMeterRegistry.getPrometheusRegistry().metricFamilySamples());
    return writer.toString();
  }

  @GetMapping("/internal/k8s/health-check")
  public Mono<Map<String, Object>> healthCheck() {
    return Mono.defer(() -> Mono.just(Map.of(
        "status", "ok",
        "start_time", STARTING_TIME
    )));
  }
}
