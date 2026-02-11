package com.travel.planner.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = { "com.travel.planner.domain.user", "com.travel.planner.domain.conversation", "com.travel.planner.domain.travelstyle" })
public class JpaConfig {
}
