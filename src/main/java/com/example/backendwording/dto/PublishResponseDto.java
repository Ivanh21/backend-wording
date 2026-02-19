package com.example.backendwording.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishResponseDto {
    private String lang;
    private String newVersion;
    private String message;
}

