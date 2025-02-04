package com.quick_park_assist.dto;

public class ParkingSpotUpdateDTO{
    private Long id; // ID of the spot to be updated
    private String spotType; // EVSpot, indoor, outdoor, underground
    private String availability; // Available, Unavailable
    private String additionalInstructions;
    private Double pricePerHour;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getSpotType() {
        return spotType;
    }

    public void setSpotType(String spotType) {
        this.spotType = spotType;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    @Override
    public String toString() {
        return "ParkingSpotUpdateDTO{" +
                "id=" + id +
                ", spotType='" + spotType + '\'' +
                ", availability='" + availability + '\'' +
                ", additionalInstructions='" + additionalInstructions + '\'' +
                ", pricePerHour=" + pricePerHour +
                '}';
    }
}

