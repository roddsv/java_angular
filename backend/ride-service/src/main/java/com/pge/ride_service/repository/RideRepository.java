package com.pge.ride_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pge.ride_service.domain.Ride;

public interface RideRepository extends JpaRepository<Ride, Long> {

}
