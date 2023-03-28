package com.soam.web.specification;

import com.soam.model.specification.Specification;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@ExceptionHandler(EntityNotFoundException.class)
	public String errorHandler() {
		return RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT;
	}
}
