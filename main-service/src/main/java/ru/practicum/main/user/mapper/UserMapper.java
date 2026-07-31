package ru.practicum.main.user.mapper;

import ru.practicum.main.user.User;
import ru.practicum.main.user.dto.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
