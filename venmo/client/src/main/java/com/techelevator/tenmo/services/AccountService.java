package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private final String userToken;
    private final String API_BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();


    public AccountService(String userToken, String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
        this.userToken = userToken;
    }


    public BigDecimal getBalance() {
        return restTemplate.exchange(API_BASE_URL + "account/balance" , HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(headers);
    }
}
