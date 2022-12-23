package com.soam.web.stakeholder;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
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
public class StakeholderController {
	private final StakeholderRepository stakeholders;
	private final StakeholderTemplateRepository stakeholderTemplates;
	private final SpecificationRepository specificationRepository;
	private final PriorityRepository priorities;

	public StakeholderController(StakeholderRepository stakeholders, StakeholderTemplateRepository stakeholderTemplates, SpecificationRepository specificationRepository, PriorityRepository priorities) {
		this.stakeholders = stakeholders;
		this.stakeholderTemplates = stakeholderTemplates;
		this.specificationRepository = specificationRepository;
		this.priorities = priorities;
	}


	@GetMapping("/stakeholder/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("stakeholder", new Stakeholder());
		return "stakeholder/findStakeholder";
	}

	@GetMapping("/stakeholders")
	public String processFindForm( @RequestParam(defaultValue = "1") int page, Stakeholder stakeholder,
			BindingResult result, Model model ) {

		if ( StringUtils.isEmpty(stakeholder.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute( "stakeholder", stakeholder );
			return "stakeholder/findStakeholder";
		}


		Page<Stakeholder> stakeholderResults = findPaginatedForStakeholderName(page, stakeholder.getName());
		if (stakeholderResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute( "stakeholder", stakeholder );
			return "stakeholder/findStakeholder";
		}

		if ( stakeholderResults.getTotalElements() == 1) {
			stakeholder = stakeholderResults.iterator().next();
			return String.format("redirect:/specification/%s/stakeholder/%s",stakeholder.getSpecification().getId(),
					stakeholder.getId());
		}

		return addPaginationModel(page, model, stakeholderResults);
	}

	@GetMapping("/stakeholder/list")
	public String listStakeholderTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<Stakeholder> stakeholderResults =
				findPaginatedForStakeholderName(page, "");
		addPaginationModel( page, model, stakeholderResults );
		return "stakeholder/stakeholderList";
	}

	private String addPaginationModel(int page, Model model, Page<Stakeholder> paginated) {
		model.addAttribute("paginated", paginated);
		List<Stakeholder> listStakeholders = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listStakeholders", listStakeholders);
		return "stakeholder/stakeholderList";
	}

	private Page<Stakeholder> findPaginatedForStakeholderName(int page, String name) {
		int pageSize = 20;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return stakeholders.findByNameStartsWithIgnoreCase(name, pageable);
	}
}
