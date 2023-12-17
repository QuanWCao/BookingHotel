package com.example.bookinghotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked =false;
    @Lob
    private Blob photo;
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookingRoom> bookings;
    public Room() {
        this.bookings = new ArrayList<>();
    }

    public void addRoom(BookingRoom bookingRoom) {
        if(bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(bookingRoom);
        bookingRoom.setRoom(this);
        isBooked = true;
        String bookingCode = RandomStringUtils.randomNumeric(10);
        bookingRoom.setBookingConfirmationCode(bookingCode);
    }
}
