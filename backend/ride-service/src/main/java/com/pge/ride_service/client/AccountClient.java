package com.pge.ride_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pge.ride_service.dto.UserDTO;

@FeignClient(value = "account-service")
public interface AccountClient {

    @GetMapping("/accounts/{id}")
    UserDTO findById(@PathVariable("id") Long id);
}
