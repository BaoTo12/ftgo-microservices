package com.chibao.orderservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();

        headers.add(
                UserContext.CORRELATION_ID,
                UserContextHolder.getContext().getCorrelationId()
        );

        headers.add(
                UserContext.AUTH_TOKEN,
                UserContextHolder.getContext().getAuthToken()
        );

        log.debug("Adding correlation ID: {}",
                UserContextHolder.getContext().getCorrelationId());

        return execution.execute(request, body);
    }
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        interceptors.add(new UserContextInterceptor());
        template.setInterceptors(interceptors);
        return template;
    }
}
