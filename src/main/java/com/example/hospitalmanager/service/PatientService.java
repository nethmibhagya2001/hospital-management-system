package com.example.hospitalmanager.service;

import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final Validator validator;

    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository,
            Validator validator) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public Patient createPatient(Patient patient) {
        validate(patient);
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "A patient with this email already exists");
        }
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(String id) {
        if (id == null || id.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Patient id is required");
        }
        return patientRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public Patient getPatientByUser(String username) {
        if (username == null || username.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getRole() != Role.PATIENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is not a patient");
        }
        return patientRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public Patient updatePatient(String id, Patient updates) {
        Patient existing = getPatientById(id);
        validate(updates);
        if (!Objects.equals(existing.getEmail(), updates.getEmail())
                && patientRepository.existsByEmail(updates.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "A patient with this email already exists");
        }

        existing.setFullName(updates.getFullName());
        existing.setEmail(updates.getEmail());
        existing.setPhone(updates.getPhone());
        existing.setGender(updates.getGender());
        existing.setAge(updates.getAge());
        existing.setAddress(updates.getAddress());
        return patientRepository.save(existing);
    }

    private void validate(Patient patient) {
        if (patient == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Patient data is required");
        }
        String violations = validator.validate(patient).stream()
                .map(this::formatViolation)
                .collect(Collectors.joining(", "));
        if (!violations.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, violations);
        }
    }

    private String formatViolation(ConstraintViolation<Patient> violation) {
        return violation.getPropertyPath() + " " + violation.getMessage();
    }
}
