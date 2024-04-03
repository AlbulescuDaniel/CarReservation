package com.car.reservation.service;

import com.car.reservation.common.exceptions.BadRequestException;
import com.car.reservation.common.exceptions.NotFoundException;
import com.car.reservation.model.Car;
import com.car.reservation.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;


    @Test
    void getAllCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car(1L, "Toyota", "Camry", "C123"));

        when(carRepository.findAll()).thenReturn(cars);

        List<Car> result = carService.getAllCars();

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getMake());
        assertEquals("Camry", result.get(0).getModel());
        assertEquals("C123", result.get(0).getIdentifier());
    }

    @Test
    void createCar() {
        Car car = new Car(null, "Golf", "7 RLine", "C123");

        when(carRepository.save(car)).thenReturn(car);

        Car result = carService.createCar(car);

        assertNotNull(result);
        assertEquals("Golf", result.getMake());
        assertEquals("7 RLine", result.getModel());
        assertEquals("C123", result.getIdentifier());
    }

    @Test
    void createCar_alreadyExists() {
        Car car = new Car(null, "Toyota", "Camry", "C123");

        when(carRepository.save(car)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(BadRequestException.class, () -> carService.createCar(car));
    }

    @Test
    void updateCar() {
        Car car = new Car(1L, "Toyota", "Camry", "C123");

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);

        Car result = carService.updateCar(1L, car);

        assertNotNull(result);
        assertEquals("Toyota", result.getMake());
        assertEquals("Camry", result.getModel());
        assertEquals("C123", result.getIdentifier());
    }

    @Test
    void updateCar_notFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carService.updateCar(1L, new Car()));
    }

    @Test
    void deleteCar() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(new Car()));

        carService.deleteCar(1L);

        verify(carRepository).deleteById(1L);
    }

    @Test
    void deleteCar_notFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carService.deleteCar(1L));

        verify(carRepository, never()).deleteById(1L);

    }
}
