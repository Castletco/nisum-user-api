package com.nisum.userapi.controller;

import com.nisum.userapi.dto.UserPatchRequest;
import com.nisum.userapi.dto.UserRequest;
import com.nisum.userapi.dto.UserResponse;
import com.nisum.userapi.entity.User;
import com.nisum.userapi.mapper.UserMapper;
import com.nisum.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El correo ya registrado")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {

        User user = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toResponse(user));
    }

    @Operation(summary = "Obtener un usuario por su UUID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Operation(summary = "Actualizar completamente un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "El correo ya registrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {

        User updatedUser = userService.updateUser(id, request);

        return ResponseEntity.ok(
                userMapper.toResponse(updatedUser)
        );
    }

    @Operation(summary = "Actualizar parcialmente un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado parcialmente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "El correo ya registrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable UUID id,
            @RequestBody UserPatchRequest request) {

        User updatedUser = userService.patchUser(id, request);

        return ResponseEntity.ok(
                userMapper.toResponse(updatedUser)
        );
    }

    @Operation(summary = "Eliminar un usuario por su UUID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}