package com.car.reservation.repository;

import com.car.reservation.model.CarReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarReservationRepository extends JpaRepository<CarReservation, Long> {


}
