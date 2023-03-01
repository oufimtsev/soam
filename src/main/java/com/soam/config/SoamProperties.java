package com.soam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "soam")
public class SoamProperties {
    private final String topEntityName;
    private final int pageSize;

    public SoamProperties(String topEntityName, int pageSize) {
        this.topEntityName = topEntityName;
        this.pageSize = pageSize;
    }

    public String getTopEntityName() {
        return topEntityName;
    }

    public int getPageSize() {
        return pageSize;
    }
}
