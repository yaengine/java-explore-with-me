package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;

import java.util.List;

@Component
public class StatsClient {
    private final RestClient restClient;

    public StatsClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.restClient = RestClient.builder().baseUrl(statsServerUrl).build();
    }

    public void sendHit(EndpointHitDto endPointHitDto) {
        restClient.post()
                .uri("/hit")
                .body(endPointHitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStats> getStats(String start, String end) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
