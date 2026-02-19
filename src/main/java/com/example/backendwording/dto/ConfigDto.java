package com.example.backendwording.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDto {
    private List<String> active_languages;
    private Map<String, String> versions;
}
