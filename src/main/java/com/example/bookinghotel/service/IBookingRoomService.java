package com.example.bookinghotel.service;

import com.example.bookinghotel.model.BookingRoom;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IBookingRoomService {
    void cancelBooking(Long bookingId);

    List<BookingRoom> getAllBookingsByRoomId(Long roomId);

    String saveBooking(Long roomId, BookingRoom bookingRequest);

    BookingRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookingRoom> getAllBookings();

    List<BookingRoom> getBookingsByUserEmail(String email);
}
