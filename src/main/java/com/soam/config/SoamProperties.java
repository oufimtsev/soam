package com.soam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "soam")
public class SoamProperties {
    private final String topEntityName;
    private final int pageSize;
    private final AccessMode accessMode;

    public SoamProperties(String topEntityName, int pageSize, AccessMode accessMode) {
        this.topEntityName = topEntityName;
        this.pageSize = pageSize;
        this.accessMode = accessMode;
    }

    public String getTopEntityName() {
        return topEntityName;
    }

    public int getPageSize() {
        return pageSize;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }
}
