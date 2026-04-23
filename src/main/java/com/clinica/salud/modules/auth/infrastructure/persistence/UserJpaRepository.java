package com.clinica.salud.modules.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.email = :email AND u.isActive = true")
    Optional<UserEntity> findByEmailAndIsActiveTrue(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.googleId = :googleId")
    Optional<UserEntity> findByGoogleId(@Param("googleId") String googleId);
}
