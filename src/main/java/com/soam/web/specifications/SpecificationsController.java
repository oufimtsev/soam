package com.soam.web.specifications;

import com.soam.model.specification.Specification;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.web.ModelConstants;
import com.soam.web.ViewConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SpecificationsController {
	private final SpecificationService specificationService;
	private final PriorityService priorityService;

	public SpecificationsController(SpecificationService specificationService, PriorityService priorityService) {
		this.specificationService = specificationService;
		this.priorityService = priorityService;
	}

	@GetMapping("/specifications2")
	public String listAll(Model model) {
		List<Specification> specifications = specificationService.findAll();
		model.addAttribute(ModelConstants.ATTR_SPECIFICATIONS, specifications);
		return ViewConstants.VIEW_TREE;
	}

	@GetMapping("/specifications2/default")
	public String defaultView() {
		return ViewConstants.VIEW_TREE_DEFAULT;
	}

//	@GetMapping("/tree/specification/{specificationId}")
//	public String specificationDetails(@PathVariable("specificationId") int specificationId, Model model) {
//		Specification specification = specificationService.getById(specificationId);
//		model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
//		model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
//		return ViewConstants.VIEW_TREE_SPECIFICATION;
//	}
}
