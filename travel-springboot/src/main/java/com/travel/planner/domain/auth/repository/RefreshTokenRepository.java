package com.travel.planner.domain.auth.repository;

import com.travel.planner.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
