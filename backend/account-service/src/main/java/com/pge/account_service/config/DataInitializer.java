package com.pge.account_service.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pge.account_service.domain.User;
import com.pge.account_service.domain.UserType;
import com.pge.account_service.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new User(null, "Rodrigo (Passageiro)", UserType.PASSAGEIRO));
                repository.save(new User(null, "Marina (Passageiro)", UserType.PASSAGEIRO));
                repository.save(new User(null, "Bruno (Motorista)", UserType.MOTORISTA));
                repository.save(new User(null, "Davi (Motorista)", UserType.MOTORISTA));
                System.out.println("Dados iniciais de mock inseridos com sucesso!");
            }
        };
    }
}
