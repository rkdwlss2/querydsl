package com.example.sktest.controller;

import com.example.sktest.config.auth.PrincipalDetails;
import com.example.sktest.config.dto.FacebookDto;
import com.example.sktest.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class FeedController {
    private final UserRepository userRepository;

    // 유저 혹은 매니저 혹은 어드민이 접근 가능
    @GetMapping("feed")
    public ResponseEntity<String> user(Authentication authentication, HttpServletRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        String userId = principal.getUser().getProviderId();

        String accessToken = request.getHeader("AccessToken");
        if (accessToken == null || !accessToken.startsWith("Bearer")){
            return new ResponseEntity<>("this is no accessToken",HttpStatus.BAD_REQUEST);
        }
        String facebookAccessToken =request.getHeader("AccessToken").replace("Bearer ","");

        WebClient client = WebClient.builder()
                .baseUrl("https://graph.facebook.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .build();
        FacebookDto response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v15.0/{id}/feed")
                        .queryParam("access_token",facebookAccessToken)
                        .queryParam("debug","all")
                        .queryParam("fields","attachments,message,picture,link,name,caption,description,source")
                        .queryParam("format","json")
                        .queryParam("method","get")
                        .queryParam("pretty","0")
                        .queryParam("suppress_http_code","1")
                        .queryParam("transport","cors")
                        .build(userId)
                )
                        .retrieve()
                        .bodyToMono(FacebookDto.class)
                        .block();

        log.info("responseData : "+response);
        return new ResponseEntity<>(principal.getUser().toString(), HttpStatus.OK);
    }

    // This method returns filter function which will log request data
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }
}
