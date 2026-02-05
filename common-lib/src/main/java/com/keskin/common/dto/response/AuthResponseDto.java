package com.keskin.common.dto.response;

import com.keskin.common.dto.UserDto;

public record AuthResponseDto(
        UserDto userDto,
        String accessToken,
        String refreshTokenStr
) {
}
