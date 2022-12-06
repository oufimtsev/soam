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
import com.soam.model.specification.SpecificationTemplate;
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
public class SpecificationTemplateController {

	private static final String VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM = "specification/template/addUpdateSpecificationTemplate";
	private static final String REDIRECT_TEMPLATE_LIST = "redirect:/specification/template/list";

	private final SpecificationTemplateRepository specificationTemplates;
	private final PriorityRepository priorities;

	public SpecificationTemplateController(SpecificationTemplateRepository specificationTemplateRepository,  PriorityRepository priorityRepository) {
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


	@GetMapping("/specification/template/new")
	public String initCreationForm(Model model ) {

		SpecificationTemplate specificationTemplate = new SpecificationTemplate();
		model.addAttribute("specificationTemplate", specificationTemplate);
		this.populateFormModel( model );

		return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
	}

	@PostMapping("/specification/template/new")
	public String processCreationForm(@Valid SpecificationTemplate specificationTemplate, BindingResult result, Model model) {

		Optional<SpecificationTemplate> testTemplate = specificationTemplates.findByName(specificationTemplate.getName());
		if( testTemplate.isPresent() ){
			result.rejectValue("name", "unique", "Template already exists");
		}

		if (result.hasErrors()) {
			this.populateFormModel( model );
			return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
		}

		this.specificationTemplates.save(specificationTemplate);
		return REDIRECT_TEMPLATE_LIST;
	}

	@GetMapping("/specification/template/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("specificationTemplate", new SpecificationTemplate());
		return "specification/template/findSpecificationTemplate";
	}

	@GetMapping("/specification/templates")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, SpecificationTemplate specificationTemplate,
								  BindingResult result, Model model) {

		if (specificationTemplate.getName() == null) {
			specificationTemplate.setName(""); // empty string signifies broadest possible search
		}

		Page<SpecificationTemplate> specificationResults = findPaginatedForSpecificationTemplateName(page, specificationTemplate.getName());
		if (specificationResults.isEmpty()) {
			result.rejectValue("name", "notFound", "not found");
			return "specification/template/findSpecificationTemplate";
		}

		if (specificationResults.getTotalElements() == 1) {
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
		return "specification/template/specificationTemplateList";
	}

	private String addPaginationModel(int page, Model model, Page<SpecificationTemplate> paginated) {
		model.addAttribute("paginated", paginated);
		List<SpecificationTemplate> listSpecificationTemplates = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listSpecificationTemplates", listSpecificationTemplates);
		return "specification/template/specificationTemplateList";
	}

	private Page<SpecificationTemplate> findPaginatedForSpecificationTemplateName(int page, String name) {
		int pageSize = 10;
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(order));
		return specificationTemplates.findByNameContainingIgnoreCase(name, pageable);
	}

	@GetMapping("/specification/template/{specificationTemplateId}/edit")
	public String initUpdateSpecificationForm(@PathVariable("specificationTemplateId") int specificationId, Model model) {
		Optional<SpecificationTemplate> maybeSpecificationTemplate = this.specificationTemplates.findById(specificationId);
		if(maybeSpecificationTemplate.isEmpty()){
			//todo: pass error message
			return REDIRECT_TEMPLATE_LIST;
		}
		model.addAttribute(maybeSpecificationTemplate.get());
		this.populateFormModel( model );
		return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
	}

	@PostMapping("/specification/template/{specificationTemplateId}/edit")
	public String processUpdateSpecificationForm(@Valid SpecificationTemplate specificationTemplate, BindingResult result,
			@PathVariable("specificationTemplateId") int specificationTemplateId, Model model) {

		Optional<SpecificationTemplate> testTemplate = specificationTemplates.findByName(specificationTemplate.getName());
		if( testTemplate.isPresent() && testTemplate.get().getId() != specificationTemplateId ){
			result.rejectValue("name", "unique", "Template already exists");
		}

		if (result.hasErrors()) {
			specificationTemplate.setId( specificationTemplateId );
			model.addAttribute("specification", specificationTemplate );
			this.populateFormModel( model );
			return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
		}


		specificationTemplate.setId(specificationTemplateId); // strange
		this.specificationTemplates.save(specificationTemplate);
		return REDIRECT_TEMPLATE_LIST;
	}

	private void populateFormModel( Model model ){
		model.addAttribute("priorities", priorities.findAll());
		model.addAttribute("specificationTemplates", specificationTemplates.findAll());
	}

}
