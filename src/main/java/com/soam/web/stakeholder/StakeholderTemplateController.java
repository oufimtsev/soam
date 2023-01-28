package com.soam.web.stakeholder;

import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Map;

@Controller
public class StakeholderTemplateController implements SoamFormController {
	private final StakeholderTemplateRepository stakeholderTemplateRepository;

	public StakeholderTemplateController(StakeholderTemplateRepository stakeholderTemplateRepository) {
		this.stakeholderTemplateRepository = stakeholderTemplateRepository;
	}

	@GetMapping("/stakeholder/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, new StakeholderTemplate());
		return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
	}

	@GetMapping("/stakeholder/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, StakeholderTemplate stakeholderTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(stakeholderTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, stakeholderTemplate);
			return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
		}

		Page<StakeholderTemplate> stakeholderResults = findPaginatedForStakeholderTemplateName(page, stakeholderTemplate.getName());
		if (stakeholderResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, stakeholderTemplate);
			return ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE;
		}

		if ( stakeholderResults.getTotalElements() == 1) {
			stakeholderTemplate = stakeholderResults.iterator().next();
			return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, stakeholderTemplate.getId());
		}

		return addPaginationModel(page, model, stakeholderResults);
	}

	@GetMapping("/stakeholder/template/list")
	public String listStakeholderTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<StakeholderTemplate> stakeholderTemplateResults =
				findPaginatedForStakeholderTemplateName(page, "");
		addPaginationModel( page, model, stakeholderTemplateResults );
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

	private Page<StakeholderTemplate> findPaginatedForStakeholderTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
	}
}
