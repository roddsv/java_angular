package com.pge.ride_service.service;

import com.pge.ride_service.client.AccountClient;
import com.pge.ride_service.dto.UserDTO;
import com.pge.ride_service.exception.ResourceNotFoundException;
import com.pge.ride_service.exception.ValidationException;
import com.pge.ride_service.domain.Ride;
import com.pge.ride_service.producer.RideProducer;
import com.pge.ride_service.repository.RideRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private RideProducer rideProducer;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RideService rideService;

    private Ride sampleRide;
    private UserDTO sampleUser;

    @BeforeEach
    void setUp() {
        sampleRide = new Ride(1L, 10L, null, "Aeroporto", "PGE", "CRIADO");
        sampleUser = new UserDTO();
        sampleUser.setId(10L);
        sampleUser.setName("Carlos");
        sampleUser.setType("CLIENTE");
    }

    @Nested
    @DisplayName("Testes de Criação de Corrida")
    class CreateRideTests {

        @Test
        @DisplayName("Deve criar corrida com sucesso quando os dados forem válidos")
        void shouldCreateRideWithSuccess() {
            when(accountClient.findById(10L)).thenReturn(sampleUser);
            when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

            Ride created = rideService.createRide(sampleRide);

            assertNotNull(created);
            assertEquals("CRIADO", created.getStatus());
            verify(rideRepository, times(1)).save(sampleRide);
            verify(rideProducer, times(1)).sendNewRideNotification(sampleRide);
        }

        @Test
        @DisplayName("Deve lançar ValidationException quando origem estiver vazia")
        void shouldThrowExceptionWhenOriginIsEmpty() {
            sampleRide.setOrigem("");

            assertThrows(ValidationException.class, () -> rideService.createRide(sampleRide));
            verifyNoInteractions(accountClient, rideRepository, rideProducer);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existir no AccountService")
        void shouldThrowExceptionWhenUserDoesNotExist() {
            when(accountClient.findById(10L)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> rideService.createRide(sampleRide));
            verify(rideRepository, never()).save(any(Ride.class));
            verifyNoInteractions(rideProducer);
        }
    }

    @Nested
    @DisplayName("Testes de Aceitação de Corrida")
    class AcceptRideTests {

        @Test
        @DisplayName("Deve aceitar corrida com sucesso, atualizando banco e salvando no Redis")
        void shouldAcceptRideWithSuccess() {
            Long driverId = 5L;
            when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
            when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            Ride accepted = rideService.acceptRide(1L, driverId);

            assertNotNull(accepted);
            assertEquals("EM_ANDAMENTO", accepted.getStatus());
            assertEquals(driverId, accepted.getMotoristaId());
            verify(rideRepository, times(1)).save(sampleRide);
            verify(valueOperations, times(1)).set("ride:status:1", "EM_ANDAMENTO");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar aceitar corrida inexistente")
        void shouldThrowExceptionWhenRideDoesNotExist() {
            when(rideRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> rideService.acceptRide(99L, 5L));
            verify(rideRepository, never()).save(any(Ride.class));
            verifyNoInteractions(redisTemplate);
        }
    }
}
