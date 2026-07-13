package com.pge.ride_service;

import org.springframework.stereotype.Service;

import com.pge.ride_service.client.AccountClient;
import com.pge.ride_service.domain.Ride;
import com.pge.ride_service.dto.UserDTO;
import com.pge.ride_service.exception.ResourceNotFoundException;
import com.pge.ride_service.exception.ValidationException;
import com.pge.ride_service.repository.RideRepository;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final AccountClient accountClient;

    public RideService(RideRepository rideRepository, AccountClient accountClient) {
        this.rideRepository = rideRepository;
        this.accountClient = accountClient;
    }

    public Ride createRide(Ride ride) {
        
        if (ride.getOrigem() == null || ride.getOrigem().trim().isEmpty() || ride.getDestino() == null || ride.getDestino().trim().isEmpty()) {
            throw new ValidationException("Nenhum endereço de origem e destino foi inserido");
        }

        try {
            UserDTO user = accountClient.findById(ride.getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("Nenhum usuário encontrado com o ID: " + ride.getUserId());
            }
        } catch (Exception exception) {
            throw new ResourceNotFoundException("A validação de usuário falhou. ID: " + ride.getUserId());
        }

        ride.setStatus("CRIADO");
        return rideRepository.save(ride);
    }
}
