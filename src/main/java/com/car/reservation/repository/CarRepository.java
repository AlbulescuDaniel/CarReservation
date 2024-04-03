package com.car.reservation.repository;

import com.car.reservation.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByIdentifier(String identifier);

    @Query("SELECT c FROM Car c WHERE NOT EXISTS (SELECT r FROM CarReservation r WHERE r.car = c AND r.reservationTime BETWEEN :startTime AND :endTime)")
    List<Car> findFirstAvailableCar(Instant startTime, Instant endTime);

}
