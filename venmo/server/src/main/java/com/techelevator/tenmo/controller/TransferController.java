package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
    private static final int STATUS_APPROVED  = 2;

    private final UserDao userDao;
    private final TransferDao transferDao;

    public TransferController (UserDao userDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer, Principal principal){
        /*
        A user can only create an approved transfer
        if they are sending the money
        Otherwise they are requesting a pending transfer
        and must be the one receiving the money
         */
        int userId = userDao.findIdByUsername(principal.getName());
        if (transfer.getStatusId()==STATUS_APPROVED) {
            if (transfer.getUserFromId()!=userId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else if (transfer.getUserToId()!=userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return transferDao.create(transfer);
    }

    @RequestMapping(path = "transfer", method = RequestMethod.GET)
    public List<TransferView> viewTransfersByPending(@RequestParam boolean isPending, Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        return transferDao.getTransferViewByStatus(userId, isPending);
    }

    @RequestMapping(path = "transfer/confirm-pending/{transferId}", method = RequestMethod.PUT)
    public void confirmPendingTransfer(@PathVariable int transferId, @RequestParam boolean isApproved, Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        transferDao.confirm(userId, transferId, isApproved);
    }
}
