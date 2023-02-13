package com.soam.web.objective;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Controller
public class ObjectiveTemplateController implements SoamFormController {
	private final ObjectiveTemplateService objectiveTemplateService;

	public ObjectiveTemplateController(ObjectiveTemplateService objectiveTemplateService) {
		this.objectiveTemplateService = objectiveTemplateService;
	}

	@GetMapping("/objective/template/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, new ObjectiveTemplate());
		return ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE;
	}

	@GetMapping("/objective/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, ObjectiveTemplate objectiveTemplate,
		  	BindingResult result, Model model) {
		if (StringUtils.isEmpty(objectiveTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE;
		}

		Page<ObjectiveTemplate> objectiveResults = objectiveTemplateService.findByPrefix(objectiveTemplate.getName(), page - 1);
		if (objectiveResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE;
		}

		if (objectiveResults.getTotalElements() == 1) {
			objectiveTemplate = objectiveResults.iterator().next();
			return String.format(RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_EDIT, objectiveTemplate.getId());
		}

		return addPaginationModel(page, model, objectiveResults);
	}

	@GetMapping("/objective/template/list")
	public String listAll(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<ObjectiveTemplate> objectiveTemplateResults = objectiveTemplateService.findByPrefix("", page - 1);
		addPaginationModel(page, model, objectiveTemplateResults);
		model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, new ObjectiveTemplate());
		return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_LIST;
	}

	private String addPaginationModel(int page, Model model, Page<ObjectiveTemplate> paginated) {
		model.addAttribute(ModelConstants.ATTR_PAGINATED, paginated);
		List<ObjectiveTemplate> objectiveTemplates = paginated.getContent();
		model.addAttribute(ModelConstants.ATTR_CURRENT_PAGE, page);
		model.addAttribute(ModelConstants.ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ModelConstants.ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplates);
		return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_LIST;
	}
}
