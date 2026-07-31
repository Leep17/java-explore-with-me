package ru.practicum.main.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.request.RequestStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {
    private Set<Long> requestIds;
    private RequestStatus status;
}
