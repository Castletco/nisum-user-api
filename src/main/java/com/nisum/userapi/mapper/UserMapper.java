package com.nisum.userapi.mapper;

import com.nisum.userapi.dto.PhoneRequest;
import com.nisum.userapi.dto.UserResponse;
import com.nisum.userapi.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreated(user.getCreated());
        response.setModified(user.getModified());
        response.setLastLogin(user.getLastLogin());
        response.setIsActive(user.getIsActive());
        response.setToken(user.getToken());

        response.setPhones(
            user.getPhones().stream()
                .map(phone -> {
                    PhoneRequest phoneResponse = new PhoneRequest();

                    phoneResponse.setNumber(phone.getNumber());
                    phoneResponse.setCityCode(phone.getCityCode());
                    phoneResponse.setCountryCode(phone.getCountryCode());

                    return phoneResponse;
                })
            .toList()
        );

        return response;
    }
}