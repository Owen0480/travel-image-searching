package com.travel.planner.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.travel.planner.domain.auth")
public class RedisConfig {
}
