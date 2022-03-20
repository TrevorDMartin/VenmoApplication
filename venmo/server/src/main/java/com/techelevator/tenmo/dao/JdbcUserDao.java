package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.security.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            throw new UsernameNotFoundException("User " + username + " was not found.");
        }
    }

    @Override
    public List<User> findAllButMe(int id) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username FROM tenmo_user WHERE user_id != ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()) {
            User user = new User();
            user.setId(results.getLong("user_id"));
            user.setUsername(results.getString("username"));
            users.add(user);
        }
        return users;
    }
}
