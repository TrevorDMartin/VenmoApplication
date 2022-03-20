package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.security.User;

import java.util.List;

public interface UserDao {

    List<User> findAllButMe(int id);

    int findIdByUsername(String username);
}
