package com.keskin.users.api;

import com.keskin.users.application.dto.CreateUserRequestDto;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.users.application.dto.UserDto;
import com.keskin.users.application.service.UserAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id){
        UserDto response = userAppService.getUserDtoById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequestDto request) {
        UserDto response = userAppService.registerUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {
        UserDto response = userAppService.getUserDtoByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateById(@PathVariable UUID id, @RequestBody UpdateUserRequestDto request){
        UserDto response = userAppService.updateUserById(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id){
        userAppService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<UserDto> promoteToAdmin(@PathVariable UUID id){
        UserDto dto = userAppService.promoteToAdmin(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/changeRole")
    public ResponseEntity<UserDto> changeRoleToEmployee(@PathVariable UUID id){
        UserDto dto = userAppService.changeRoleToEmployee(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<UserDto> activateUser(@PathVariable UUID id){
        UserDto dto = userAppService.activateUser(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable UUID id){
        UserDto dto = userAppService.deactivateUser(id);
        return ResponseEntity.ok(dto);
    }
}
