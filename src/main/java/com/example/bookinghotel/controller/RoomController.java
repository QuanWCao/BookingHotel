package com.example.bookinghotel.controller;

import com.example.bookinghotel.exception.PhotoRetrievalException;
import com.example.bookinghotel.model.BookingRoom;
import com.example.bookinghotel.model.Room;
import com.example.bookinghotel.response.BookingRoomResponse;
import com.example.bookinghotel.response.RoomResponse;
import com.example.bookinghotel.service.IBookingRoomService;
import com.example.bookinghotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tomcat.util.codec.binary.Base64;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;
    private final IBookingRoomService bookingRoomService;



    @PostMapping("/add/new_rooms")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,@RequestParam("roomType") String roomType, @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        Room newRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = new RoomResponse(newRoom.getId(), newRoom.getRoomType(), newRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    };

    @GetMapping("/rooms/types")
    public List<String> getRoomType() {
        return roomService.getAllRoomType();
    }

    @GetMapping("/rooms/all")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for(Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotobyRoomId(room.getId());
            if(photoBytes != null && photoBytes.length >0 ){
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomRespone(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    };
@DeleteMapping("delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private RoomResponse getRoomRespone(Room room) {
         List<BookingRoom> bookings = getAllBookingsByRoomId(room.getId());
         List<BookingRoomResponse> bookingInfo = bookings.stream().map(booking -> new BookingRoomResponse(booking.getBookingId(), booking.getCheckInDate(), booking.getCheckOutDate(),booking.getBookingConfirmationCode())).toList();
        byte[] photoBytes = null;
        Blob photoBlog =  room.getPhoto();
        if(photoBlog != null) {
            try {
                photoBytes= photoBlog.getBytes(1, (int) photoBlog.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(),room.getRoomPrice(),room.isBooked(), photoBytes , bookingInfo);
    }

    private List<BookingRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRoomService.getAllBookingsByRoomId(roomId);
    };


}
