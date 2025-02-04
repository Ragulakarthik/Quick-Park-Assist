package com.quick_park_assist.entity;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.enums.PaymentMethod;

import lombok.Data;

@Entity
@Data
@Table(name = "booking_spot")
public class BookingSpot {
// Do not change the Variable names it can affect the query implementation in the
	// BookingRepository. If change is necessary make appropriate changes in the repository.
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id",nullable = false)
	private Long bookingId;
	public ParkingSpot getSpot() {
		return spot;
	}

	public void setSpot(ParkingSpot spot) {
		this.spot = spot;
	}

	@Transient
	private ParkingSpot spot;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	User user;
	@ManyToOne
	@JoinColumn(name = "spotId",nullable = false)
	ParkingSpot spotID;

	@Column
	private String spotLocation; // Ensure this field exists
	@Column(name = "mobile_number")
	private String mobileNumber;
	private Double duration;

    @Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date startTime;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date endTime;

	private Double estimatedPrice;
	@Enumerated(EnumType.STRING) // Stores enum as a String (e.g., "CREDIT_CARD")
	@Column(name = "payment_method", nullable = true)
	private PaymentMethod paymentMethod;
	@Enumerated(EnumType.STRING)
	@Column(name = "booking_status")// Stores enum as a String (e.g., "BOOKED"
	private BookingSpotStatus bookingSpotStatus = BookingSpotStatus.CONFIRMED;


	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Long getBookingId() {
		return bookingId;
	}
	public ParkingSpot getSpotId() {
		return spotID;
	}

	public String	 getSpotLocation() {
		return spotLocation;
	}

	public void setSpotLocation(String spotLocation) {
		this.spotLocation = spotLocation;
	}

	public void setSpotId(ParkingSpot parkingSpot) {
		this.spotID = parkingSpot;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

    public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Double getEstimatedPrice() {
		return estimatedPrice;
	}

	public void setEstimatedPrice(Double estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BookingSpotStatus getBookingSpotStatus() {
		return bookingSpotStatus;
	}

	public void setBookingSpotStatus(BookingSpotStatus bookingSpotStatus) {
		this.bookingSpotStatus = bookingSpotStatus;
	}
}

//create table booking_spot (
//        booking_id bigint not null auto_increment,
//        booking_spot_status tinyint check (booking_spot_status between 0 and 3),
//        duration float(53),
//        estimated_price float(53),
//        mobile_number varchar(255),
//        payment_method tinyint check (payment_method between 0 and 2),
//        spotid bigint,
//        start_time datetime(6),
//        userid bigint,
//        primary key (booking_id)
//    )