package ru.practicum.main.user.service;

import ru.practicum.main.user.User;
import ru.practicum.main.user.dto.NewUserDto;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    User save(NewUserDto newUserDto);

    void deleteById(Long id);

    Collection<User> getAll(int from, int size, Set<Long> ids);
}
