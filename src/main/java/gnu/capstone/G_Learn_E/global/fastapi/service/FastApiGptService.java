package gnu.capstone.G_Learn_E.global.fastapi.service;

import gnu.capstone.G_Learn_E.global.fastapi.entity.FastApiProperties;
import gnu.capstone.G_Learn_E.global.security.SecurityPathProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiGptService {

    private final FastApiProperties fastApiProperties;


    @PostConstruct
    private void init() {

        log.info("FastAPI Base URL: {}", fastApiProperties.baseUrl());
        for (String endpointName : fastApiProperties.endpoints().keySet()) {
            FastApiProperties.Endpoint endpoint = fastApiProperties.endpoints().get(endpointName);
            log.info("FastAPI Endpoint - {}: {} {}", endpointName, endpoint.method(), endpoint.path());
        }
    }

}
