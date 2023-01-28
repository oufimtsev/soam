package com.soam.web.specification;

import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
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
public class SpecificationController {
	private static final String ATTR_SPECIFICATION = "specification";
	private static final String ATTR_SPECIFICATIONS = "specifications";
	private static final String ATTR_PAGINATED = "paginated";
	private static final String ATTR_CURRENT_PAGE = "currentPage";
	private static final String ATTR_TOTAL_PAGES = "totalPages";
	private static final String ATTR_TOTAL_ITEMS = "totalItems";

	public static final String  VIEW_FIND_SPECIFICATION = "specification/findSpecification";

	private final SpecificationRepository specificationRepository;

	public SpecificationController(SpecificationRepository specificationRepository) {
		this.specificationRepository = specificationRepository;
	}


	@GetMapping("/specification/find")
	public String initFindForm(Map<String, Object> model) {
		model.put(ATTR_SPECIFICATION, new Specification());
		return VIEW_FIND_SPECIFICATION;
	}

	@GetMapping("/specifications")
	public String processFindForm( @RequestParam(defaultValue = "1") int page, Specification specification,
			BindingResult result, Model model ) {

		if ( StringUtils.isEmpty(specification.getName())) {
			result.rejectValue("name", "notBlank", "not blank");
			model.addAttribute(ATTR_SPECIFICATION, specification);
			return VIEW_FIND_SPECIFICATION;
		}


		Page<Specification> specificationResults = findPaginatedForSpecificationName(page, specification.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			model.addAttribute(ATTR_SPECIFICATION, specification);
			return VIEW_FIND_SPECIFICATION;
		}

		if ( specificationResults.getTotalElements() == 1) {
			specification = specificationResults.iterator().next();
			return "redirect:/specification/" + specification.getId();
		}

		return addPaginationModel(page, model, specificationResults);
	}

	@GetMapping("/specification/list")
	public String listSpecificationTemplates( @RequestParam(defaultValue = "1") int page, Model model ){

		Page<Specification> specificationResults =
				findPaginatedForSpecificationName(page, "");
		addPaginationModel( page, model, specificationResults );

		model.addAttribute(ATTR_SPECIFICATION, new Specification()); // for breadcrumb
		return "specification/specificationList";
	}

	private String addPaginationModel(int page, Model model, Page<Specification> paginated) {
		model.addAttribute(ATTR_PAGINATED, paginated);
		List<Specification> specifications = paginated.getContent();
		model.addAttribute(ATTR_CURRENT_PAGE, page);
		model.addAttribute(ATTR_TOTAL_PAGES, paginated.getTotalPages());
		model.addAttribute(ATTR_TOTAL_ITEMS, paginated.getTotalElements());
		model.addAttribute(ATTR_SPECIFICATIONS, specifications);
		return "specification/specificationList";
	}

	private Page<Specification> findPaginatedForSpecificationName(int page, String name) {
		int pageSize = 20;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specificationRepository.findByNameStartsWithIgnoreCase(name, pageable);
	}


	@GetMapping("/specification/{specificationId}")
	public String showSpecification(@PathVariable("specificationId") int specificationId, Model model) {
		Optional<Specification> maybeSpecification = this.specificationRepository.findById(specificationId);
		if(maybeSpecification.isEmpty()){
			return "redirect:/specification/find";
		}
		model.addAttribute(maybeSpecification.get());
		return "specification/specificationDetails";
	}


}
