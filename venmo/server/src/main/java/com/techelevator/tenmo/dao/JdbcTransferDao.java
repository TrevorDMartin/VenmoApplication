package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private static final int STATUS_APPROVED  = 2;
    private static final int TYPE_REQUEST = 1;
    private static final int TYPE_SEND = 2;
    private final JdbcTemplate jdbcTemplate;


    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer create(Transfer transfer) {

        int transferType = transfer.getStatusId()==STATUS_APPROVED ? TYPE_SEND: TYPE_REQUEST;

        String sql = "INSERT INTO transfer (account_from, account_to, transfer_status_id, amount, transfer_type_id) \n" +
                "VALUES ((SELECT account_id FROM account WHERE user_id = ?), " +
                "(SELECT account_id FROM account WHERE user_id = ?), ?, ?, ?) RETURNING transfer_id;";
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getUserFromId(),
                    transfer.getUserToId(), transfer.getStatusId(), transfer.getAmount(), transferType);
            if (transfer.getTransferId()==null) throw new Exception("Transfer was not created on the database");
            transfer.setTransferId(id);
        } catch (Exception e) {
            //BasicLogger.log(e.getMessage)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (transfer.getStatusId()==STATUS_APPROVED) adjustBalance(transfer.getTransferId());

        return transfer;
    }

    @Override
    public List<TransferView> getTransferViewByStatus(int userId, boolean pending){
        List<TransferView> transferViewList = new ArrayList<>();
        /*
        Joins 5 tables to return descriptive string values of id counterparts
        Because account is only joined to transfer via account_from
        I used a subquery with a join on account_to to retrieve the other username
        Finally i user a ternary operation to differentiate between pending or not pending.
         */
        String sql = "SELECT transfer_id, transfer_status_desc, transfer_type_desc, amount, username AS user_from, " +

                    "(SELECT username FROM tenmo_user " +
                    "JOIN account ON tenmo_user.user_id = account.user_id " +
                    "WHERE account_id = account_to ) AS user_to " +

                "FROM tenmo_user " +
                "JOIN account ON tenmo_user.user_id = account.user_id " +
                "JOIN transfer ON account_id = account_from " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "WHERE tenmo_user.user_id = ? AND transfer.transfer_status_id ";
        sql += pending ? "= 1;": "= 2;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()){
            transferViewList.add(mapRowToTransferView(results));
        }
        return transferViewList;
    }

    @Override
    public void confirm(int userId, int transferId, boolean isApproved) {
        /*
        If transferId is not valid, or userId is not the account_from user
        sql query will return 0 and app will throw ResponseStatusExceptions
         */
        int status = 3;
        if (isApproved) status = 2;
        String sql = "UPDATE transfer SET transfer_status_id = ?\n" +
                "WHERE transfer_id = ? AND transfer_status_id = 1 AND " +
                "account_from = (SELECT account_id FROM account WHERE user_id = ?);";
        int result = jdbcTemplate.update(sql, status, transferId, userId);
        if (result!=1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(isApproved) {
            adjustBalance(transferId);
        }
    }

    // Helper Methods
    private void adjustBalance(int transferId) {
        /*
        Adjusting transfer only using the id adds security
        Update query needs to join transfer to account to accomplish

        Open up a transaction
        Attempt to adjust both accounts related to transfer respectively
        If there's any issue rollback transaction
        Otherwise commit transaction and move on.
         */
        jdbcTemplate.update("BEGIN TRANSACTION;");
        String sql = "UPDATE account \n" +
                "SET balance = balance - amount\n" +
                "FROM transfer\n" +
                "WHERE account_id = account_from AND transfer_id = ?;\n";
        int confirm = jdbcTemplate.update(sql, transferId);
        if(confirm!=1) {
            jdbcTemplate.update("ROLLBACK;");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        sql = "UPDATE account \n" +
                "SET balance = balance + amount\n" +
                "FROM transfer\n" +
                "WHERE account_id = account_to AND transfer_id = ?;";
        confirm = jdbcTemplate.update(sql, transferId);
        if(confirm!=1) {
            jdbcTemplate.update("ROLLBACK;");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        jdbcTemplate.update("COMMIT;");
    }

    private TransferView mapRowToTransferView(SqlRowSet result) {
        TransferView transfer = new TransferView();
        transfer.setTransferID(result.getInt("transfer_id"));
        transfer.setUserFrom(result.getString("user_from"));
        transfer.setUserTo(result.getString("user_to"));
        transfer.setStatus(result.getString("transfer_status_desc"));
        transfer.setType(result.getString("transfer_type_desc"));
        transfer.setAmount(result.getBigDecimal("amount"));
        return transfer;
    }
}
