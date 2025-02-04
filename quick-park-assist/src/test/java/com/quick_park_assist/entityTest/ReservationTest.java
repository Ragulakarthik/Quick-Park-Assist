package com.quick_park_assist.entityTest;


import com.quick_park_assist.entity.Reservation;
import com.quick_park_assist.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    @Test
    void testIdGetterAndSetter() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        assertThat(reservation.getId()).isEqualTo(1L);
    }

    @Test
    void testNameGetterAndSetter() {
        Reservation reservation = new Reservation();
        reservation.setName("John Doe");

        assertThat(reservation.getName()).isEqualTo("John Doe");
    }

    @Test
    void testVehicleNumberGetterAndSetter() {
        Reservation reservation = new Reservation();
        reservation.setVehicleNumber("AB123CD");

        assertThat(reservation.getVehicleNumber()).isEqualTo("AB123CD");
    }

    @Test
    void testChargingStationGetterAndSetter() {
        Reservation reservation = new Reservation();
        reservation.setChargingStation("Station A");

        assertThat(reservation.getChargingStation()).isEqualTo("Station A");
    }

    @Test
    void testUserGetterAndSetter() {
        Reservation reservation = new Reservation();
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");

        reservation.setUser(user);

        assertThat(reservation.getUser()).isNotNull();
        assertThat(reservation.getUser().getId()).isEqualTo(1L);
        assertThat(reservation.getUser().getFullName()).isEqualTo("Test User");
    }

    @Test
    void testSlotGetterAndSetter() {
        Reservation reservation = new Reservation();
        reservation.setSlot("A1");

        assertThat(reservation.getSlot()).isEqualTo("A1");
    }

    @Test
    void testStatusGetterAndSetter() {
        Reservation reservation = new Reservation();
        reservation.setStatus("Active");

        assertThat(reservation.getStatus()).isEqualTo("Active");
    }

    @Test
    void testReservationTimeGetterAndSetter() {
        Reservation reservation = new Reservation();
        Date now = new Date();
        reservation.setReservationTime(now);

        assertThat(reservation.getReservationTime()).isEqualTo(now);
    }

    @Test
    void testFullEntity() {
        // Create a user for testing
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");

        // Set all fields
        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setName("John Doe");
        reservation.setVehicleNumber("AB123CD");
        reservation.setChargingStation("Station A");
        reservation.setUser(user);
        reservation.setSlot("A1");
        reservation.setStatus("Active");
        Date now = new Date();
        reservation.setReservationTime(now);

        // Assert all fields
        assertThat(reservation.getId()).isEqualTo(100L);
        assertThat(reservation.getName()).isEqualTo("John Doe");
        assertThat(reservation.getVehicleNumber()).isEqualTo("AB123CD");
        assertThat(reservation.getChargingStation()).isEqualTo("Station A");
        assertThat(reservation.getUser()).isNotNull();
        assertThat(reservation.getUser().getId()).isEqualTo(1L);
        assertThat(reservation.getUser().getFullName()).isEqualTo("Test User");
        assertThat(reservation.getSlot()).isEqualTo("A1");
        assertThat(reservation.getStatus()).isEqualTo("Active");
        assertThat(reservation.getReservationTime()).isEqualTo(now);
    }
}
