package com.quick_park_assist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VehicleDTO {
    @NotBlank(message = "Vehicle number is required")
    @Pattern(regexp = "^[A-Z]{2}-\\d{2}-[A-Z]{2}-\\d{4}$",
            message = "Vehicle number must be in format: XX-99-XX-9999")
    private String vehicleNumber;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Color is required")
    private String color;
    private boolean ev;

    // Getters and Setters
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public boolean isEv() {
        return ev;
    }

    public void setEv(boolean ev) {
        this.ev = ev;
    }
}