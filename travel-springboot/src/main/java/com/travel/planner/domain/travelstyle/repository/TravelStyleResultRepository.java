package com.travel.planner.domain.travelstyle.repository;

import com.travel.planner.domain.travelstyle.entity.TravelStyleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelStyleResultRepository extends JpaRepository<TravelStyleResult, Long> {
}