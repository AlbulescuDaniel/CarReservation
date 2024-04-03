package com.car.reservation.service;

import com.car.reservation.common.exceptions.BadRequestException;
import com.car.reservation.common.exceptions.NotFoundException;
import com.car.reservation.model.Car;
import com.car.reservation.repository.CarRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CarService {

    private final CarRepository carRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car createCar(Car car) {
        try {
            return carRepository.save(car);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(String.format("The car with identifier %s already exists.", car.getIdentifier()));
        } catch (RuntimeException e) {
            throw new BadRequestException("Could not save the car.");
        }
    }

    public Car updateCar(Long carId, Car updatedCar) {
        var car = carRepository.findById(carId).orElseThrow(() -> new NotFoundException(String.format("Car with id %s does not exist.", carId)));

        car.setMake(updatedCar.getMake());
        car.setIdentifier(updatedCar.getIdentifier());
        car.setModel(updatedCar.getModel());

        return carRepository.save(car);
    }

    public void deleteCar(Long carId) {
        carRepository.findById(carId).orElseThrow(() -> new NotFoundException(String.format("Car with id %s does not exist.", carId)));
        carRepository.deleteById(carId);
    }
}
