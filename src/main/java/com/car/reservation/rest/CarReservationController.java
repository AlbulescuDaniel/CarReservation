package com.car.reservation.rest;

import com.car.reservation.commons.model.CarReservationDto;
import com.car.reservation.commons.ports.application.ReservationApi;
import com.car.reservation.service.CarReservationService;
import com.car.reservation.service.mapper.CarReservationMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class CarReservationController implements ReservationApi {

    private final CarReservationService carReservationService;
    private final CarReservationMapper carReservationMapper;


    @Override
    public ResponseEntity<CarReservationDto> createCarReservation(CarReservationDto carReservationDto) {
        var carReservation = carReservationService.createCarReservation(carReservationMapper.fromDto(carReservationDto));
        return new ResponseEntity<>(carReservationMapper.toDto(carReservation), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<CarReservationDto>> getCarReservations() {
        return new ResponseEntity<>(carReservationMapper.toDtoList(carReservationService.getCarReservations()), HttpStatus.OK);
    }
}
