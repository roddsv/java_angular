package com.pge.ride_service.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.pge.ride_service.client.AccountClient;
import com.pge.ride_service.domain.Ride;
import com.pge.ride_service.dto.UserDTO;
import com.pge.ride_service.exception.ResourceNotFoundException;
import com.pge.ride_service.exception.ValidationException;
import com.pge.ride_service.producer.RideProducer;
import com.pge.ride_service.repository.RideRepository;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final AccountClient accountClient;
    private final RideProducer rideProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    public RideService(RideRepository rideRepository, AccountClient accountClient, RideProducer rideProducer, RedisTemplate<String, Object> redisTemplate) {
        this.rideRepository = rideRepository;
        this.accountClient = accountClient;
        this.rideProducer = rideProducer;
        this.redisTemplate = redisTemplate;
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
        Ride savedRide = rideRepository.save(ride);
        rideProducer.sendNewRideNotification(savedRide);

        return savedRide;
    }

    public Ride acceptRide(Long rideId, Long motoristaId) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new ResourceNotFoundException("Corrida não encontrada. ID: " + rideId));

        ride.setMotoristaId(motoristaId);
        ride.setStatus("EM_ANDAMENTO");

        Ride updatedRide = rideRepository.save(ride);

        String redisKey = "ride:status:" + rideId;
        redisTemplate.opsForValue().set(redisKey, "EM_ANDAMENTO");

        System.out.println("Corrida " + rideId + " aceita pelo motorista " + motoristaId + ". Status salvo no Redis");

        return updatedRide;
    }
}
