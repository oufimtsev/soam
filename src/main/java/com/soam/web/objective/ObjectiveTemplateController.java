package com.soam.web.objective;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/objective/template/find")
	public String processFindForm(ObjectiveTemplate objectiveTemplate, BindingResult result, Model model) {
		if (StringUtils.isEmpty(objectiveTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE;
		}

		List<ObjectiveTemplate> objectiveTemplates = objectiveTemplateService.findByPrefix(objectiveTemplate.getName());
		if (objectiveTemplates.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE;
		}

		if (objectiveTemplates.size() == 1) {
			return String.format(RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_EDIT, objectiveTemplates.get(0).getId());
		}

		model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplates);
		return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_LIST;
	}
}
