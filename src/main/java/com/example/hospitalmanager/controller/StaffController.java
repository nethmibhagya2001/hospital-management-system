package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private static final Set<Role> STAFF_ROLES =
            Set.of(Role.ADMIN, Role.DOCTOR, Role.NURSE, Role.RECEPTIONIST);

    private final UserRepository userRepository;

    public StaffController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserResponse> listStaff(@RequestParam(required = false) String role) {
        Role filterRole = parseStaffRole(role);
        return userRepository.findAll().stream()
                .filter(user -> STAFF_ROLES.contains(user.getRole()))
                .filter(user -> filterRole == null || user.getRole() == filterRole)
                .map(this::toUserResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getStaffById(@PathVariable String id) {
        if (id == null || id.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Staff id is required");
        }
        AppUser user = userRepository.findById(id)
                .filter(staff -> STAFF_ROLES.contains(staff.getRole()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff member not found"));
        return toUserResponse(user);
    }

    private Role parseStaffRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        try {
            Role parsedRole = Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
            if (!STAFF_ROLES.contains(parsedRole)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Role is not a staff role");
            }
            return parsedRole;
        } catch (IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid staff role");
        }
    }

    private UserResponse toUserResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
    }
}
