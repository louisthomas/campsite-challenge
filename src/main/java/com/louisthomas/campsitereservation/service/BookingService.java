package com.louisthomas.campsitereservation.service;

import com.louisthomas.campsitereservation.common.exception.NotFoundBookingException;
import com.louisthomas.campsitereservation.controller.BookingDto;
import com.louisthomas.campsitereservation.model.Booking;
import com.louisthomas.campsitereservation.repository.BookingRepository;
import com.louisthomas.campsitereservation.controller.BookingRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;

    private ModelMapper modelMapper;

    public BookingService(BookingRepository bookingRepository, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingDto createBooking(BookingRequest bookingRequest) {
        Booking booking = bookingRepository.save(modelMapper.map(bookingRequest, Booking.class));
        return modelMapper.map(booking, BookingDto.class);
    }

    @Transactional(readOnly = true)
    public Booking findBookingById(UUID id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElseThrow(() -> new NotFoundBookingException(id));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingDto updateBooking(UUID id, BookingRequest bookingRequest) {
        bookingRepository.findById(id).orElseThrow(() -> new NotFoundBookingException(id));
        Booking booking = bookingRepository.save(modelMapper.map(bookingRequest, Booking.class));
        return modelMapper.map(booking, BookingDto.class);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelBooking(UUID id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundBookingException(id));
        bookingRepository.delete(booking);
        log.info("Deleted booking with id: {}", id  );
    }
}
