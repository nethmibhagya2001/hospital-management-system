package com.example.hospitalmanager.model;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;

    @NotBlank
    private String patientId;

    @NotBlank
    private String doctorId;

    @NotNull
    private LocalDateTime appointmentDateTime;

    @NotBlank
    @Size(max = 500)
    private String reason;

    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    public AppointmentStatus getStatus() {
        return status == null ? AppointmentStatus.SCHEDULED : status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status == null ? AppointmentStatus.SCHEDULED : status;
    }
}
