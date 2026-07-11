package com.pge.account_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pge.account_service.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
