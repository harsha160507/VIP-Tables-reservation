package com.viptables.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.viptables.model.Restaurant;

@Repository
public class RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;

    public RestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Restaurant> findAll() {
        return jdbcTemplate.query(
                """
                SELECT id, name, description, address, cuisine, image_url, created_at
                FROM restaurants
                ORDER BY name
                """,
                this::mapRow);
    }

    public Optional<Restaurant> findById(long id) {
        return jdbcTemplate.query(
                        """
                        SELECT id, name, description, address, cuisine, image_url, created_at
                        FROM restaurants
                        WHERE id = ?
                        """,
                        this::mapRow,
                        id)
                .stream()
                .findFirst();
    }

    public Restaurant insert(String name, String description, String address, String cuisine, String imageUrl) {
        jdbcTemplate.update(
                """
                INSERT INTO restaurants (name, description, address, cuisine, image_url)
                VALUES (?, ?, ?, ?, ?)
                """,
                name, description, address, cuisine, imageUrl);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        if (id == null) {
            throw new IllegalStateException("Database did not return the new restaurant id");
        }
        return findById(id).orElseThrow();
    }

    public Restaurant update(
            long id,
            String name,
            String description,
            String address,
            String cuisine,
            String imageUrl) {
        int updated = jdbcTemplate.update(
                """
                UPDATE restaurants
                SET name = ?, description = ?, address = ?, cuisine = ?, image_url = ?
                WHERE id = ?
                """,
                name, description, address, cuisine, imageUrl, id);
        if (updated == 0) {
            return null;
        }
        return findById(id).orElseThrow();
    }

    public boolean deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM restaurants WHERE id = ?", id) == 1;
    }

    private Restaurant mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Restaurant(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("address"),
                resultSet.getString("cuisine"),
                resultSet.getString("image_url"),
                resultSet.getTimestamp("created_at").toInstant());
    }
}
