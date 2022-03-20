package com.techelevator.tenmo.model;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class Transfer {

    private Integer transferId;
    @Max(value = 2, message = "Status ID must be 1 (Pending) or 2 (Approved)")
    @Min(value = 1, message = "Status ID must be 1 (Pending) or 2 (Approved)")
    private Integer statusId;
    @NotNull(message = "Account from is required")
    private Integer userFromId;
    @NotNull(message = "Account to is required")
    private Integer userToId;
    @Positive(message="Amount must be greater than zero")
    private BigDecimal amount;

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(Integer userFromId) {
        this.userFromId = userFromId;
    }

    public Integer getUserToId() {
        return userToId;
    }

    public void setUserToId(Integer userToId) {
        this.userToId = userToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
