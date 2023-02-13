package com.soam.web.stakeholder;

import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Controller
public class StakeholderTemplateController implements SoamFormController {
	@Value("${soam.pageSize}")
	private int pageSize;
	private final StakeholderTemplateService stakeholderTemplateService;

	public StakeholderTemplateController(StakeholderTemplateService stakeholderTemplateService) {
		this.stakeholderTemplateService = stakeholderTemplateService;
	}

	@GetMapping("/stakeholder/template/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, new StakeholderTemplate());
		return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
	}

	@GetMapping("/stakeholder/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, StakeholderTemplate stakeholderTemplate,
		  	BindingResult result, Model model) {
		if (StringUtils.isEmpty(stakeholderTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
		}

		Page<StakeholderTemplate> stakeholderResults = stakeholderTemplateService.findByPrefix(stakeholderTemplate.getName(), page - 1);
		if (stakeholderResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
		}

		if (stakeholderResults.getTotalElements() == 1) {
			stakeholderTemplate = stakeholderResults.iterator().next();
			return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, stakeholderTemplate.getId());
		}

		return addPaginationModel(page, model, stakeholderResults);
	}

	@GetMapping("/stakeholder/template/list")
	public String listAll(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<StakeholderTemplate> stakeholderTemplateResults = stakeholderTemplateService.findByPrefix("", page - 1);
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
