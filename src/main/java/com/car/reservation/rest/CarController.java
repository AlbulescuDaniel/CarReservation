package com.car.reservation.rest;

import com.car.reservation.commons.model.CarDto;
import com.car.reservation.commons.ports.application.CarApi;
import com.car.reservation.service.CarService;
import com.car.reservation.service.mapper.CarMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class CarController implements CarApi {

    private final CarService carService;
    private final CarMapper carMapper;


    @Override
    public ResponseEntity<List<CarDto>> getCars() {
        var carDtoList = carMapper.toDtoList(carService.getAllCars());
        return new ResponseEntity<>(carDtoList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CarDto> createCar(CarDto carDto) {
        var newCar = carService.createCar(carMapper.fromDto(carDto));
        return new ResponseEntity<>(carMapper.toDto(newCar), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<CarDto> updateCar(Long carId, CarDto carDto) {
        var updatedCar = carService.updateCar(carId, carMapper.fromDto(carDto));
        return new ResponseEntity<>(carMapper.toDto(updatedCar), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteCar(Long carId) {
        carService.deleteCar(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
