package com.nisum.userapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPatchRequest {

    private String name;
    private String email;
    private String password;
    private List<PhoneRequest> phones;
}