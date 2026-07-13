package com.pge.ride_service;

import org.springframework.stereotype.Service;

import com.pge.ride_service.client.AccountClient;
import com.pge.ride_service.repository.RideRepository;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final AccountClient accountClient;
}
