package com.soam.web.specification;

import com.soam.model.specification.SpecificationTemplate;
import com.soam.service.specification.SpecificationTemplateService;
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
public class SpecificationTemplateController implements SoamFormController {
	private final SpecificationTemplateService specificationTemplateService;

	public SpecificationTemplateController(SpecificationTemplateService specificationTemplateService) {
		this.specificationTemplateService = specificationTemplateService;
	}

	@GetMapping("/templates")
	public String defaultTree(Model model) {
		return ViewConstants.VIEW_TEMPLATE_DEFAULT;
	}

	@GetMapping("/specification/template/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, new SpecificationTemplate());
		return ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE;
	}

	@PostMapping("/specification/template/find")
	public String processFindForm(SpecificationTemplate specificationTemplate,
		  	BindingResult result, Model model) {
		if (StringUtils.isEmpty(specificationTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE;
		}

		List<SpecificationTemplate> specificationTemplates = specificationTemplateService.findByPrefix(specificationTemplate.getName());
		if (specificationTemplates.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE;
		}

		if (specificationTemplates.size() == 1) {
			return String.format(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_EDIT, specificationTemplates.get(0).getId());
		}

		model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplates);
		return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_LIST;
	}
}
