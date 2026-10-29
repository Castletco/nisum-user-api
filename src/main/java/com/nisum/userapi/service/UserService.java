package com.nisum.userapi.service;

import com.nisum.userapi.dto.UserPatchRequest;
import com.nisum.userapi.dto.UserRequest;
import com.nisum.userapi.entity.User;

import java.util.UUID;

public interface UserService {

    User createUser(UserRequest request);

    User getUserById(UUID id);

    User updateUser(UUID id, UserRequest request);

    User patchUser(UUID id, UserPatchRequest request);

    void deleteUser(UUID id);

}