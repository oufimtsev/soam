package com.soam.web.objective;

import com.soam.model.objective.Objective;
import com.soam.model.objective.ObjectiveRepository;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class ObjectiveController {
	private final ObjectiveRepository objectives;
	private final ObjectiveTemplateRepository objectiveTemplates;
	private final PriorityRepository priorities;

	public ObjectiveController(ObjectiveRepository objectiveRepository, PriorityRepository priorityRepository, ObjectiveTemplateRepository objectiveTemplateRepository) {
		this.objectives = objectiveRepository;
		this.objectiveTemplates = objectiveTemplateRepository;
		this.priorities = priorityRepository;
	}


	@GetMapping("/objective/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("objective", new Objective());
		return "objective/findObjective";
	}

	@GetMapping("/objectives")
	public String processFindForm( @RequestParam(defaultValue = "1") int page, Objective objective,
			BindingResult result, Model model ) {

		if ( StringUtils.isEmpty(objective.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute( "objective", objective );
			return "objective/findObjective";
		}


		Page<Objective> objectiveResults = findPaginatedForObjectiveName(page, objective.getName());
		if (objectiveResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute( "objective", objective );
			return "objective/findObjective";
		}

		if ( objectiveResults.getTotalElements() == 1) {
			objective = objectiveResults.iterator().next();
			return "redirect:/objective/" + objective.getId();
		}

		return addPaginationModel(page, model, objectiveResults);
	}

	@GetMapping("/objective/list")
	public String listObjectiveTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<Objective> objectiveResults =
				findPaginatedForObjectiveName(page, "");
		addPaginationModel( page, model, objectiveResults );
		return "objective/objectiveList";
	}

	private String addPaginationModel(int page, Model model, Page<Objective> paginated) {
		model.addAttribute("paginated", paginated);
		List<Objective> listObjectives = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listObjectives", listObjectives);
		return "objective/objectiveList";
	}

	private Page<Objective> findPaginatedForObjectiveName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return objectives.findByNameStartsWithIgnoreCase(name, pageable);
	}


	@GetMapping("/objective/{objectiveId}")
	public String showObjective(@PathVariable("objectiveId") int objectiveId, Model model) {
		Optional<Objective> maybeObjective = this.objectives.findById(objectiveId);
		if(maybeObjective.isEmpty()){
			return "redirect:/objective/find";
		}
		model.addAttribute(maybeObjective.get());
		return "objective/objectiveDetails";
	}


}
