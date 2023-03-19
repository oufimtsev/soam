package com.soam.web.template;

import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.service.stakeholder.StakeholderService;
import com.soam.service.stakeholder.StakeholderTemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TemplateTreeController {
	private final SpecificationTemplateService specificationTemplateService;
	private final StakeholderTemplateService stakeholderTemplateService;
	private final ObjectiveTemplateService objectiveTemplateService;

	public TemplateTreeController(
			SpecificationTemplateService specificationTemplateService,
			StakeholderTemplateService stakeholderTemplateService,
			ObjectiveTemplateService objectiveTemplateService) {
		this.specificationTemplateService = specificationTemplateService;
		this.stakeholderTemplateService = stakeholderTemplateService;
		this.objectiveTemplateService = objectiveTemplateService;
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
				.map(specificationTemplate -> Map.of(
						"id", String.valueOf(specificationTemplate.getId()),
						"name", specificationTemplate.getName(),
						"type", "stakeholderTemplate"
				))
				.toList();
	}

	@GetMapping("/tree/objectiveTemplate")
	public List<Map<String, String>> getObjectiveTemplates() {
		return objectiveTemplateService.findAll().stream()
				.map(specificationTemplate -> Map.of(
						"id", String.valueOf(specificationTemplate.getId()),
						"name", specificationTemplate.getName(),
						"type", "objectiveTemplate"
				))
				.toList();
	}
}
