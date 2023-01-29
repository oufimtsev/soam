package com.soam.web.specification;

import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;

@Controller
public class SpecificationController {
	@Value("${soam.pageSize}")
	private int pageSize;
	private final SpecificationRepository specificationRepository;

	public SpecificationController(SpecificationRepository specificationRepository) {
		this.specificationRepository = specificationRepository;
	}

	@GetMapping("/specification/find")
	public String initFindForm(Model model) {
		model.addAttribute(ModelConstants.ATTR_SPECIFICATION, new Specification());
		return ViewConstants.VIEW_FIND_SPECIFICATION;
	}

	@GetMapping("/specifications")
	public String processFindForm(
			@RequestParam(defaultValue = "1") int page, Specification specification, BindingResult result, Model model) {

		if (StringUtils.isEmpty(specification.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			return ViewConstants.VIEW_FIND_SPECIFICATION;
		}

		Page<Specification> specificationResults = findPaginatedForSpecificationName(page, specification.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
			return ViewConstants.VIEW_FIND_SPECIFICATION;
		}

		if (specificationResults.getTotalElements() == 1) {
			specification = specificationResults.iterator().next();
			return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
		}

		return addPaginationModel(page, model, specificationResults);
	}

	@GetMapping("/specification/list")
	public String listAll(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<Specification> specificationResults =
				findPaginatedForSpecificationName(page, "");
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

	private Page<Specification> findPaginatedForSpecificationName(int page, String name) {
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specificationRepository.findByNameStartsWithIgnoreCase(name, pageable);
	}

	@GetMapping("/specification/{specificationId}")
	public String showDetails(@PathVariable("specificationId") int specificationId, Model model) {
		Optional<Specification> maybeSpecification = specificationRepository.findById(specificationId);
		if (maybeSpecification.isEmpty()) {
			return RedirectConstants.REDIRECT_FIND_SPECIFICATION;
		}
		model.addAttribute(maybeSpecification.get());
		return ViewConstants.VIEW_SPECIFICATION_DETAILS;
	}
}
