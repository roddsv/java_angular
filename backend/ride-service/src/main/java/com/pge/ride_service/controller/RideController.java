package com.pge.ride_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pge.ride_service.domain.Ride;
import com.pge.ride_service.notification.NotificationService;
import com.pge.ride_service.service.RideService;

@RestController
@RequestMapping("/rides")
public class RideController {

    private final RideService rideService;
    private final NotificationService notificationService;


    public RideController(RideService rideService, NotificationService notificationService) {
        this.rideService = rideService;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Ride> create(@RequestBody Ride ride) {
        Ride createdRide = rideService.createRide(ride);
        return ResponseEntity.ok(createdRide);
    }

    @GetMapping("/notifications/subscribe")
    public SseEmitter subscribe() {
        return notificationService.subscribe();
    }
}
