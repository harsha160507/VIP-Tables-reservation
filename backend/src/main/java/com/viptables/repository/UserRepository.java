package com.viptables.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.viptables.model.User;
import com.viptables.model.UserRole;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findById(long id) {
        return jdbcTemplate.query(
                        """
                        SELECT id, full_name, email, password_hash, role, created_at
                        FROM users
                        WHERE id = ?
                        """,
                        this::mapRow,
                        id)
                .stream()
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query(
                        """
                        SELECT id, full_name, email, password_hash, role, created_at
                        FROM users
                        WHERE email = ?
                        """,
                        this::mapRow,
                        email)
                .stream()
                .findFirst();
    }

    public boolean existsCustomerById(long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ? AND role = 'CUSTOMER'",
                Integer.class,
                id);
        return count != null && count > 0;
    }

    public User insertCustomer(String fullName, String email, String passwordHash) {
        jdbcTemplate.update(
                """
                INSERT INTO users (full_name, email, password_hash, role)
                VALUES (?, ?, ?, 'CUSTOMER')
                """,
                fullName,
                email,
                passwordHash);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        if (id == null) {
            throw new IllegalStateException("Database did not return the new user id");
        }
        return findById(id).orElseThrow();
    }

    private User mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                UserRole.valueOf(resultSet.getString("role")),
                resultSet.getTimestamp("created_at").toInstant());
    }
}
