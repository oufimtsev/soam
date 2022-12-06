package com.soam.model;

import com.soam.model.priority.PriorityType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * Domain object that contains the replicated member data across the SOAM system.
 * It could be worth duplicating this in each class for easier reuse.
 */
@MappedSuperclass
public abstract class SoamEntity extends BaseEntity  {

    @Column(name = "name")
    @NotBlank
    @Length(max = 40)
    private String name;

    @Column(name = "description")
    @NotEmpty
    @Length(max = 80)
    private String description;

    @Column(name = "notes")
    @NotEmpty
    @Length(max = 255)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "priority_id")
    private PriorityType priority;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
