	package com.quick_park_assist.serviceImpl;

	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;

	import com.quick_park_assist.entity.BookingSpot;
	import com.quick_park_assist.repository.BookingSpotRepository;
	import com.quick_park_assist.service.IBookingSpotService;

	import java.util.Date;
	import java.util.Optional;

	@Service
	public class BookingSpotServiceImpl implements IBookingSpotService {

		@Autowired
		private BookingSpotRepository bookingSpotRepository;

		@Override
		public void saveBookingSpot(BookingSpot bookingSpot) {
			bookingSpotRepository.save(bookingSpot);
		}

		@Override
		public boolean checkIfPreviouslyBooked(Long userId, Long spotId, Date startTime) {
			// this will get the
			Optional<BookingSpot> bookedSpot=  bookingSpotRepository.findTopLastBookingSpotByUserIdAndSpotId(userId,spotId);

			if(bookedSpot.isPresent()){
				BookingSpot bookingSpot = bookedSpot.get();
				Date endTime = bookingSpot.getEndTime();
				if(startTime.after(endTime)){
					return true;
				}
				else return false;
			}else{
				return true;
			}
		}
	}