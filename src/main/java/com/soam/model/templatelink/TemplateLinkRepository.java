package com.soam.model.templatelink;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TemplateLinkRepository extends CrudRepository<TemplateLink, Integer> {
    Optional<TemplateLink> findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
            SpecificationTemplate specificationTemplate, StakeholderTemplate stakeholderTemplate,
            ObjectiveTemplate objectiveTemplate);
}
