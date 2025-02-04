package com.quick_park_assist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_spot")
public class ParkingSpot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(nullable = false)
	private String location;// Corrected: Represents the parking location
	@Column(name = "spot_location",nullable = false)
	private String spotLocation;   // Specifies the exact spot
	@Column(nullable = false)
	private String availability;
	@Column(nullable = false)
	private double pricePerHour;
	@Column(nullable = false)
	private String spotType;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String additionalInstructions;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {  // Corrected setter name
		this.location = location;
	}

	public String getSpotLocation() {
		return spotLocation;
	}
	public User getUser() {return user;}

	public void setUser(User user) {this.user = user;}

	public void setSpotLocation(String spotLocation) {
		this.spotLocation = spotLocation;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public double getPricePerHour() {
		return pricePerHour;
	}

	public void setPricePerHour(double pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public String getSpotType() {
		return spotType;
	}

	public void setSpotType(String spotType) {
		this.spotType = spotType;
	}


	public String getAdditionalInstructions() {

		return additionalInstructions;
	}

	public void setAdditionalInstructions(String additionalInstructions) {

		this.additionalInstructions = additionalInstructions;
	}

	@Override
	public String toString() {
		return "ParkingSpot{" +
				"id=" + id +
				"'user_id'"+user+ '\''+
				", location='" + location + '\'' +
				", spotLocation='" + spotLocation + '\'' +
				", availability='" + availability + '\'' +
				", pricePerHour=" + pricePerHour +
				", spotType='" + spotType + '\'' +
				", additionalInstructions='" + additionalInstructions + '\'' +
				'}';
	}
}
