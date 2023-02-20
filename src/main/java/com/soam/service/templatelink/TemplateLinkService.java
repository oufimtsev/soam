package com.soam.service.templatelink;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.service.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateLinkService {
    private static final Sort TEMPLATE_LINK_SORT = Sort.by(List.of(
            Sort.Order.by("specificationTemplate.name").ignoreCase(),
            Sort.Order.by("stakeholderTemplate.name").ignoreCase(),
            Sort.Order.by("objectiveTemplate.name").ignoreCase()
    ));

    private final TemplateLinkRepository templateLinkRepository;

    public TemplateLinkService(TemplateLinkRepository templateLinkRepository) {
        this.templateLinkRepository = templateLinkRepository;
    }

    public TemplateLink getById(int templateLinkId) {
        return templateLinkRepository.findById(templateLinkId)
                .orElseThrow(() -> new EntityNotFoundException("Template Link", templateLinkId));
    }

    public Iterable<TemplateLink> findBySpecificationTemplateAndStakeholderTemplate(
            SpecificationTemplate specificationTemplate, StakeholderTemplate stakeholderTemplate) {
        return templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplate(
                specificationTemplate, stakeholderTemplate, TEMPLATE_LINK_SORT);
    }

    public Iterable<TemplateLink> findBySpecificationTemplate(SpecificationTemplate specificationTemplate) {
        return templateLinkRepository.findBySpecificationTemplate(specificationTemplate, TEMPLATE_LINK_SORT);
    }

    public Iterable<TemplateLink> findByStakeholderTemplate(StakeholderTemplate stakeholderTemplate) {
        return templateLinkRepository.findByStakeholderTemplate(stakeholderTemplate, TEMPLATE_LINK_SORT);
    }

    public Iterable<TemplateLink> findAll() {
        return templateLinkRepository.findAll(TEMPLATE_LINK_SORT);
    }

    public Optional<TemplateLink> findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
            SpecificationTemplate specificationTemplate, StakeholderTemplate stakeholderTemplate,
            ObjectiveTemplate objectiveTemplate) {
        return templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                specificationTemplate, stakeholderTemplate, objectiveTemplate);
    }

    public TemplateLink save(TemplateLink templateLink) {
        return templateLinkRepository.save(templateLink);
    }

    public void delete(TemplateLink templateLink) {
        templateLinkRepository.delete(templateLink);
    }
}
