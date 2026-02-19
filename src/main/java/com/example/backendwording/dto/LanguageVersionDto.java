package com.example.backendwording.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageVersionDto {
    private String code;       // ex: "fr"
    private String name;       // ex: "Français"
    private String currentVersion; // ex: "2"
}

