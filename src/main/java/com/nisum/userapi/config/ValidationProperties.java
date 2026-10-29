package com.nisum.userapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.validation")
public record ValidationProperties(
        String emailRegex,
        String passwordRegex
) {
}