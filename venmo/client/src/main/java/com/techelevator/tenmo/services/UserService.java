package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class UserService {

    private final String userToken;
    private final String API_BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();


    public UserService(String userToken, String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
        this.userToken = userToken;
    }


    public User[] getUsers() {
        return restTemplate.exchange(API_BASE_URL + "user", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(headers);
    }
}
