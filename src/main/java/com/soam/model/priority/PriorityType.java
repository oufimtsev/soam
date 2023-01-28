package com.soam.model.priority;

import com.soam.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.util.comparator.Comparators;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriorityType that = (PriorityType) o;
        return sequence.equals(that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }

    @Override
    public String toString() {
        return getName();
    }
}
