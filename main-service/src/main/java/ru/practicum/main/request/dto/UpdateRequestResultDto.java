package ru.practicum.main.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestResultDto {
    private Collection<RequestDto> confirmedRequests;
    private Collection<RequestDto> rejectedRequests;
}
