package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.StatusRequest;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.Appointment;
import com.example.hospitalmanager.model.AppointmentStatus;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import jakarta.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final Validator validator;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            Validator validator) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.validator = validator;
    }

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        validate(request);
        if (!patientRepository.existsById(request.patientId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found");
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .appointmentDateTime(request.appointmentDateTime())
                .reason(request.reason())
                .status(AppointmentStatus.SCHEDULED)
                .build();
        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse getAppointmentById(String id) {
        if (id == null || id.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Appointment id is required");
        }
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
        return toResponse(appointment);
    }

    public List<AppointmentResponse> getAppointmentsByPatientId(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Patient id is required");
        }
        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found");
        }
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse updateAppointmentStatus(String id, StatusRequest request) {
        if (id == null || id.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Appointment id is required");
        }
        validate(request);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
        appointment.setStatus(request.status());
        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getAppointmentDateTime(),
                appointment.getReason(),
                appointment.getStatus());
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
