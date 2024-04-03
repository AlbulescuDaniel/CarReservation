package com.car.reservation.service.mapper;

import com.car.reservation.commons.model.CarDto;
import com.car.reservation.model.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    Car fromDto(CarDto carDto);

    CarDto toDto(Car carDto);

    List<CarDto> toDtoList(List<Car> carList);
}
