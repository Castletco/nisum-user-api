package com.nisum.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private List<PhoneRequest> phones;

    private LocalDateTime created;
    private LocalDateTime modified;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;

    private String token;

    @JsonProperty("isactive")
    private Boolean isActive;
}