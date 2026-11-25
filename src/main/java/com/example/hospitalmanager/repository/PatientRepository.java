package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Patient> findByFullNameContainingIgnoreCase(String fullName);
}
