package com.example.bookinghotel.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.bookinghotel.model.Room;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomService {

    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws SQLException, IOException;

    List<String> getAllRoomType();

    List<Room> getAllRooms();

    byte[] getRoomPhotobyRoomId(Long roomId) throws SQLException;

    void deleteRoom(Long id);

    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes);

    Optional<Room> getRoomById(Long roomId);

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
