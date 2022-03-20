package com.techelevator.tenmo.dao.security;

import com.techelevator.tenmo.model.security.User;

public interface UserSecurityDao {

    User findByUsername(String username);

    boolean create(String username, String password);
}
