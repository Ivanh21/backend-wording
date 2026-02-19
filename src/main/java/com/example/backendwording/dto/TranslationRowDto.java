package com.example.backendwording.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * Représente une ligne de la matrice de traduction.
 * key       -> clé de traduction (ex: "home.title")
 * values    -> map langue -> valeur (ex: {"fr": "Accueil", "en": "Home"})
 * isDraft   -> true si non publié
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRowDto {
    private String key;
    private Map<String, Object> values;
    private boolean isDraft;
}
