package com.soam.web.specifications;

import com.soam.service.specification.SpecificationService;
import com.soam.service.stakeholder.StakeholderService;
import com.soam.web.ViewConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class SpecificationsController {
	private final SpecificationService specificationService;
	private final StakeholderService stakeholderService;

	public SpecificationsController(
			SpecificationService specificationService, StakeholderService stakeholderService) {
		this.specificationService = specificationService;
		this.stakeholderService = stakeholderService;
	}

	@GetMapping("/specifications2")
	public String listAll(Model model) {
		return ViewConstants.VIEW_TREE;
	}

	@GetMapping("/specifications2/default")
	public String defaultView() {
		return ViewConstants.VIEW_TREE_DEFAULT;
	}

	@GetMapping("/tree/specification")
	@ResponseBody
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
	@ResponseBody
	public List<Map<String, String>> getSpecificationObjectives(@PathVariable("specificationId") int specificationId) {
		return specificationService.getById(specificationId).getSpecificationObjectives().stream()
				.map(specificationObjective -> Map.of(
						"id", String.valueOf(specificationObjective.getId()),
						"name", specificationObjective.getName(),
						"type", "specificationObjective"
				))
				.toList();
	}

	@GetMapping("/tree/specification/{specificationId}/stakeholder")
	@ResponseBody
	public List<Map<String, String>> getStakeholders(@PathVariable("specificationId") int specificationId) {
		return specificationService.getById(specificationId).getStakeholders().stream()
				.map(stakeholder -> Map.of(
						"id", String.valueOf(stakeholder.getId()),
						"name", stakeholder.getName(),
						"type", "stakeholder"
				))
				.toList();
	}

	@GetMapping("/tree/stakeholder/{stakeholderId}/stakeholderObjective")
	@ResponseBody
	public List<Map<String, String>> getStakeholderObjectives(@PathVariable("stakeholderId") int stakeholderId) {
		return stakeholderService.getById(stakeholderId).getStakeholderObjectives().stream()
				.map(stakeholderObjective -> Map.of(
						"id", String.valueOf(stakeholderObjective.getId()),
						"name", stakeholderObjective.getSpecificationObjective().getName(),
						"type", "stakeholderObjective"
				))
				.toList();
	}
}
