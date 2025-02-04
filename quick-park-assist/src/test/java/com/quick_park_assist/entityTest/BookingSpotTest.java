package com.quick_park_assist.entityTest;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BookingSpotTest {

    @Test
    void testValidBookingSpot() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setBookingId(1L);
        bookingSpot.setUser(new User());
        bookingSpot.setSpotId(new ParkingSpot());
        bookingSpot.setSpotLocation("Level 1, Zone A");
        bookingSpot.setMobileNumber("1234567890");
        bookingSpot.setDuration(2.5);
        bookingSpot.setStartTime(new Date());
        bookingSpot.setEndTime(new Date());
        bookingSpot.setEstimatedPrice(100.0);
        bookingSpot.setPaymentMethod(PaymentMethod.CREDIT);
        bookingSpot.setBookingSpotStatus(BookingSpotStatus.CONFIRMED);

        assertEquals(1L, bookingSpot.getBookingId());
        assertNotNull(bookingSpot.getUser());
        assertNotNull(bookingSpot.getSpotId());
        assertEquals("Level 1, Zone A", bookingSpot.getSpotLocation());
        assertEquals("1234567890", bookingSpot.getMobileNumber());
        assertEquals(2.5, bookingSpot.getDuration());
        assertNotNull(bookingSpot.getStartTime());
        assertNotNull(bookingSpot.getEndTime());
        assertEquals(100.0, bookingSpot.getEstimatedPrice());
        assertEquals(PaymentMethod.CREDIT, bookingSpot.getPaymentMethod());
        assertEquals(BookingSpotStatus.CONFIRMED, bookingSpot.getBookingSpotStatus());
    }

    @Test
    void testNullUser() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setUser(null);

        assertNull(bookingSpot.getUser());
    }

    @Test
    void testInvalidMobileNumber() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setMobileNumber("12345"); // Invalid number

        assertEquals("12345", bookingSpot.getMobileNumber());
    }

    @Test
    void testStartTimeNull() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(null);

        assertNull(bookingSpot.getStartTime());
    }

    @Test
    void testEndTimeBeforeStartTime() {
        Date startTime = new Date(System.currentTimeMillis() + 3600000); // 1 hour from now
        Date endTime = new Date(System.currentTimeMillis()); // Current time

        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(startTime);
        bookingSpot.setEndTime(endTime);

        assertTrue(bookingSpot.getEndTime().before(bookingSpot.getStartTime()));
    }

    @Test
    void testEstimatedPriceNegative() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setEstimatedPrice(-100.0); // Invalid negative price

        assertEquals(-100.0, bookingSpot.getEstimatedPrice());
    }

    @Test
    void testSpotLocation() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setSpotLocation("Level 1, Zone A");

        assertEquals("Level 1, Zone A", bookingSpot.getSpotLocation());
    }

    @Test
    void testDuration() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setDuration(3.0);

        assertEquals(3.0, bookingSpot.getDuration());
    }

    @Test
    void testPaymentMethod() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setPaymentMethod(PaymentMethod.CASH);

        assertEquals(PaymentMethod.CASH, bookingSpot.getPaymentMethod());
    }

    @Test
    void testBookingSpotStatus() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setBookingSpotStatus(BookingSpotStatus.PENDING);

        assertEquals(BookingSpotStatus.PENDING, bookingSpot.getBookingSpotStatus());
    }
}
