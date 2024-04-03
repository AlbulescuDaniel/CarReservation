package com.car.reservation.service;

import com.car.reservation.common.exceptions.BadRequestException;
import com.car.reservation.common.exceptions.NotFoundException;
import com.car.reservation.model.Car;
import com.car.reservation.model.CarReservation;
import com.car.reservation.repository.CarRepository;
import com.car.reservation.repository.CarReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarReservationServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarReservationRepository carReservationRepository;

    @InjectMocks
    private CarReservationService carReservationService;


    @Test
    void getCarReservations() {
        List<CarReservation> carReservations = new ArrayList<>();
        carReservations.add(new CarReservation());
        carReservations.add(new CarReservation());

        when(carReservationRepository.findAll()).thenReturn(carReservations);

        List<CarReservation> result = carReservationService.getCarReservations();

        assertEquals(2, result.size());
    }

    @Test
    void createCarReservation_Success() {
        CarReservation carReservation = new CarReservation();
        carReservation.setReservationTime(Instant.now());
        carReservation.setDurationHours(2);
        Car car = new Car(1L, "Golf", "7 R Line", "C5595");

        when(carRepository.findFirstAvailableCar(any(), any())).thenReturn(List.of(car));
        when(carReservationRepository.save(any())).thenReturn(carReservation);

        CarReservation result = carReservationService.createCarReservation(carReservation);

        assertNotNull(result);
        assertEquals(car, result.getCar());
    }

    @Test
    void createCarReservation_NoAvailableCar() {
        CarReservation carReservation = new CarReservation();
        carReservation.setReservationTime(Instant.now());
        carReservation.setDurationHours(2);

        when(carRepository.findFirstAvailableCar(any(), any())).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> carReservationService.createCarReservation(carReservation));
    }

    @Test
    void createCarReservation_ExceedDurationLimit() {
        CarReservation carReservation = new CarReservation();
        carReservation.setDurationHours(3);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carReservationService.createCarReservation(carReservation));

        assertEquals("Reservations must not exceed a duration of 2 hours.", exception.getMessage());
    }

    @Test
    void createCarReservation_AdvanceReservationLimit() {
        CarReservation carReservation = new CarReservation();
        carReservation.setReservationTime(Instant.now().plus(Period.ofDays(2)));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carReservationService.createCarReservation(carReservation));

        assertEquals("Reservations cannot be made more than 24 hours in advance.", exception.getMessage());
    }
}
