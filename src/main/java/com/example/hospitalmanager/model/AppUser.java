package com.example.hospitalmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class AppUser {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String passwordHash;
    private String fullName;

    @Builder.Default
    private Role role = Role.PATIENT;

    public Role getRole() {
        return role == null ? Role.PATIENT : role;
    }

    public void setRole(Role role) {
        this.role = role == null ? Role.PATIENT : role;
    }
}
