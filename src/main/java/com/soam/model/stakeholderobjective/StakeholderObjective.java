package com.soam.model.stakeholderobjective;

import com.soam.model.BaseEntity;
import com.soam.model.priority.PriorityType;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/**
 * Simple JavaBean domain object representing a stakeholder objective.
 */
@Entity
@Table(name = "stakeholder_objectives")
public class StakeholderObjective extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "stakeholder_id")
    private Stakeholder stakeholder;

    @ManyToOne
    @JoinColumn(name = "specification_objective_id")
    private SpecificationObjective specificationObjective;

    @Column(name = "notes")
    @NotBlank
    @Length(max = 255)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "priority_id")
    private PriorityType priority;

    public Stakeholder getStakeholder() {
        return stakeholder;
    }

    public void setStakeholder(Stakeholder stakeholder) {
        this.stakeholder = stakeholder;
    }

    public SpecificationObjective getSpecificationObjective() {
        return specificationObjective;
    }

    public void setSpecificationObjective(SpecificationObjective specificationObjective) {
        this.specificationObjective = specificationObjective;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public PriorityType getPriority() {
        return priority;
    }

    public void setPriority(PriorityType priority) {
        this.priority = priority;
    }
}
