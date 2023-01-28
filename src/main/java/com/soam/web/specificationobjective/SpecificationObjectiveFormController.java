package com.soam.web.specificationobjective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.web.ModelConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}")
public class SpecificationObjectiveFormController extends SoamFormController {
    private static final String REDIRECT_SPECIFICATION_LIST = "redirect:/specification/list";
    private static final String REDIRECT_SPECIFICATION_OBJECTIVE_LIST = "redirect:/specification/%s/specificationObjective/list";
    private static final String REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS = "redirect:/specification/%s/specificationObjective/%s";

    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final ObjectiveTemplateRepository objectiveTemplateRepository;
    private final SpecificationRepository specificationRepository;
    private final PriorityRepository priorityRepository;

    public SpecificationObjectiveFormController(
            SpecificationObjectiveRepository specificationObjectiveRepository, ObjectiveTemplateRepository objectiveTemplateRepository,
            SpecificationRepository specificationRepository, PriorityRepository priorityRepository) {
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.objectiveTemplateRepository = objectiveTemplateRepository;
        this.specificationRepository = specificationRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId){
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElse(null);
    }

    @GetMapping("/specificationObjective/new")
    public String initCreationForm(Specification specification, Model model) {
        if (specification == null) {
            //todo: throw error!
            return REDIRECT_SPECIFICATION_LIST;
        }
        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setSpecification(specification);

        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, specificationObjective);
        this.populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @Valid SpecificationObjective specificationObjective, BindingResult result, Model model) {
        Optional<SpecificationObjective> testSpecificationObjective = specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(specification, specificationObjective.getName());
        if( testSpecificationObjective.isPresent()) {
            result.rejectValue("name", "unique", "Specification Objective already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        this.specificationObjectiveRepository.save(specificationObjective);
        return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, specification.getId(), specificationObjective.getId());
    }

    @GetMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String initUpdateSpecificationObjectiveForm(
            Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId, Model model) {
        Optional<SpecificationObjective> maybeSpecificationObjective = this.specificationObjectiveRepository.findById(specificationObjectiveId);
        if (maybeSpecificationObjective.isEmpty()) {
            return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
        }
        model.addAttribute(maybeSpecificationObjective.get());
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String processUpdateSpecificationObjectiveForm(
            @Valid SpecificationObjective specificationObjective, BindingResult result,
            @ModelAttribute(binding = false) Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId,
            Model model) {
        Optional<SpecificationObjective> testSpecificationObjective = specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(specification, specificationObjective.getName());
        testSpecificationObjective.ifPresent(s-> {
            if (testSpecificationObjective.get().getId() != specificationObjectiveId) {
                result.rejectValue("name", "unique", "Specification Objective already exists");
            }
        });
        if (result.hasErrors()) {
            specificationObjective.setId(specificationObjectiveId);
            model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, specificationObjective);
            this.populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        specificationObjective.setId(specificationObjectiveId);
        this.specificationObjectiveRepository.save(specificationObjective);
        return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, specification.getId(), specificationObjectiveId);
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/delete")
    @Transactional
    public String processDeleteSpecificationObjective(
            @PathVariable("specificationObjectiveId") int specificationObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            Model model, BindingResult result, RedirectAttributes redirectAttributes) {
        Optional<SpecificationObjective> maybeSpecificationObjective = specificationObjectiveRepository.findById(specificationObjectiveId);
        //todo: validate objectiveById's name matches the passed in Stakeholder's name.

        if (maybeSpecificationObjective.isPresent()) {
            if (maybeSpecificationObjective.get().getStakeholderObjectives() != null && !maybeSpecificationObjective.get().getStakeholderObjectives().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any stakeholder objectives first.");
            } else {
                specificationObjectiveRepository.delete(maybeSpecificationObjective.get());
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", maybeSpecificationObjective.get().getName()));
            }
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting specification objective");
        }
        return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
