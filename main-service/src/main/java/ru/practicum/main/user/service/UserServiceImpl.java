package ru.practicum.main.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.User;
import ru.practicum.main.user.dto.NewUserDto;
import ru.practicum.main.user.repository.UserRepository;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public User save(NewUserDto newUserDto) {
        if (userRepository.existsByEmail(newUserDto.getEmail())) {
            throw new ConflictException("Пользователь с email=" + newUserDto.getEmail() + " уже существует");
        }
        User user = new User();
        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        userRepository.deleteById(id);
    }

    @Override
    public Collection<User> getAll(int from, int size, Set<Long> ids) {
        return userRepository.findAll().stream()
                .filter(user -> ids == null || ids.isEmpty() || ids.contains(user.getId()))
                .skip(from)
                .limit(size)
                .toList();
    }
}
