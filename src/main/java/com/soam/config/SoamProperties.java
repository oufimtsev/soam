package com.soam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "soam")
public class SoamProperties {
    private final String topEntityName;

    public SoamProperties(String topEntityName) {
        this.topEntityName = topEntityName;
    }

    public String getTopEntityName() {
        return topEntityName;
    }
}
