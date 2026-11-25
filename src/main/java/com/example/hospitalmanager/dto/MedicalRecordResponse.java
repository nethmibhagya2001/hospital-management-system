package com.example.hospitalmanager.dto;

import java.time.LocalDateTime;

public record MedicalRecordResponse(
        String id,
        String patientId,
        String appointmentId,
        String doctorName,
        String diagnosis,
        String treatment,
        String notes,
        LocalDateTime recordDate
) {
}
