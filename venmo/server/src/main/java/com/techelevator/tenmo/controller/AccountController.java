package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private final UserDao userDao;
    private final AccountDao accountDao;

    public AccountController (UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "account/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        return accountDao.getBalance(userId);
    }
}
