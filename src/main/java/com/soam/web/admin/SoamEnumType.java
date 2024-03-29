package com.soam.web.admin;

import com.soam.model.priority.PriorityType;
import com.soam.model.soamenum.SoamEnum;

public enum SoamEnumType {
    PriorityType(PriorityType.class);

    private final Class<? extends SoamEnum> clazz;

    SoamEnumType(Class<? extends SoamEnum> clazz) {
        this.clazz = clazz;
    }

    public int getId() {
        return ordinal();
    }

    public String getName() {
        return name();
    }

    public Class<? extends SoamEnum> getEnumClass() {
        return clazz;
    }
}
