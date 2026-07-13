package com.pge.ride_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.pge.ride_service.config.RabbitMQConfig;
import com.pge.ride_service.domain.Ride;
import com.pge.ride_service.notification.NotificationService;

@Component
public class RideConsumer {

    private final NotificationService notificationService;

    public RideConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeNewRideMessage(Ride ride) {
        System.out.println("Mensagem da queue consumida com sucesso. Enviando ID de corrida: " + ride.getId());

        notificationService.broadcastNewRide(ride);
    }
}
