package com.soam.model.templatelink;

import com.soam.model.BaseEntity;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.style.ToStringCreator;

/**
 * Simple JavaBean domain object representing a Template Link.
 */
@Entity
@Table(name = "template_links")
public class TemplateLink extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "specification_template_id")
    @NotNull
    private SpecificationTemplate specificationTemplate;

    @ManyToOne
    @JoinColumn(name = "stakeholder_template_id")
    @NotNull
    private StakeholderTemplate stakeholderTemplate;

    @ManyToOne
    @JoinColumn(name = "objective_template_id")
    @NotNull
    private ObjectiveTemplate objectiveTemplate;

    public SpecificationTemplate getSpecificationTemplate() {
        return specificationTemplate;
    }

    public void setSpecificationTemplate(SpecificationTemplate specificationTemplate) {
        this.specificationTemplate = specificationTemplate;
    }

    public StakeholderTemplate getStakeholderTemplate() {
        return stakeholderTemplate;
    }

    public void setStakeholderTemplate(StakeholderTemplate stakeholderTemplate) {
        this.stakeholderTemplate = stakeholderTemplate;
    }

    public ObjectiveTemplate getObjectiveTemplate() {
        return objectiveTemplate;
    }

    public void setObjectiveTemplate(ObjectiveTemplate objectiveTemplate) {
        this.objectiveTemplate = objectiveTemplate;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.getId()).append("new", this.isNew())
                .toString();
    }
}
