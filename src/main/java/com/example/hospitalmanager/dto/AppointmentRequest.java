package com.example.hospitalmanager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotBlank String patientId,
        @NotBlank String doctorId,
        @NotNull @Future LocalDateTime appointmentDateTime,
        @NotBlank String reason
) {
}
