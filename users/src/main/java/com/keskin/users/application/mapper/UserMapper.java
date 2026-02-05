package com.keskin.users.application.mapper;


import com.keskin.common.dto.UserDto;
import com.keskin.users.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto (User user){
        if (user == null) return null;

        return new UserDto(
                user.getUuid(),
                user.getName().value(),
                user.getAge().value(),
                user.getEmail().value(),
                user.getRole(),
                user.isActive()
        );
    }
}
