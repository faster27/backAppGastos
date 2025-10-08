package com.example.gastosapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseDto {

    @NotBlank(message = "La fecha es obligatoria")
    private String date;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Integer amount;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod;
}
