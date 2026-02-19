package com.example.backendwording.service;

import com.example.backendwording.config.StorageProperties;
import com.example.backendwording.dto.ConfigDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Gere la lecture/ecriture des fichiers JSON sur le disque.
 *
 * Structure du dossier (configurable via storage.path) :
 *   /storage
 *     - config.json
 *     - i18n/
 *         - fr.v1.json
 *         - en.v1.json
 */
@Service
public class JsonFileStorageService {

    private final StorageProperties storageProperties;
    private final ObjectMapper objectMapper;

    public JsonFileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Paths

    private Path storageRoot() {
        return Paths.get(storageProperties.getPath());
    }

    private Path i18nDir() {
        return storageRoot().resolve("i18n");
    }

    private Path configFile() {
        return storageRoot().resolve("config.json");
    }

    private Path translationFile(String lang, String version) {
        return i18nDir().resolve(lang + ".v" + version + ".json");
    }

    private Path draftFile(String lang) {
        return i18nDir().resolve(lang + ".draft.json");
    }

    // Config

    public ConfigDto readConfig() throws IOException {
        return objectMapper.readValue(configFile().toFile(), ConfigDto.class);
    }

    public void saveConfig(ConfigDto config) throws IOException {
        objectMapper.writeValue(configFile().toFile(), config);
    }

    // Helpers

    public String getCurrentVersion(String lang) throws IOException {
        return readConfig().getVersions().getOrDefault(lang, "0");
    }

    public String incrementVersion(String lang) throws IOException {
        ConfigDto config = readConfig();
        Map<String, String> versions = config.getVersions();
        int current = Integer.parseInt(versions.getOrDefault(lang, "0"));
        String newVersion = String.valueOf(current + 1);
        versions.put(lang, newVersion);
        if (!config.getActive_languages().contains(lang)) {
            config.getActive_languages().add(lang);
        }
        saveConfig(config);
        return newVersion;
    }

    // Traductions publiees

    public Map<String, Object> readTranslation(String lang, String version) throws IOException {
        Path path = translationFile(lang, version);
        if (!Files.exists(path)) {
            throw new NoSuchFileException("Fichier introuvable : " + path);
        }
        return objectMapper.readValue(path.toFile(), new TypeReference<LinkedHashMap<String, Object>>() {});
    }

    public void saveTranslation(String lang, String version, Map<String, Object> content) throws IOException {
        objectMapper.writeValue(translationFile(lang, version).toFile(), content);
    }

    // Brouillons (Draft)

    public Map<String, Object> readDraft(String lang) throws IOException {
        Path path = draftFile(lang);
        if (!Files.exists(path)) {
            return new LinkedHashMap<>();
        }
        return objectMapper.readValue(path.toFile(), new TypeReference<LinkedHashMap<String, Object>>() {});
    }

    public void saveDraft(String lang, Map<String, Object> content) throws IOException {
        objectMapper.writeValue(draftFile(lang).toFile(), content);
    }

    public void deleteDraft(String lang) throws IOException {
        Files.deleteIfExists(draftFile(lang));
    }
}
