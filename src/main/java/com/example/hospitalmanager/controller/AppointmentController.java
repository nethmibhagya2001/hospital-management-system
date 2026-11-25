package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.StatusRequest;
import com.example.hospitalmanager.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(request));
    }

    @GetMapping
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> getAppointmentsByPatientId(
            @PathVariable String patientId) {
        return appointmentService.getAppointmentsByPatientId(patientId);
    }

    @GetMapping("/{id}")
    public AppointmentResponse getAppointmentById(@PathVariable String id) {
        return appointmentService.getAppointmentById(id);
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponse updateAppointmentStatus(
            @PathVariable String id, @Valid @RequestBody StatusRequest request) {
        return appointmentService.updateAppointmentStatus(id, request);
    }
}
