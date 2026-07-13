package com.pge.ride_service.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.pge.ride_service.config.RabbitMQConfig;
import com.pge.ride_service.domain.Ride;

@Component
public class RideProducer {

    private final RabbitTemplate rabbitTemplate;

    public RideProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNewRideNotification(Ride ride) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            ride
        );

        System.out.println("Corrida ID" + ride.getId() + "enviada para a queue do RabbitMQ!");
    }
}
