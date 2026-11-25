package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.MedicalRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientId(String patientId);

    Optional<MedicalRecord> findByAppointmentId(String appointmentId);
}
