package com.example.bookinghotel.service;

import com.example.bookinghotel.exception.InvalidBookingRequestException;
import com.example.bookinghotel.exception.ResourceNotFoundException;
import com.example.bookinghotel.model.BookingRoom;
import com.example.bookinghotel.model.Room;
import com.example.bookinghotel.repository.BookingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Service
@RequiredArgsConstructor
public class BookingRoomService implements IBookingRoomService{

    private final BookingRoomRepository bookingRoomRepository;
    private final IRoomService roomService;


    @Override
    public List<BookingRoom> getAllBookings() {
        return bookingRoomRepository.findAll();
    }


    @Override
    public List<BookingRoom> getBookingsByUserEmail(String email) {
        return bookingRoomRepository.findByGuestEmail(email);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRoomRepository.deleteById(bookingId);
    }

    @Override
    public List<BookingRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRoomRepository.findByRoomId(roomId);
    }

    @Override
    public String saveBooking(Long roomId, BookingRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookingRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest,existingBookings);
        if (roomIsAvailable){
            room.addRoom(bookingRequest);
            bookingRoomRepository.save(bookingRequest);
        }else{
            throw  new InvalidBookingRequestException("Sorry, This room is not available for the selected dates;");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public BookingRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRoomRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code :"+confirmationCode));

    }


    private boolean roomIsAvailable(BookingRoom bookingRequest, List<BookingRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}
