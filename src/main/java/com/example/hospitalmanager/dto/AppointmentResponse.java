package com.example.hospitalmanager.dto;

import com.example.hospitalmanager.model.AppointmentStatus;
import java.time.LocalDateTime;

public record AppointmentResponse(
        String id,
        String patientId,
        String doctorId,
        LocalDateTime appointmentDateTime,
        String reason,
        AppointmentStatus status
) {
}
