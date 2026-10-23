package com.viptables.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.viptables.model.Reservation;
import com.viptables.model.ReservationStatus;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Reservation> findById(long id) {
        return jdbcTemplate.query(
                        """
                        SELECT id, customer_id, restaurant_table_id, reservation_date,
                               start_time, end_time, status, created_at
                        FROM reservations
                        WHERE id = ?
                        """,
                        this::mapRow,
                        id)
                .stream()
                .findFirst();
    }

    public List<Reservation> findByCustomerId(long customerId) {
        return jdbcTemplate.query(
                """
                SELECT id, customer_id, restaurant_table_id, reservation_date,
                       start_time, end_time, status, created_at
                FROM reservations
                WHERE customer_id = ?
                ORDER BY reservation_date DESC, start_time DESC
                """,
                this::mapRow,
                customerId);
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query(
                """
                SELECT id, customer_id, restaurant_table_id, reservation_date,
                       start_time, end_time, status, created_at
                FROM reservations
                ORDER BY reservation_date DESC, start_time DESC
                """,
                this::mapRow);
    }

    public long countByStatus(String status) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reservations WHERE status = ?",
                Long.class,
                status);
        return count == null ? 0 : count;
    }

    public boolean updateStatus(long id, ReservationStatus status) {
        return jdbcTemplate.update(
                "UPDATE reservations SET status = ? WHERE id = ?",
                status.name(),
                id) == 1;
    }

    public boolean hasOverlappingConfirmedReservation(
            long tableId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime) {
        return hasOverlappingConfirmedReservation(
                tableId, reservationDate, startTime, endTime, null);
    }

    public boolean hasOverlappingConfirmedReservationExcept(
            long reservationId,
            long tableId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime) {
        return hasOverlappingConfirmedReservation(
                tableId, reservationDate, startTime, endTime, reservationId);
    }

    private boolean hasOverlappingConfirmedReservation(
            long tableId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            Long excludedReservationId) {
        String exclusion = excludedReservationId == null ? "" : " AND id <> ?";
        Object[] parameters = excludedReservationId == null
                ? new Object[] { tableId, reservationDate, endTime, startTime }
                : new Object[] { tableId, reservationDate, endTime, startTime, excludedReservationId };
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM reservations
                WHERE restaurant_table_id = ?
                  AND reservation_date = ?
                  AND status = 'CONFIRMED'
                  AND start_time < ?
                  AND end_time > ?
                """ + exclusion,
                Integer.class,
                parameters);
        return count != null && count > 0;
    }

    public Reservation insert(
            long customerId,
            long tableId,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime) {
        jdbcTemplate.update(
                """
                INSERT INTO reservations (
                    customer_id, restaurant_table_id, reservation_date,
                    start_time, end_time, status
                ) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')
                """,
                customerId,
                tableId,
                reservationDate,
                startTime,
                endTime);

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        if (id == null) {
            throw new IllegalStateException("Database did not return the new reservation id");
        }
        return findById(id).orElseThrow(
                () -> new IllegalStateException("New reservation could not be loaded"));
    }

    public boolean cancel(long reservationId) {
        return jdbcTemplate.update(
                """
                UPDATE reservations
                SET status = 'CANCELLED'
                WHERE id = ? AND status = 'CONFIRMED'
                """,
                reservationId) == 1;
    }

    private Reservation mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Reservation(
                resultSet.getLong("id"),
                resultSet.getLong("customer_id"),
                resultSet.getLong("restaurant_table_id"),
                resultSet.getDate("reservation_date").toLocalDate(),
                resultSet.getTime("start_time").toLocalTime(),
                resultSet.getTime("end_time").toLocalTime(),
                ReservationStatus.valueOf(resultSet.getString("status")),
                resultSet.getTimestamp("created_at").toInstant());
    }
}
