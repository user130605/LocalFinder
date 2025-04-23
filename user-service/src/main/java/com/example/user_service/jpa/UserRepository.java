package com.example.user_service.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findById(int userId);
    Optional<UserEntity> findByEmail(String email);
}
