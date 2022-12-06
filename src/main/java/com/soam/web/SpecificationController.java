/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soam.web;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplateRepository;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class SpecificationController {

	private static final String VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM = "specification/addUpdateSpecification";

	private final SpecificationRepository specifications;
	private final SpecificationTemplateRepository specificationTemplates;
	private final PriorityRepository priorities;

	public SpecificationController(SpecificationRepository specificationRepository, PriorityRepository priorityRepository, SpecificationTemplateRepository specificationTemplateRepository) {
		this.specifications = specificationRepository;
		this.specificationTemplates = specificationTemplateRepository;
		this.priorities = priorityRepository;
	}
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}


	@GetMapping("/specification/new")
	public String initCreationForm(Model model) {
		Specification specification = new Specification();
		model.addAttribute("specification", specification);
		this.populateFormModel( model );
		return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
	}

	@PostMapping("/specification/new")
	public String processCreationForm(@Valid Specification specification, BindingResult result, Model model) {
		Optional<Specification> testSpecification = specifications.findByNameIgnoreCase(specification.getName());
		if( testSpecification.isPresent() ){
			result.rejectValue("name", "unique", "Specification already exists");
		}

		if (result.hasErrors()) {
			this.populateFormModel(model);
			return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
		}

		this.specifications.save(specification);
		return "redirect:/specification/" + specification.getId();
	}

	@GetMapping("/specification/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("specification", new Specification());
		return "specification/findSpecification";
	}

	@GetMapping("/specifications")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Specification specification,
			BindingResult result, Model model) {

		if (specification.getName() == null) {
			specification.setName(""); // empty string signifies broadest possible search
		}

		Page<Specification> specificationResults = findPaginatedForSpecificationName(page, specification.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return "specification/findSpecification";
		}

		if (specificationResults.getTotalElements() == 1) {
			specification = specificationResults.iterator().next();
			return "redirect:/specification/" + specification.getId();
		}

		return addPaginationModel(page, model, specificationResults);
	}

	private String addPaginationModel(int page, Model model, Page<Specification> paginated) {
		model.addAttribute("paginated", paginated);
		List<Specification> listSpecifications = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listSpecifications", listSpecifications);
		return "specification/specificationList";
	}

	private Page<Specification> findPaginatedForSpecificationName(int page, String name) {
		int pageSize = 20;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specifications.findByNameStartsWithIgnoreCase(name, pageable);
	}

	@GetMapping("/specification/{specificationId}/edit")
	public String initUpdateSpecificationForm(@PathVariable("specificationId") int specificationId, Model model) {
		Optional<Specification> maybeSpecification = this.specifications.findById(specificationId);
		if(maybeSpecification.isEmpty()){
			return "redirect:/specification/find";
		}
		model.addAttribute(maybeSpecification.get());
		populateFormModel(model);
		return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
	}

	@PostMapping("/specification/{specificationId}/edit")
	public String processUpdateSpecificationForm(@Valid Specification specification, BindingResult result,
			@PathVariable("specificationId") int specificationId, Model model) {

		Optional<Specification> testSpecification = specifications.findByNameIgnoreCase(specification.getName());
		if( testSpecification.isPresent()  && testSpecification.get().getId() != specificationId  ){
			result.rejectValue("name", "unique", "Specification already exists");
		}

		if (result.hasErrors()) {
			specification.setId( specificationId );
			model.addAttribute("specification", specification );
			this.populateFormModel( model );
			return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
		}

		specification.setId(specificationId);
		this.specifications.save(specification);
		return "redirect:/specification/{specificationId}";
	}

	@GetMapping("/specification/{specificationId}")
	public String showSpecification(@PathVariable("specificationId") int specificationId, Model model) {
		Optional<Specification> maybeSpecification = this.specifications.findById(specificationId);
		if(maybeSpecification.isEmpty()){
			return "redirect:/specification/find";
		}
		model.addAttribute(maybeSpecification.get());
		return "specification/specificationDetails";
	}

	private void populateFormModel( Model model ){
		model.addAttribute("priorities", priorities.findAll());
		model.addAttribute("specificationTemplates", specificationTemplates.findAll());
	}

}
