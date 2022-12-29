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


	private final ObjectiveTemplateRepository objectiveTemplates;

	public ObjectiveTemplateController(ObjectiveTemplateRepository objectiveTemplateRepository) {
		this.objectiveTemplates = objectiveTemplateRepository;
	}



	@GetMapping("/objective/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("objectiveTemplate", new ObjectiveTemplate());
		return "objective/template/findObjectiveTemplate";
	}

	@GetMapping("/objective/templates")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, ObjectiveTemplate objectiveTemplate,
								  BindingResult result, Model model) {

		if ( StringUtils.isEmpty(objectiveTemplate.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute( "objectiveTemplate", objectiveTemplate);
			return "objective/template/findObjectiveTemplate";
		}

		Page<ObjectiveTemplate> objectiveResults = findPaginatedForObjectiveTemplateName(page, objectiveTemplate.getName());
		if (objectiveResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute( "objectiveTemplate", objectiveTemplate);
			return "objective/template/findObjectiveTemplate";
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
		model.addAttribute("objectiveTemplate", new ObjectiveTemplate());
		return "objective/template/objectiveTemplateList";
	}

	private String addPaginationModel(int page, Model model, Page<ObjectiveTemplate> paginated) {
		model.addAttribute("paginated", paginated);
		List<ObjectiveTemplate> listObjectiveTemplates = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listObjectiveTemplates", listObjectiveTemplates);
		return "objective/template/objectiveTemplateList";
	}

	private Page<ObjectiveTemplate> findPaginatedForObjectiveTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return objectiveTemplates.findByNameStartsWithIgnoreCase(name, pageable);
	}



}
