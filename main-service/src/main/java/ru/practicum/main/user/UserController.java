package ru.practicum.main.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.user.dto.NewUserDto;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.service.UserService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/admin/users")
    public Collection<UserDto> getAll(@RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) Set<Long> ids) {
        return userService.getAll(from, size, ids).stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody NewUserDto newUserDto) {
        User user = userService.save(newUserDto);
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
