package com.keskin.users.api;

import com.keskin.common.dto.UserDto;
import com.keskin.common.dto.request.CreateUserRequestDto;
import com.keskin.common.dto.request.LoginRequestDto;
import com.keskin.common.dto.response.AuthResponseDto;
import com.keskin.users.application.service.AuthAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthAppService authAppService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody CreateUserRequestDto request) {
        AuthResponseDto response = authAppService.registerUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.userDto().id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request){
        AuthResponseDto response = authAppService.loginUser(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam("refreshToken") String refreshToken) {
        String newAccessToken = authAppService.refreshMyAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("refreshToken") String refreshToken) {
        authAppService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
