package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.MedicalRecord;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final Validator validator;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            Validator validator) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.validator = validator;
    }

    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        validate(request);
        ensurePatientExists(request.patientId());

        MedicalRecord record = MedicalRecord.builder()
                .patientId(request.patientId())
                .appointmentId(request.appointmentId())
                .doctorName(request.doctorName())
                .diagnosis(request.diagnosis())
                .treatment(request.treatment())
                .notes(request.notes())
                .recordDate(LocalDateTime.now())
                .build();
        return toResponse(medicalRecordRepository.save(record));
    }

    public List<MedicalRecordResponse> getAllMedicalRecords() {
        return medicalRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MedicalRecordResponse getMedicalRecordById(String id) {
        if (id == null || id.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Medical record id is required");
        }
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Medical record not found"));
        return toResponse(record);
    }

    public List<MedicalRecordResponse> getMedicalRecordsByPatientId(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Patient id is required");
        }
        ensurePatientExists(patientId);
        return medicalRecordRepository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    public MedicalRecordResponse updateMedicalRecord(String id, MedicalRecordRequest request) {
        if (id == null || id.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Medical record id is required");
        }
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Medical record not found"));
        validate(request);
        ensurePatientExists(request.patientId());

        record.setPatientId(request.patientId());
        record.setAppointmentId(request.appointmentId());
        record.setDoctorName(request.doctorName());
        record.setDiagnosis(request.diagnosis());
        record.setTreatment(request.treatment());
        record.setNotes(request.notes());
        return toResponse(medicalRecordRepository.save(record));
    }

    private void ensurePatientExists(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found");
        }
    }

    private MedicalRecordResponse toResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getPatientId(),
                record.getAppointmentId(),
                record.getDoctorName(),
                record.getDiagnosis(),
                record.getTreatment(),
                record.getNotes(),
                record.getRecordDate());
    }

    private <T> void validate(T value) {
        if (value == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        String violations = validator.validate(value).stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining(", "));
        if (!violations.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, violations);
        }
    }
}
