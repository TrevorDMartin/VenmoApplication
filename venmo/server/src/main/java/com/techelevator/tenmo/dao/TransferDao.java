package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;

import java.util.List;

public interface TransferDao {

    Transfer create (Transfer transfer);

    List<TransferView> getTransferViewByStatus(int userId, boolean pending);

    void confirm(int userId, int transferId, boolean isApproved);
}
