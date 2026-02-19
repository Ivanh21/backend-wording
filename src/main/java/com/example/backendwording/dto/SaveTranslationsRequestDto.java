package com.example.backendwording.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Body pour POST /api/v1/translations (sauvegarde Draft)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveTranslationsRequestDto {
    private List<TranslationRowDto> translations;
}

