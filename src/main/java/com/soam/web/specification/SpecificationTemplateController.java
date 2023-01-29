package com.soam.web.specification;

import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
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

import static com.soam.web.ModelConstants.ATTR_SPECIFICATION_TEMPLATE;

@Controller
public class SpecificationTemplateController implements SoamFormController {
	private final SpecificationTemplateRepository specificationTemplateRepository;

	public SpecificationTemplateController(SpecificationTemplateRepository specificationTemplateRepository) {
		this.specificationTemplateRepository = specificationTemplateRepository;
	}

	@GetMapping("/specification/template/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, new SpecificationTemplate());
		return ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE;
	}

	@GetMapping("/specification/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, SpecificationTemplate specificationTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(specificationTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute(ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
			return ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE;
		}

		Page<SpecificationTemplate> specificationResults = findPaginatedForSpecificationTemplateName(page, specificationTemplate.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute(ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
			return ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE;
		}

		if ( specificationResults.getTotalElements() == 1) {
			specificationTemplate = specificationResults.iterator().next();
			return String.format(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_EDIT, specificationTemplate.getId());
		}

		return addPaginationModel(page, model, specificationResults);
	}

	@GetMapping("/specification/template/list")
	public String listAll( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<SpecificationTemplate> specificationTemplateResults =
				findPaginatedForSpecificationTemplateName(page, "");
		addPaginationModel( page, model, specificationTemplateResults );
		model.addAttribute(ATTR_SPECIFICATION_TEMPLATE, new SpecificationTemplate());
		return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_LIST;
	}

	private String addPaginationModel(int page, Model model, Page<SpecificationTemplate> paginated) {
		model.addAttribute(ModelConstants.ATTR_PAGINATED, paginated);
		List<SpecificationTemplate> specificationTemplates = paginated.getContent();
		model.addAttribute(ModelConstants.ATTR_CURRENT_PAGE, page);
		model.addAttribute(ModelConstants.ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ModelConstants.ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplates);
		return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_LIST;
	}

	private Page<SpecificationTemplate> findPaginatedForSpecificationTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specificationTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
	}
}
