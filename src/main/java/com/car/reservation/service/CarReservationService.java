package com.car.reservation.service;

import com.car.reservation.common.exceptions.BadRequestException;
import com.car.reservation.common.exceptions.NotFoundException;
import com.car.reservation.model.CarReservation;
import com.car.reservation.repository.CarRepository;
import com.car.reservation.repository.CarReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

@AllArgsConstructor
@Service
public class CarReservationService {

    private final CarRepository carRepository;
    private final CarReservationRepository carReservationRepository;


    public List<CarReservation> getCarReservations() {
        return carReservationRepository.findAll();
    }

    public CarReservation createCarReservation(CarReservation carReservation) {
        validateCarReservation(carReservation);

        Instant startTime = carReservation.getReservationTime();
        Instant endTime = carReservation.getReservationTime().plus(carReservation.getDurationHours(), ChronoUnit.HOURS);

        return carRepository.findFirstAvailableCar(startTime, endTime)
                .stream().findFirst()
                .map(car -> {
                    carReservation.setCar(car);
                    carReservationRepository.save(carReservation);
                    return carReservation;
                })
                .orElseThrow(() -> new NotFoundException("There is no car available for this time."));
    }

    private void validateCarReservation(CarReservation carReservation) {
        final int LIMIT_OF_HOURS = 2;
        final int LIMIT_OF_DAYS = 1;

        if (carReservation.getDurationHours() > LIMIT_OF_HOURS) {
            throw new BadRequestException("Reservations must not exceed a duration of 2 hours.");
        }

        if (carReservation.getReservationTime().isAfter(Instant.now().plus(Period.ofDays(LIMIT_OF_DAYS)).minus(carReservation.getDurationHours(), ChronoUnit.HOURS))) {
            throw new BadRequestException("Reservations cannot be made more than 24 hours in advance.");
        }
    }
}
