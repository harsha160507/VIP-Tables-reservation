package com.viptables.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.viptables.model.RestaurantTable;

@Repository
public class RestaurantTableRepository {

    private final JdbcTemplate jdbcTemplate;

    public RestaurantTableRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RestaurantTable> findActiveByRestaurantId(long restaurantId) {
        return jdbcTemplate.query(
                """
                SELECT id, restaurant_id, table_number, capacity, active
                FROM restaurant_tables
                WHERE restaurant_id = ?
                ORDER BY table_number
                """,
                this::mapRow,
                restaurantId)
                .stream()
                .filter(RestaurantTable::active)
                .toList();
    }

    public Optional<RestaurantTable> findActiveByIdForUpdate(long id) {
        return jdbcTemplate.query(
                        """
                        SELECT id, restaurant_id, table_number, capacity, active
                        FROM restaurant_tables
                        WHERE id = ?
                        FOR UPDATE
                        """,
                        this::mapRow,
                        id)
                .stream()
                .filter(RestaurantTable::active)
                .findFirst();
    }

    public List<RestaurantTable> findAllByRestaurantId(long restaurantId) {
        return jdbcTemplate.query(
                """
                SELECT id, restaurant_id, table_number, capacity, active
                FROM restaurant_tables
                WHERE restaurant_id = ?
                ORDER BY table_number
                """,
                this::mapRow,
                restaurantId);
    }

    public RestaurantTable insert(long restaurantId, String tableNumber, int capacity) {
        jdbcTemplate.update(
                """
                INSERT INTO restaurant_tables (restaurant_id, table_number, capacity, active)
                VALUES (?, ?, ?, TRUE)
                """,
                restaurantId, tableNumber, capacity);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        if (id == null) {
            throw new IllegalStateException("Database did not return the new table id");
        }
        return findById(id).orElseThrow();
    }

    public Optional<RestaurantTable> findById(long id) {
        return jdbcTemplate.query(
                        """
                        SELECT id, restaurant_id, table_number, capacity, active
                        FROM restaurant_tables
                        WHERE id = ?
                        """,
                        this::mapRow,
                        id)
                .stream()
                .findFirst();
    }

    public RestaurantTable update(long id, String tableNumber, int capacity) {
        int updated = jdbcTemplate.update(
                """
                UPDATE restaurant_tables
                SET table_number = ?, capacity = ?
                WHERE id = ?
                """,
                tableNumber, capacity, id);
        if (updated == 0) {
            return null;
        }
        return findById(id).orElseThrow();
    }

    public boolean setActive(long id, boolean active) {
        return jdbcTemplate.update(
                "UPDATE restaurant_tables SET active = ? WHERE id = ?",
                active, id) == 1;
    }

    private RestaurantTable mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new RestaurantTable(
                resultSet.getLong("id"),
                resultSet.getLong("restaurant_id"),
                resultSet.getString("table_number"),
                resultSet.getInt("capacity"),
                resultSet.getBoolean("active"));
    }
}
