package com.example.backendwording.controller;

import com.example.backendwording.dto.ConfigDto;
import com.example.backendwording.service.JsonFileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * API publique consommée par la librairie @indatacore/wording-library
 *
 * GET /api/v1/public/config.json
 * GET /api/v1/public/i18n/{lang}.v{version}.json
 */
@RestController
@RequestMapping("/v1/public")
public class PublicController {

    private final JsonFileStorageService storageService;

    public PublicController(JsonFileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Retourne la configuration globale (versions actuelles par langue).
     * Exemple de réponse :
     * {
     *   "languages": [
     *     { "code": "fr", "name": "Français", "currentVersion": "2" },
     *     { "code": "en", "name": "English",  "currentVersion": "1" }
     *   ]
     * }
     */
    @GetMapping("/config.json")
    public ResponseEntity<ConfigDto> getConfig() throws IOException {
        return ResponseEntity.ok(storageService.readConfig());
    }

    /**
     * Retourne le fichier de traduction pour une langue et une version données.
     * Exemple : GET /api/v1/public/i18n/fr.v1.json
     */
    @GetMapping("/i18n/{lang}.v{version}.json")
    public ResponseEntity<Map<String, Object>> getTranslation(
            @PathVariable String lang,
            @PathVariable String version) throws IOException {
        Map<String, Object> translations = storageService.readTranslation(lang, version);
        return ResponseEntity.ok(translations);
    }
}

