package com.example.backendwording.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String path = "storage";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

