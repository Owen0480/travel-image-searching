package com.travel.planner.domain.user.repository;

import com.travel.planner.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthIdentifier(String oauthIdentifier);
}