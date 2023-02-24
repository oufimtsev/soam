package com.soam.model.priority;

import com.soam.model.soamenum.SoamEnum;
import jakarta.persistence.Entity;
import org.springframework.util.comparator.Comparators;

import java.util.Objects;

@Entity
public class PriorityType extends SoamEnum implements Comparable<PriorityType> {
    @Override
    public int compareTo(PriorityType o) {
        return Comparators.comparable().compare(getSequence(), o.getSequence());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriorityType that = (PriorityType) o;
        return getSequence().equals(that.getSequence());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSequence());
    }

    @Override
    public String toString() {
        return getName();
    }
}
