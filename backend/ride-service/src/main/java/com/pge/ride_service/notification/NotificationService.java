package com.pge.ride_service.notification;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pge.ride_service.domain.Ride;

@Service
public class NotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    
        this.emitters.add(emitter);
        System.out.println("Novo motorista conectado! Total de conexões ativas: " + this.emitters.size());
    
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Conectado com sucesso!"));
        } catch (IOException e) {
            this.emitters.remove(emitter);
        }
    
        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
            System.out.println("Conexão concluída e removida.");
        });
        emitter.onTimeout(() -> {
            this.emitters.remove(emitter);
            System.out.println("Conexão expirou e foi removida.");
        });
        emitter.onError((e) -> {
            this.emitters.remove(emitter);
            System.out.println("Erro na conexão. Conexão removida.");
        });
    
        return emitter;
    }

    public void broadcastNewRide(Ride ride) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("NEW_RIDE")
                    .data(ride));
            } catch (Exception exception) {
                deadEmitters.add(emitter);
            }
        }

        this.emitters.removeAll(deadEmitters);
    }
}
