package com.techelevator.tenmo.model;

public class TransferView {

    private int transferID;
    private String type;
    private String status;
    private String userFrom;
    private String userTo;
    private double amount;

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }




}
