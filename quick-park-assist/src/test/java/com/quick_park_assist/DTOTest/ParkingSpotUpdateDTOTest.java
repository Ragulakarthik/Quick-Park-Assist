package com.quick_park_assist.DTOTest;
import com.quick_park_assist.dto.ParkingSpotUpdateDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotUpdateDTOTest {

    @Test
    void testGettersAndSetters() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();

        dto.setId(1L);
        dto.setSpotType("EVSpot");
        dto.setAvailability("Available");
        dto.setAdditionalInstructions("Near the entrance");
        dto.setPricePerHour(5.50);

        assertEquals(1L, dto.getId());
        assertEquals("EVSpot", dto.getSpotType());
        assertEquals("Available", dto.getAvailability());
        assertEquals("Near the entrance", dto.getAdditionalInstructions());
        assertEquals(5.50, dto.getPricePerHour());
    }

    @Test
    void testAllFieldsInitialization() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();
        dto.setId(10L);
        dto.setSpotType("Indoor");
        dto.setAvailability("Unavailable");
        dto.setAdditionalInstructions("Close to exit gate");
        dto.setPricePerHour(10.0);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Indoor", dto.getSpotType());
        assertEquals("Unavailable", dto.getAvailability());
        assertEquals("Close to exit gate", dto.getAdditionalInstructions());
        assertEquals(10.0, dto.getPricePerHour());
    }

    @Test
    void testSettersForNullValues() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();

        dto.setId(null);
        dto.setSpotType(null);
        dto.setAvailability(null);
        dto.setAdditionalInstructions(null);
        dto.setPricePerHour(null);

        assertNull(dto.getId());
        assertNull(dto.getSpotType());
        assertNull(dto.getAvailability());
        assertNull(dto.getAdditionalInstructions());
        assertNull(dto.getPricePerHour());
    }

    @Test
    void testPricePerHourWithDecimals() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();
        dto.setPricePerHour(9.99);

        assertEquals(9.99, dto.getPricePerHour());
    }

    @Test
    void testSpotTypeValues() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();
        dto.setSpotType("Outdoor");

        assertEquals("Outdoor", dto.getSpotType());
    }

    @Test
    void testAdditionalInstructionsAreSet() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();
        dto.setAdditionalInstructions("Park near the lobby");

        assertEquals("Park near the lobby", dto.getAdditionalInstructions());
    }

    @Test
    void testEqualsAndHashCode() {
        ParkingSpotUpdateDTO dto1 = new ParkingSpotUpdateDTO();
        dto1.setId(1L);
        dto1.setSpotType("Indoor");
        dto1.setAvailability("Available");
        dto1.setAdditionalInstructions("Near entrance");
        dto1.setPricePerHour(15.0);

        ParkingSpotUpdateDTO dto2 = dto1;
        dto2.setId(1L);
        dto2.setSpotType("Indoor");
        dto2.setAvailability("Available");
        dto2.setAdditionalInstructions("Near entrance");
        dto2.setPricePerHour(15.0);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        ParkingSpotUpdateDTO dto1 = new ParkingSpotUpdateDTO();
        dto1.setId(1L);
        dto1.setSpotType("EVSpot");

        ParkingSpotUpdateDTO dto2 = new ParkingSpotUpdateDTO();
        dto2.setId(2L);
        dto2.setSpotType("Outdoor");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString() {
        ParkingSpotUpdateDTO dto = new ParkingSpotUpdateDTO();
        dto.setId(1L);
        dto.setSpotType("Underground");
        dto.setAvailability("Available");
        dto.setAdditionalInstructions("Level -1");
        dto.setPricePerHour(12.0);

        String expected = "ParkingSpotUpdateDTO{id=1, spotType='Underground', availability='Available', additionalInstructions='Level -1', pricePerHour=12.0}";
        assertEquals(expected, dto.toString());
    }
}
