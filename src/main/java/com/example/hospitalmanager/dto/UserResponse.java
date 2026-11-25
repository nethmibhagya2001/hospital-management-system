package com.example.hospitalmanager.dto;

import com.example.hospitalmanager.model.Role;

public record UserResponse(String id, String username, String fullName, Role role) {
}
