package com.example.bookinghotel.controller;
import com.example.bookinghotel.exception.InvalidBookingRequestException;
import com.example.bookinghotel.exception.ResourceNotFoundException;
import com.example.bookinghotel.model.BookingRoom;
import com.example.bookinghotel.model.Room;
import com.example.bookinghotel.response.BookingRoomResponse;
import com.example.bookinghotel.response.RoomResponse;
import com.example.bookinghotel.service.IBookingRoomService;
import com.example.bookinghotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingRoomController {
    private final IBookingRoomService bookingRoomService;
    private final IRoomService roomService;

    @GetMapping("/all-bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingRoomResponse>> getAllBookings(){
        List<BookingRoom> bookings = bookingRoomService.getAllBookings();
        List<BookingRoomResponse> bookingResponses = new ArrayList<>();
        for (BookingRoom booking : bookings){
            BookingRoomResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookingRoom bookingRequest){
        try{
            String confirmationCode = bookingRoomService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok(
                    "Room booked successfully, Your booking confirmation code is :"+confirmationCode);

        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookingRoom booking = bookingRoomService.findByBookingConfirmationCode(confirmationCode);
            BookingRoomResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingRoomResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<BookingRoom> bookings = bookingRoomService.getBookingsByUserEmail(email);
        List<BookingRoomResponse> bookingResponses = new ArrayList<>();
        for (BookingRoom booking : bookings) {
            BookingRoomResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingRoomService.cancelBooking(bookingId);
    }

    private BookingRoomResponse getBookingResponse(BookingRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice());
        return new BookingRoomResponse(
                booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getGuestFullName(),
                booking.getGuestEmail(), booking.getNumOfAdults(),
                booking.getNumOfChildren(), booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), room);
    }
}
