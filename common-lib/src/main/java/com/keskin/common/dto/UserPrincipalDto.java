package com.keskin.common.dto;

import java.util.UUID;

public record UserPrincipalDto(UUID userId, String email) {}