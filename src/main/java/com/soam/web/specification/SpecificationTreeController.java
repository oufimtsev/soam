package com.soam.web.specification;

import com.soam.service.specification.SpecificationService;
import com.soam.service.stakeholder.StakeholderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SpecificationTreeController {
	private final SpecificationService specificationService;
	private final StakeholderService stakeholderService;

	public SpecificationTreeController(
			SpecificationService specificationService, StakeholderService stakeholderService) {
		this.specificationService = specificationService;
		this.stakeholderService = stakeholderService;
	}

	@GetMapping("/tree/specification")
	public List<Map<String, String>> getSpecifications() {
		return specificationService.findAll().stream()
				.map(specification -> Map.of(
						"id", String.valueOf(specification.getId()),
						"name", specification.getName(),
						"type", "specification"
				))
				.toList();
	}

	@GetMapping("/tree/specification/{specificationId}/specificationObjective")
	public List<Map<String, String>> getSpecificationObjectives(@PathVariable("specificationId") int specificationId) {
		return specificationService.getById(specificationId).getSpecificationObjectives().stream()
				.map(specificationObjective -> Map.of(
						"id", String.valueOf(specificationObjective.getId()),
						"specificationId", String.valueOf(specificationObjective.getSpecification().getId()),
						"name", specificationObjective.getName(),
						"type", "specificationObjective"
				))
				.toList();
	}

	@GetMapping("/tree/specification/{specificationId}/stakeholder")
	public List<Map<String, String>> getStakeholders(@PathVariable("specificationId") int specificationId) {
		return specificationService.getById(specificationId).getStakeholders().stream()
				.map(stakeholder -> Map.of(
						"id", String.valueOf(stakeholder.getId()),
						"specificationId", String.valueOf(stakeholder.getSpecification().getId()),
						"name", stakeholder.getName(),
						"type", "stakeholder"
				))
				.toList();
	}

	@GetMapping("/tree/stakeholder/{stakeholderId}/stakeholderObjective")
	public List<Map<String, String>> getStakeholderObjectives(@PathVariable("stakeholderId") int stakeholderId) {
		return stakeholderService.getById(stakeholderId).getStakeholderObjectives().stream()
				.map(stakeholderObjective -> Map.of(
						"id", String.valueOf(stakeholderObjective.getId()),
						"specificationId", String.valueOf(stakeholderObjective.getSpecificationObjective().getSpecification().getId()),
						"stakeholderId", String.valueOf(stakeholderObjective.getStakeholder().getId()),
						"name", stakeholderObjective.getSpecificationObjective().getName(),
						"type", "stakeholderObjective"
				))
				.toList();
	}
}
