package com.car.reservation.service.mapper;

import com.car.reservation.commons.model.CarReservationDto;
import com.car.reservation.model.CarReservation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarReservationMapper {

    CarReservation fromDto(CarReservationDto carReservationDto);

    CarReservationDto toDto(CarReservation carReservation);

    List<CarReservationDto> toDtoList(List<CarReservation> carReservation);
}
