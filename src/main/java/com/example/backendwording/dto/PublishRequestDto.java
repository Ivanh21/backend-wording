package com.example.backendwording.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body pour POST /api/v1/publish
 * lang -> langue à publier (ex: "fr")
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishRequestDto {
    private String lang;
}

