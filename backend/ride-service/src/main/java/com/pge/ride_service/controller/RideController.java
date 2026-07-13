package com.pge.ride_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pge.ride_service.domain.Ride;
import com.pge.ride_service.service.RideService;

@RestController
@RequestMapping("/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<Ride> create(@RequestBody Ride ride) {
        Ride createdRide = rideService.createRide(ride);
        return ResponseEntity.ok(createdRide);
    }
}
