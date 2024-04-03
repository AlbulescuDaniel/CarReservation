package com.car.reservation.rest;

import com.car.reservation.common.exceptions.BadRequestException;
import com.car.reservation.common.exceptions.NotFoundException;
import com.car.reservation.common.exceptions.handler.RestErrorHandler;
import com.car.reservation.commons.model.ApiErrorDto;
import com.car.reservation.commons.model.CarDto;
import com.car.reservation.model.Car;
import com.car.reservation.service.CarService;
import com.car.reservation.service.mapper.CarMapper;
import com.car.reservation.service.mapper.CarMapperImpl;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    public static final String TEST_ERROR_MESSAGE = "test error message";

    @Mock
    private CarService carService;

    @Spy
    private CarMapper carMapper = new CarMapperImpl();

    @InjectMocks
    private CarController carController;

    private MockMvcRequestSpecification givenController() {
        return given().standaloneSetup(carController, new RestErrorHandler());
    }

    @Test
    void getCars() {
        var car1 = new Car(1L, "Toyota", "Camry", "C123");
        var car2 = new Car(2L, "Golf", "7 R Line", "C555");
        var carList = List.of(car1, car2);

        when(carService.getAllCars()).thenReturn(carList);

        var carDtoListResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get("/car")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(CarDto[].class);

        assertEquals(carMapper.toDtoList(carList), List.of(carDtoListResponse));
    }

    @Test
    void createCar_succeed() {
        CarDto carDto = new CarDto("C123", "Toyota", "Camry");
        Car car = carMapper.fromDto(carDto);

        when(carService.createCar(any(Car.class))).thenReturn(car);

        var carDtoResponse = givenController()
                .body(carDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/car")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .extract().as(CarDto.class);

        assertNotNull(carDtoResponse);
        assertEquals(carDto, carDtoResponse);
    }

    @Test
    void createCar_carIdentifierValidationError() {
        CarDto carDto = new CarDto("X123", "Toyota", "Camry");

        var statusCode = givenController()
                .body(carDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/car")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

    }

    @Test
    void createCar_carAlreadyExists() {
        CarDto carDto = new CarDto("C123", "Toyota", "Camry");

        when(carService.createCar(any(Car.class))).thenThrow(new BadRequestException(TEST_ERROR_MESSAGE));

        var errorDto = givenController()
                .body(carDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/car")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .extract().as(ApiErrorDto.class);

        assertNotNull(errorDto);
        assertEquals(TEST_ERROR_MESSAGE, errorDto.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorDto.getCode());
    }

    @Test
    void updateCar_succeed() {
        CarDto carDto = new CarDto("C123", "Toyota", "Camry");
        Car car = carMapper.fromDto(carDto);

        when(carService.updateCar(anyLong(), any(Car.class))).thenReturn(car);

        var carDtoResponse = givenController()
                .body(carDto)
                .contentType(ContentType.JSON)
                .when()
                .put("/car/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(CarDto.class);

        assertNotNull(carDtoResponse);
        assertEquals(carDto, carDtoResponse);
    }

    @Test
    void updateCar_carIdentifierValidationError() {
        CarDto carDto = new CarDto("X123", "Toyota", "Camry");

        var statusCode = givenController()
                .body(carDto)
                .contentType(ContentType.JSON)
                .when()
                .put("/car/1")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    }

    @Test
    void updateCar_carNotFoundException() {
        CarDto carDto = new CarDto("C123", "Toyota", "Camry");

        when(carService.updateCar(anyLong(), any(Car.class))).thenThrow(new NotFoundException(TEST_ERROR_MESSAGE));

        var errorDto = givenController()
                .body(carDto)
                .contentType(ContentType.JSON)
                .when()
                .put("/car/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .extract().as(ApiErrorDto.class);

        assertNotNull(errorDto);
        assertEquals(TEST_ERROR_MESSAGE, errorDto.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorDto.getCode());
    }

    @Test
    void deleteCar_succeed() {
        doNothing().when(carService).deleteCar(anyLong());

        var statusCode = givenController()
                .when()
                .delete("/car/1")
                .statusCode();

        assertEquals(HttpStatus.OK.value(), statusCode);
    }

    @Test
    void deleteCar_carNotFoundException() {
        doThrow(new NotFoundException(TEST_ERROR_MESSAGE)).when(carService).deleteCar(anyLong());

        var errorDto = givenController()
                .when()
                .delete("/car/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .extract().as(ApiErrorDto.class);

        assertNotNull(errorDto);
        assertEquals(TEST_ERROR_MESSAGE, errorDto.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorDto.getCode());
    }
}
