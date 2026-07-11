package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.client.BaseClient;
import ru.practicum.client.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EndpointHitClient extends BaseClient {
    private static final String API_PREFIX = "";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public EndpointHitClient(@Value("${stats_server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getStatsByPeriod(LocalDateTime start, LocalDateTime end)  {

        Map<String, Object> parameters = Map.of(
                "start", start.format(FORMATTER),
                "end", end.format(FORMATTER)
        );
        return get("/stats?start={start}&end={end}", parameters);
    }

    public ResponseEntity<Object> getStatsByUriAndPeriod(LocalDateTime start, LocalDateTime end, List<String> uris)  {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("start", start.format(FORMATTER));
        parameters.put("end", end.format(FORMATTER));

        StringBuilder path = new StringBuilder("/stats?start={start}&end={end}");

        for (int i = 0; i < uris.size(); i++) {
            String uriParamName = "uri" + i;

            path.append("&uris={").append(uriParamName).append("}");
            parameters.put(uriParamName, uris.get(i));
        }
        return get(path.toString(), parameters);
    }

    public ResponseEntity<Object> getStatsByPeriodAndUniqueTrue(LocalDateTime start, LocalDateTime end)  {

        Map<String, Object> parameters = Map.of(
                "start", start.format(FORMATTER),
                "end", end.format(FORMATTER),
                "unique", true
        );
        return get("/stats?start={start}&end={end}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> getStatsByUriAndPeriodAndUniqueTrue(LocalDateTime start, LocalDateTime end, List<String> uris)  {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("start", start.format(FORMATTER));
        parameters.put("end", end.format(FORMATTER));
        parameters.put("unique", true);

        StringBuilder path = new StringBuilder("/stats?start={start}&end={end}&unique={unique}");

        for (int i = 0; i < uris.size(); i++) {
            String uriParamName = "uri" + i;

            path.append("&uris={").append(uriParamName).append("}");
            parameters.put(uriParamName, uris.get(i));
        }
        return get(path.toString(), parameters);
    }

    public ResponseEntity<Object> saveEndpointHit(EndpointHitDto endpointHitDto) {
      return post("/hit", endpointHitDto);
    }
}
