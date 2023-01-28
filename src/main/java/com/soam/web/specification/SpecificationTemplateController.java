package com.soam.web.specification;

import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
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
public class SpecificationTemplateController extends SoamFormController {
	private static final String ATTR_SPECIFICATION_TEMPLATE = "specificationTemplate";
	private static final String ATTR_SPECIFICATION_TEMPLATES = "specificationTemplates";
	private static final String ATTR_PAGINATED = "paginated";
	private static final String ATTR_CURRENT_PAGE = "currentPage";
	private static final String ATTR_TOTAL_PAGES = "totalPages";
	private static final String ATTR_TOTAL_ITEMS = "totalItems";

	public static final String VIEW_FIND_SPECIFICATION_TEMPLATE =  "specification/template/findSpecificationTemplate";

	private final SpecificationTemplateRepository specificationTemplateRepository;

	public SpecificationTemplateController(SpecificationTemplateRepository specificationTemplateRepository) {
		this.specificationTemplateRepository = specificationTemplateRepository;
	}

	@GetMapping("/specification/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put(ATTR_SPECIFICATION_TEMPLATE, new SpecificationTemplate());
		return VIEW_FIND_SPECIFICATION_TEMPLATE;
	}

	@GetMapping("/specification/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, SpecificationTemplate specificationTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(specificationTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute(ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
			return VIEW_FIND_SPECIFICATION_TEMPLATE;
		}

		Page<SpecificationTemplate> specificationResults = findPaginatedForSpecificationTemplateName(page, specificationTemplate.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute(ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
			return VIEW_FIND_SPECIFICATION_TEMPLATE;
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
		model.addAttribute(ATTR_SPECIFICATION_TEMPLATE, new SpecificationTemplate());
		return "specification/template/specificationTemplateList";
	}

	private String addPaginationModel(int page, Model model, Page<SpecificationTemplate> paginated) {
		model.addAttribute(ATTR_PAGINATED, paginated);
		List<SpecificationTemplate> specificationTemplates = paginated.getContent();
		model.addAttribute(ATTR_CURRENT_PAGE, page);
		model.addAttribute(ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ATTR_SPECIFICATION_TEMPLATES, specificationTemplates);
		return "specification/template/specificationTemplateList";
	}

	private Page<SpecificationTemplate> findPaginatedForSpecificationTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specificationTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
	}



}
