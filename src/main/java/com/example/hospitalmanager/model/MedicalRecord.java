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
@Document(collection = "medical_records")
public class MedicalRecord {
    @Id
    private String id;

    @NotBlank
    private String patientId;

    private String appointmentId;

    @NotBlank
    private String doctorName;

    @NotBlank
    @Size(max = 1000)
    private String diagnosis;

    @NotBlank
    @Size(max = 2000)
    private String treatment;

    @Size(max = 5000)
    private String notes;

    @NotNull
    private LocalDateTime recordDate;
}
