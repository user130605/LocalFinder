package com.example.user_service.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findById(int userId);
    UserEntity findByEmail(String email);
}
