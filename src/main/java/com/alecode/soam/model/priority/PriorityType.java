package com.alecode.soam.model.priority;

import com.alecode.soam.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.util.comparator.Comparators;

@Entity
@Table(name = "priority_types")
//todo: This should be an enum.
public class PriorityType extends BaseEntity implements Comparable<PriorityType> {
    private String name;
    private Integer sequence; // todo: make unique? use as a weight?

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public int compareTo(PriorityType o) {
        return Comparators.comparable().compare(this.getSequence(), o.getSequence());
    }

    @Override
    public String toString() {
        return getName();
    }
}
