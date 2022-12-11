package com.soam.web;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
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
public class SpecificationTemplateController extends SoamFormController {


	private final SpecificationTemplateRepository specificationTemplates;
	private final PriorityRepository priorities;

	public SpecificationTemplateController(SpecificationTemplateRepository specificationTemplateRepository,  PriorityRepository priorityRepository) {
		this.specificationTemplates = specificationTemplateRepository;
		this.priorities = priorityRepository;
	}



	@GetMapping("/specification/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("specificationTemplate", new SpecificationTemplate());
		return "specification/template/findSpecificationTemplate";
	}

	@GetMapping("/specification/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, SpecificationTemplate specificationTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(specificationTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute( "specificationTemplate", specificationTemplate);
			return "specification/template/findSpecificationTemplate";
		}

		Page<SpecificationTemplate> specificationResults = findPaginatedForSpecificationTemplateName(page, specificationTemplate.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute( "specificationTemplate", specificationTemplate);
			return "specification/template/findSpecificationTemplate";
		}

		if ( specificationResults.getTotalElements() == 1) {
			specificationTemplate = specificationResults.iterator().next();
			return String.format( "redirect:/specification/template/%s/edit", specificationTemplate.getId());
		}

		return addPaginationModel(page, model, specificationResults);
	}


	@GetMapping("/specification/template/list")
	public String listSpecificationTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<SpecificationTemplate> specificationTemplateResults =
				findPaginatedForSpecificationTemplateName(page, "");
		addPaginationModel( page, model, specificationTemplateResults );
		return "specification/template/specificationTemplateList";
	}

	private String addPaginationModel(int page, Model model, Page<SpecificationTemplate> paginated) {
		model.addAttribute("paginated", paginated);
		List<SpecificationTemplate> listSpecificationTemplates = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listSpecificationTemplates", listSpecificationTemplates);
		return "specification/template/specificationTemplateList";
	}

	private Page<SpecificationTemplate> findPaginatedForSpecificationTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specificationTemplates.findByNameStartsWithIgnoreCase(name, pageable);
	}



}
