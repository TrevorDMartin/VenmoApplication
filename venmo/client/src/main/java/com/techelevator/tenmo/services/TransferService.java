package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private final String userToken;
    private final String API_BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();


    public TransferService(String userToken, String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
        this.userToken = userToken;
    }


    public void createTransfer(Transfer transfer) {
        restTemplate.postForObject(API_BASE_URL + "transfer", makeTransferEntity(transfer), Transfer.class);
    }

    public TransferView[] viewTransfersByPending(Boolean isPending){
        return restTemplate.exchange(API_BASE_URL + "transfer?isPending=" + isPending, HttpMethod.GET, makeAuthEntity(), TransferView[].class).getBody();
    }

    public void updateTransfer(int transferId, boolean isApproved){
        String url = API_BASE_URL + "transfer/confirm-pending/" + transferId + "?isApproved=" + isApproved;
        restTemplate.put(url, makeAuthEntity());
    }
    

    private HttpEntity<Transfer> makeTransferEntity (Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(headers);
    }



}
