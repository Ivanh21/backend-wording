package com.example.backendwording.service;

import com.example.backendwording.dto.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class TranslationService {

    private final JsonFileStorageService storageService;

    public TranslationService(JsonFileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Retourne la matrice complete des traductions.
     * Pour chaque langue active, fusionne la version publiee + le draft.
     */
    public List<TranslationRowDto> getTranslationMatrix() throws IOException {
        ConfigDto config = storageService.readConfig();
        List<String> langs = config.getActive_languages();

        // Charger publiees et drafts par langue
        Map<String, Map<String, Object>> published = new LinkedHashMap<>();
        Map<String, Map<String, Object>> drafts = new LinkedHashMap<>();

        for (String lang : langs) {
            String version = config.getVersions().getOrDefault(lang, "0");
            published.put(lang, "0".equals(version)
                    ? new LinkedHashMap<>()
                    : storageService.readTranslation(lang, version));
            drafts.put(lang, storageService.readDraft(lang));
        }

        // Union de toutes les cles
        Set<String> allKeys = new LinkedHashSet<>();
        published.values().forEach(m -> allKeys.addAll(m.keySet()));
        drafts.values().forEach(m -> allKeys.addAll(m.keySet()));

        List<TranslationRowDto> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> values = new LinkedHashMap<>();
            boolean hasDraft = false;
            for (String lang : langs) {
                Map<String, Object> draft = drafts.get(lang);
                if (draft.containsKey(key)) {
                    values.put(lang, draft.get(key));
                    hasDraft = true;
                } else {
                    values.put(lang, published.get(lang).getOrDefault(key, ""));
                }
            }
            result.add(new TranslationRowDto(key, values, hasDraft));
        }
        return result;
    }

    /**
     * Sauvegarde les traductions en tant que brouillons (Draft).
     */
    public void saveAsDraft(List<TranslationRowDto> rows) throws IOException {
        ConfigDto config = storageService.readConfig();
        List<String> langs = config.getActive_languages();

        Map<String, Map<String, Object>> byLang = new LinkedHashMap<>();
        for (String lang : langs) byLang.put(lang, new LinkedHashMap<>());

        for (TranslationRowDto row : rows) {
            row.getValues().forEach((lang, value) ->
                byLang.computeIfAbsent(lang, k -> new LinkedHashMap<>()).put(row.getKey(), value)
            );
        }

        for (Map.Entry<String, Map<String, Object>> entry : byLang.entrySet()) {
            storageService.saveDraft(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Publie une langue :
     * 1. Fusionne draft + derniere version publiee
     * 2. Ecrit {lang}.v{n+1}.json
     * 3. Incremente la version dans config.json
     * 4. Supprime le draft
     */
    public PublishResponseDto publish(String lang) throws IOException {
        String currentVersion = storageService.getCurrentVersion(lang);

        Map<String, Object> base = "0".equals(currentVersion)
                ? new LinkedHashMap<>()
                : storageService.readTranslation(lang, currentVersion);

        Map<String, Object> draft = storageService.readDraft(lang);
        base.putAll(draft);

        String newVersion = storageService.incrementVersion(lang);
        storageService.saveTranslation(lang, newVersion, base);
        storageService.deleteDraft(lang);

        return new PublishResponseDto(lang, newVersion,
                "Langue '" + lang + "' publiee en version v" + newVersion);
    }

    /**
     * Ajoute une langue dans active_languages si absente.
     */
    public void addLanguage(String code) throws IOException {
        ConfigDto config = storageService.readConfig();
        if (!config.getActive_languages().contains(code)) {
            config.getActive_languages().add(code);
            config.getVersions().put(code, "0");
            storageService.saveConfig(config);
        }
    }

    /**
     * Supprime une langue de active_languages et versions.
     */
    public void removeLanguage(String code) throws IOException {
        ConfigDto config = storageService.readConfig();
        config.getActive_languages().remove(code);
        config.getVersions().remove(code);
        storageService.saveConfig(config);
    }
}
