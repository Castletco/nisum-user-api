package com.nisum.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhoneRequest {

    @NotBlank(message = "El número de teléfono es obligatorio")
    private String number;

    @NotBlank(message = "El código de ciudad es obligatorio")
    @JsonProperty("citycode")
    private String cityCode;

    @NotBlank(message = "El código de país es obligatorio")
    @JsonProperty("contrycode")
    private String countryCode;
}