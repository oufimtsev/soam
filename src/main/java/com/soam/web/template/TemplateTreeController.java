package com.soam.web.template;

import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.service.templatelink.TemplateLinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestController
public class TemplateTreeController {
    private final SpecificationTemplateService specificationTemplateService;
    private final StakeholderTemplateService stakeholderTemplateService;
    private final ObjectiveTemplateService objectiveTemplateService;
    private final TemplateLinkService templateLinkService;

    public TemplateTreeController(
            SpecificationTemplateService specificationTemplateService,
            StakeholderTemplateService stakeholderTemplateService,
            ObjectiveTemplateService objectiveTemplateService, TemplateLinkService templateLinkService) {
        this.specificationTemplateService = specificationTemplateService;
        this.stakeholderTemplateService = stakeholderTemplateService;
        this.objectiveTemplateService = objectiveTemplateService;
        this.templateLinkService = templateLinkService;
    }

    @GetMapping("/tree/specificationTemplate")
    public List<Map<String, String>> getSpecificationTemplates() {
        return specificationTemplateService.findAll().stream()
                .map(specificationTemplate -> Map.of(
                        "id", String.valueOf(specificationTemplate.getId()),
                        "name", specificationTemplate.getName(),
                        "type", "specificationTemplate"
                ))
                .toList();
    }

    @GetMapping("/tree/stakeholderTemplate")
    public List<Map<String, String>> getStakeholderTemplates() {
        return stakeholderTemplateService.findAll().stream()
                .map(stakeholderTemplate -> Map.of(
                        "id", String.valueOf(stakeholderTemplate.getId()),
                        "name", stakeholderTemplate.getName(),
                        "type", "stakeholderTemplate"
                ))
                .toList();
    }

    @GetMapping("/tree/objectiveTemplate")
    public List<Map<String, String>> getObjectiveTemplates() {
        return objectiveTemplateService.findAll().stream()
                .map(objectiveTemplate -> Map.of(
                        "id", String.valueOf(objectiveTemplate.getId()),
                        "name", objectiveTemplate.getName(),
                        "type", "objectiveTemplate"
                ))
                .toList();
    }

    @GetMapping("/tree/link/templateLink")
    public List<Map<String, String>> getTemplateLinks() {
        return StreamSupport.stream(templateLinkService.findAll().spliterator(), false)
                .map(templateLink -> Map.of(
                        "id", String.valueOf(templateLink.getId()),
                        "name", templateLink.getSpecificationTemplate().getName() + " / " + templateLink.getStakeholderTemplate().getName() + " / " + templateLink.getObjectiveTemplate().getName(),
                        "type", "templateLink"
                ))
                .distinct()
                .toList();
    }
}
