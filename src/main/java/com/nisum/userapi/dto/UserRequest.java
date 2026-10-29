package com.nisum.userapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "El correo es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @Valid
    @NotNull(message = "La lista de teléfonos es obligatoria")
    private List<@NotNull @Valid PhoneRequest> phones;
}