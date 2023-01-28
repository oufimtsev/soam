package com.soam.web.objective;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
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
public class ObjectiveTemplateController extends SoamFormController {
	private static final String ATTR_OBJECTIVE_TEMPLATE = "objectiveTemplate";
	private static final String ATTR_OBJECTIVE_TEMPLATES = "objectiveTemplates";
	private static final String ATTR_CURRENT_PAGE = "currentPage";
	private static final String ATTR_TOTAL_PAGES = "totalPages";
	private static final String ATTR_TOTAL_ITEMS = "totalItems";

	private static final String VIEW_FIND_OBJECTIVE_TEMPLATE = "objective/template/findObjectiveTemplate";

	private final ObjectiveTemplateRepository objectiveTemplateRepository;

	public ObjectiveTemplateController(ObjectiveTemplateRepository objectiveTemplateRepository) {
		this.objectiveTemplateRepository = objectiveTemplateRepository;
	}

	@GetMapping("/objective/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put(ATTR_OBJECTIVE_TEMPLATE, new ObjectiveTemplate());
		return VIEW_FIND_OBJECTIVE_TEMPLATE;
	}

	@GetMapping("/objective/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, ObjectiveTemplate objectiveTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(objectiveTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute(ATTR_OBJECTIVE_TEMPLATE, objectiveTemplate);
			return VIEW_FIND_OBJECTIVE_TEMPLATE;
		}

		Page<ObjectiveTemplate> objectiveResults = findPaginatedForObjectiveTemplateName(page, objectiveTemplate.getName());
		if (objectiveResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute(ATTR_OBJECTIVE_TEMPLATE, objectiveTemplate);
			return VIEW_FIND_OBJECTIVE_TEMPLATE;
		}

		if ( objectiveResults.getTotalElements() == 1) {
			objectiveTemplate = objectiveResults.iterator().next();
			return String.format( "redirect:/objective/template/%s/edit", objectiveTemplate.getId());
		}

		return addPaginationModel(page, model, objectiveResults);
	}


	@GetMapping("/objective/template/list")
	public String listObjectiveTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<ObjectiveTemplate> objectiveTemplateResults =
				findPaginatedForObjectiveTemplateName(page, "");
		addPaginationModel( page, model, objectiveTemplateResults );
		model.addAttribute(ATTR_OBJECTIVE_TEMPLATE, new ObjectiveTemplate());
		return "objective/template/objectiveTemplateList";
	}

	private String addPaginationModel(int page, Model model, Page<ObjectiveTemplate> paginated) {
		model.addAttribute("paginated", paginated);
		List<ObjectiveTemplate> objectiveTemplates = paginated.getContent();
		model.addAttribute(ATTR_CURRENT_PAGE, page);
		model.addAttribute(ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ATTR_OBJECTIVE_TEMPLATES, objectiveTemplates);
		return "objective/template/objectiveTemplateList";
	}

	private Page<ObjectiveTemplate> findPaginatedForObjectiveTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return objectiveTemplateRepository.findByNameStartsWithIgnoreCase(name, pageable);
	}



}
