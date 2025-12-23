package com.demo;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class PredictController {

  private final WebClient client;

  public PredictController(WebClient.Builder builder,
                          @Value("${model.base-url}") String baseUrl) {
    this.client = builder.baseUrl(baseUrl).build();
  }

  @GetMapping("/health")
  public Mono<Map> health() {
    return client.get().uri("/health")
      .retrieve()
      .bodyToMono(Map.class)
      .timeout(Duration.ofSeconds(5));
  }

  @PostMapping(value = "/predict", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map> predict(@RequestBody Map<String, Object> payload) {
    return client.post().uri("/predict")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(payload)
      .retrieve()
      .bodyToMono(Map.class)
      .timeout(Duration.ofSeconds(10));
  }

  @PostMapping(value = "/predict/by-id", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map> predictById(@RequestBody Map<String, Object> payload) {
    // payload esperado: { "customer_id": "N001" }
    if (!payload.containsKey("customer_id") || payload.get("customer_id") == null) {
      return Mono.just(Map.of("error", "customer_id is required"));
    }

    return client.post().uri("/predict_by_id")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(payload)
      .retrieve()
      .bodyToMono(Map.class)
      .timeout(Duration.ofSeconds(10));
  }
}
