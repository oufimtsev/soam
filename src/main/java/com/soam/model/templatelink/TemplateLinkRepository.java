package com.soam.model.templatelink;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateLinkRepository extends CrudRepository<TemplateLink, Integer> {
    Iterable<TemplateLink> findAll(Sort sort);
    Iterable<TemplateLink> findBySpecificationTemplate(SpecificationTemplate specificationTemplate, Sort sort);
    Iterable<TemplateLink> findByStakeholderTemplate(StakeholderTemplate stakeholderTemplate, Sort sort);
    Iterable<TemplateLink> findBySpecificationTemplateAndStakeholderTemplate(
            SpecificationTemplate specificationTemplate, StakeholderTemplate stakeholderTemplate, Sort sort);
    Optional<TemplateLink> findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
            SpecificationTemplate specificationTemplate, StakeholderTemplate stakeholderTemplate,
            ObjectiveTemplate objectiveTemplate);
}
