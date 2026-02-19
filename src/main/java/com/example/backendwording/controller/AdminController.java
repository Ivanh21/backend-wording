package com.example.backendwording.controller;

import com.example.backendwording.dto.*;
import com.example.backendwording.service.JsonFileStorageService;
import com.example.backendwording.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * API Admin consommée par le Frontend Angular
 *
 * GET    /api/v1/translations          → Matrice de traduction
 * POST   /api/v1/translations          → Sauvegarde Draft
 * POST   /api/v1/publish               → Publie une langue (génère JSON + incrémente version)
 * GET    /api/v1/config                → Configuration (langues + versions)
 * POST   /api/v1/config/languages      → Ajouter une langue
 * DELETE /api/v1/config/languages/{code} → Supprimer une langue
 */
@RestController
@RequestMapping("/v1")
public class AdminController {

    private final TranslationService translationService;
    private final JsonFileStorageService storageService;

    public AdminController(TranslationService translationService, JsonFileStorageService storageService) {
        this.translationService = translationService;
        this.storageService = storageService;
    }

    // ─── Traductions ─────────────────────────────────────────────────────────

    /**
     * Retourne la matrice complète des traductions.
     * Chaque ligne contient la clé, les valeurs par langue, et si c'est un draft.
     */
    @GetMapping("/translations")
    public ResponseEntity<List<TranslationRowDto>> getTranslations() throws IOException {
        return ResponseEntity.ok(translationService.getTranslationMatrix());
    }

    /**
     * Sauvegarde les traductions en tant que brouillon (Draft).
     * Ne publie pas encore — les fichiers JSON publics ne sont pas modifiés.
     */
    @PostMapping("/translations")
    public ResponseEntity<Void> saveTranslations(@RequestBody SaveTranslationsRequestDto request) throws IOException {
        translationService.saveAsDraft(request.getTranslations());
        return ResponseEntity.ok().build();
    }

    // ─── Publication ──────────────────────────────────────────────────────────

    /**
     * Publie une langue :
     * - Fusionne le draft avec la dernière version publiée
     * - Génère le fichier {lang}.v{n+1}.json
     * - Incrémente la version dans config.json
     * - Supprime le draft
     */
    @PostMapping("/publish")
    public ResponseEntity<PublishResponseDto> publish(@RequestBody PublishRequestDto request) throws IOException {
        PublishResponseDto response = translationService.publish(request.getLang());
        return ResponseEntity.ok(response);
    }

    // ─── Configuration (Langues) ──────────────────────────────────────────────

    /**
     * Retourne la configuration globale (langues + versions actuelles).
     */
    @GetMapping("/config")
    public ResponseEntity<ConfigDto> getConfig() throws IOException {
        return ResponseEntity.ok(storageService.readConfig());
    }

    /**
     * Ajoute une nouvelle langue.
     * Body : { "code": "es", "name": "Español" }
     */
    @PostMapping("/config/languages")
    public ResponseEntity<Void> addLanguage(@RequestBody java.util.Map<String, String> body) throws IOException {
        translationService.addLanguage(body.get("code"));
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime une langue de la configuration.
     */
    @DeleteMapping("/config/languages/{code}")
    public ResponseEntity<Void> removeLanguage(@PathVariable String code) throws IOException {
        translationService.removeLanguage(code);
        return ResponseEntity.ok().build();
    }
}
