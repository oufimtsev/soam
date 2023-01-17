package com.soam.model.stakeholderobjective;

import java.util.Comparator;

public class StakeholderObjectiveComparator implements Comparator<StakeholderObjective> {
    @Override
    public int compare(StakeholderObjective so1, StakeholderObjective so2) {
        return so1.getSpecificationObjective().getName().compareTo(so2.getSpecificationObjective().getName());
    }
}
