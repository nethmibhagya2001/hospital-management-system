package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.service.MedicalRecordService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalRecordService.createMedicalRecord(request));
    }

    @GetMapping
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordResponse> getMedicalRecordsByPatientId(
            @PathVariable String patientId) {
        return medicalRecordService.getMedicalRecordsByPatientId(patientId);
    }

    @GetMapping("/{id}")
    public MedicalRecordResponse getMedicalRecordById(@PathVariable String id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    @PutMapping("/{id}")
    public MedicalRecordResponse updateMedicalRecord(
            @PathVariable String id, @Valid @RequestBody MedicalRecordRequest request) {
        return medicalRecordService.updateMedicalRecord(id, request);
    }
}
