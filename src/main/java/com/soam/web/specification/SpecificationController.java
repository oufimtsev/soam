package com.soam.web.specification;

import com.soam.model.specification.Specification;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Controller
public class SpecificationController {
	private final SpecificationService specificationService;

	public SpecificationController(SpecificationService specificationService) {
		this.specificationService = specificationService;
	}

	@GetMapping("/specifications")
	public String defaultTree() {
		return ViewConstants.VIEW_SPECIFICATION_DEFAULT;
	}

	@GetMapping("/specification/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_SPECIFICATION, new Specification());
		return ViewConstants.VIEW_FIND_SPECIFICATION;
	}

	@PostMapping("/specification/find")
	public String processFindForm(Specification specification, BindingResult result, Model model) {
		if (StringUtils.isEmpty(specification.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_SPECIFICATION;
		}

		List<Specification> specifications = specificationService.findByPrefix(specification.getName());
		if (specifications.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return ViewConstants.VIEW_FIND_SPECIFICATION;
		}

		if (specifications.size() == 1) {
			return String.format(RedirectConstants.REDIRECT_SPECIFICATION_EDIT, specifications.get(0).getId());
		}

		model.addAttribute(ModelConstants.ATTR_SPECIFICATIONS, specifications);
		return ViewConstants.VIEW_SPECIFICATION_LIST;
	}

	@GetMapping("/specification/list")
	public String listAll(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<Specification> specificationResults = specificationService.findAll(page - 1);
		addPaginationModel(page, model, specificationResults);

		model.addAttribute(ModelConstants.ATTR_SPECIFICATION, new Specification()); // for breadcrumb
		return ViewConstants.VIEW_SPECIFICATION_LIST;
	}

	private String addPaginationModel(int page, Model model, Page<Specification> paginated) {
		model.addAttribute(ModelConstants.ATTR_PAGINATED, paginated);
		List<Specification> specifications = paginated.getContent();
		model.addAttribute(ModelConstants.ATTR_CURRENT_PAGE, page);
		model.addAttribute(ModelConstants.ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ModelConstants.ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ModelConstants.ATTR_SPECIFICATIONS, specifications);
		return ViewConstants.VIEW_SPECIFICATION_LIST;
	}

	@GetMapping("/specification/{specificationId}")
	public String showDetails(@PathVariable("specificationId") int specificationId, Model model) {
		Specification specification = specificationService.getById(specificationId);
		model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
		return ViewConstants.VIEW_SPECIFICATION_DETAILS;
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public String errorHandler(RedirectAttributes redirectAttributes) {
		return RedirectConstants.REDIRECT_FIND_SPECIFICATION;
	}
}
