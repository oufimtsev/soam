package com.soam.web.stakeholder;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.SoamFormController;
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
public class StakeholderTemplateController extends SoamFormController {


	private final StakeholderTemplateRepository stakeholderTemplates;
	private final PriorityRepository priorities;

	public StakeholderTemplateController(StakeholderTemplateRepository stakeholderTemplateRepository, PriorityRepository priorityRepository) {
		this.stakeholderTemplates = stakeholderTemplateRepository;
		this.priorities = priorityRepository;
	}



	@GetMapping("/stakeholder/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("stakeholderTemplate", new StakeholderTemplate());
		return "stakeholder/template/findStakeholderTemplate";
	}

	@GetMapping("/stakeholder/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, StakeholderTemplate stakeholderTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(stakeholderTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute( "stakeholderTemplate", stakeholderTemplate);
			return "stakeholder/template/findStakeholderTemplate";
		}

		Page<StakeholderTemplate> stakeholderResults = findPaginatedForStakeholderTemplateName(page, stakeholderTemplate.getName());
		if (stakeholderResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute( "stakeholderTemplate", stakeholderTemplate);
			return "stakeholder/template/findStakeholderTemplate";
		}

		if ( stakeholderResults.getTotalElements() == 1) {
			stakeholderTemplate = stakeholderResults.iterator().next();
			return String.format( "redirect:/stakeholder/template/%s/edit", stakeholderTemplate.getId());
		}

		return addPaginationModel(page, model, stakeholderResults);
	}


	@GetMapping("/stakeholder/template/list")
	public String listStakeholderTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<StakeholderTemplate> stakeholderTemplateResults =
				findPaginatedForStakeholderTemplateName(page, "");
		addPaginationModel( page, model, stakeholderTemplateResults );
		model.addAttribute("stakeholderTemplate", new StakeholderTemplate());
		return "stakeholder/template/stakeholderTemplateList";
	}

	private String addPaginationModel(int page, Model model, Page<StakeholderTemplate> paginated) {
		model.addAttribute("paginated", paginated);
		List<StakeholderTemplate> listStakeholderTemplates = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listStakeholderTemplates", listStakeholderTemplates);
		return "stakeholder/template/stakeholderTemplateList";
	}

	private Page<StakeholderTemplate> findPaginatedForStakeholderTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return stakeholderTemplates.findByNameStartsWithIgnoreCase(name, pageable);
	}



}
