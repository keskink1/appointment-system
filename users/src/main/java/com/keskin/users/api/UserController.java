package com.keskin.users.api;

import com.keskin.common.dto.UserDto;
import com.keskin.common.dto.response.PaginatedResponseDto;
import com.keskin.common.enums.Role;
import com.keskin.common.security.annotation.RequiresAdmin;
import com.keskin.common.util.AuthorizationUtil;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.users.application.service.UserAppService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.keskin.common.constants.AppConstants.*;

/**
 * REST controller for managing user profiles and administrative operations.
 * <p>
 * This controller relies on security headers (X-User-Id, X-User-Role) injected by the
 * API Gateway after successful authentication. These headers are used to implement
 * Fine-Grained Access Control (FGAC) and ensure that users can only access or
 * modify their own data, while admins maintain full system access.
 */
@Tag(
        name = "User service for appointment system",
        description = "Handles profile management and administrative tasks"
)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userAppService.getUserDtoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateById(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequestDto request) {
        return ResponseEntity.ok(userAppService.updateUserById(id, request));
    }

    // --- ADMIN ONLY ---

    @GetMapping
    @RequiresAdmin
    public ResponseEntity<PaginatedResponseDto<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(userAppService.findAll(page, size));
    }

    @GetMapping("/search")
    @RequiresAdmin
    public ResponseEntity<UserDto> getByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(userAppService.getUserDtoByEmail(email));
    }

    @DeleteMapping("/{id}")
    @RequiresAdmin
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID id) {

        userAppService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/promote")
    @RequiresAdmin
    public ResponseEntity<UserDto> promoteToAdmin(
            @PathVariable UUID id) {
        return ResponseEntity.ok(userAppService.promoteToAdmin(id));
    }

    @PutMapping("/{id}/changeRole")
    @RequiresAdmin
    public ResponseEntity<UserDto> changeRoleToEmployee(
            @PathVariable UUID id) {
        return ResponseEntity.ok(userAppService.changeRoleToEmployee(id));
    }

    @PutMapping("/{id}/activate")
    @RequiresAdmin
    public ResponseEntity<UserDto> activateUser(
            @PathVariable UUID id) {
        return ResponseEntity.ok(userAppService.activateUser(id));
    }

    @PutMapping("/{id}/deactivate")
    @RequiresAdmin
    public ResponseEntity<UserDto> deactivateUser(
            @PathVariable UUID id) {
        return ResponseEntity.ok(userAppService.deactivateUser(id));
    }
}