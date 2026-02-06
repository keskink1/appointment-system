package com.keskin.users.api;

import com.keskin.common.dto.UserDto;
import com.keskin.common.enums.Role;
import com.keskin.common.util.AuthorizationUtil;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.users.application.service.UserAppService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.keskin.common.constants.AppConstants.*;

@Tag(
        name = "User service for appointment system",
        description = "You can manage profile settings and admin endpoints"
)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(
            @PathVariable UUID id,
            @RequestHeader(HEADER_USER_ID) String currentUserIdHeader,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkUserAccess(id, UUID.fromString(currentUserIdHeader), role);

        return ResponseEntity.ok(userAppService.getUserDtoById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateById(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequestDto request,
            @RequestHeader(HEADER_USER_ID) String currentUserIdHeader,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkUserAccess(id, UUID.fromString(currentUserIdHeader), role);

        return ResponseEntity.ok(userAppService.updateUserById(id, request));
    }

    // --- ADMIN ONLY ---

    @GetMapping("/search")
    public ResponseEntity<UserDto> getByEmail(
            @RequestParam String email,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        if (role != Role.ADMIN) {
            AuthorizationUtil.checkPermission(role);
        }

        return ResponseEntity.ok(userAppService.getUserDtoByEmail(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID id,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkPermission(role);

        userAppService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<UserDto> promoteToAdmin(
            @PathVariable UUID id,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkPermission(role);
        return ResponseEntity.ok(userAppService.promoteToAdmin(id));
    }

    @PutMapping("/{id}/changeRole")
    public ResponseEntity<UserDto> changeRoleToEmployee(
            @PathVariable UUID id,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkPermission(role);
        return ResponseEntity.ok(userAppService.changeRoleToEmployee(id));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<UserDto> activateUser(
            @PathVariable UUID id,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkPermission(role);
        return ResponseEntity.ok(userAppService.activateUser(id));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserDto> deactivateUser(
            @PathVariable UUID id,
            @RequestHeader(HEADER_USER_ROLE) String roleHeader) {

        Role role = AuthorizationUtil.parseRole(roleHeader);
        AuthorizationUtil.checkPermission(role);
        return ResponseEntity.ok(userAppService.deactivateUser(id));
    }
}