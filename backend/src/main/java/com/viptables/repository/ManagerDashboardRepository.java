package com.viptables.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.viptables.dto.ManagerDashboardResponse;

@Repository
public class ManagerDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public ManagerDashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ManagerDashboardResponse getStatistics() {
        return new ManagerDashboardResponse(
                count("SELECT COUNT(*) FROM restaurants"),
                count("SELECT COUNT(*) FROM restaurant_tables WHERE active = 1"),
                count("SELECT COUNT(*) FROM reservations WHERE status = 'CONFIRMED'"),
                count("SELECT COUNT(*) FROM reservations WHERE status = 'CANCELLED'"));
    }

    private long count(String sql) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class);
        return result == null ? 0 : result;
    }
}
