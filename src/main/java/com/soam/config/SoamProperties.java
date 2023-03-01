package com.soam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "soam")
public class SoamProperties {
    private final String topEntityName;
    private final int pageSize;
    private final AccessType accessType;

    public SoamProperties(String topEntityName, int pageSize, AccessType accessType) {
        this.topEntityName = topEntityName;
        this.pageSize = pageSize;
        this.accessType = accessType;
    }

    public String getTopEntityName() {
        return topEntityName;
    }

    public int getPageSize() {
        return pageSize;
    }

    public AccessType getAccessType() {
        return accessType;
    }
}
