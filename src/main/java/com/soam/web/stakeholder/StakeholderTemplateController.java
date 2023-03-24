package com.soam.web.stakeholder;

import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Controller
public class StakeholderTemplateController implements SoamFormController {
	private final StakeholderTemplateService stakeholderTemplateService;

	public StakeholderTemplateController(StakeholderTemplateService stakeholderTemplateService) {
		this.stakeholderTemplateService = stakeholderTemplateService;
	}

	@GetMapping("/stakeholder/template/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, new StakeholderTemplate());
		return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
	}

	@PostMapping("/stakeholder/template/find")
	public String processFindForm(StakeholderTemplate stakeholderTemplate, BindingResult result, Model model) {
		if (StringUtils.isEmpty(stakeholderTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
		}

		List<StakeholderTemplate> stakeholderTemplates = stakeholderTemplateService.findByPrefix(stakeholderTemplate.getName());
		if (stakeholderTemplates.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
		}

		if (stakeholderTemplates.size() == 1) {
			return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, stakeholderTemplates.get(0).getId());
		}

		model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplates);
		return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_LIST;
	}

	@GetMapping("/stakeholder/template/list")
	public String listAll(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<StakeholderTemplate> stakeholderTemplateResults = stakeholderTemplateService.findAll(page - 1);
		addPaginationModel(page, model, stakeholderTemplateResults);
		model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, new StakeholderTemplate());
		return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_LIST;
	}

	private String addPaginationModel(int page, Model model, Page<StakeholderTemplate> paginated) {
		model.addAttribute(ModelConstants.ATTR_PAGINATED, paginated);
		List<StakeholderTemplate> stakeholderTemplates = paginated.getContent();
		model.addAttribute(ModelConstants.ATTR_CURRENT_PAGE, page);
		model.addAttribute(ModelConstants.ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ModelConstants.ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplates);
		return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_LIST;
	}
}
