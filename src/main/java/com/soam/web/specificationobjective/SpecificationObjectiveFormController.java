package com.soam.web.specificationobjective;

import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.service.EntityNotFoundException;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specificationobjective.SpecificationObjectiveService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/specification/{specificationId}")
public class SpecificationObjectiveFormController implements SoamFormController {
    private static final String MSG_MALFORMED_REQUEST = "Malformed request.";

    private final SpecificationService specificationService;
    private final SpecificationObjectiveService specificationObjectiveService;
    private final ObjectiveTemplateService objectiveTemplateService;
    private final PriorityService priorityService;

    public SpecificationObjectiveFormController(
            SpecificationService specificationService,
            SpecificationObjectiveService specificationObjectiveService,
            ObjectiveTemplateService objectiveTemplateService, PriorityService priorityService) {
        this.specificationService = specificationService;
        this.specificationObjectiveService = specificationObjectiveService;
        this.objectiveTemplateService = objectiveTemplateService;
        this.priorityService = priorityService;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationService.getById(specificationId);
    }

    @GetMapping("/specificationObjective/new")
    public String initCreationForm(
            Specification specification, Model model,
            @RequestParam(name = "collectionType", required = false) String collectionType) {
        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setSpecification(specification);

        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, specificationObjective);
        model.addAttribute(ModelConstants.ATTR_COLLECTION_TYPE, collectionType == null ? "" : collectionType);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(ModelConstants.ATTR_COLLECTION_TYPE) String collectionType,
            @Valid SpecificationObjective specificationObjective, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (specificationObjective.getSpecification() == null || !Objects.equals(specification.getId(), specificationObjective.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT;
        }

        specificationObjectiveService.findBySpecificationAndName(specification, specificationObjective.getName()).ifPresent(so ->
                result.rejectValue("name", "unique", "Specification Objective already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        specificationObjective = specificationObjectiveService.save(specificationObjective);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Specification Objective created.");
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_EDIT, specification.getId(), specificationObjective.getId());
    }

    @GetMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String initUpdateForm(
            Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId, Model model,
            RedirectAttributes redirectAttributes) {
        SpecificationObjective specificationObjective = specificationObjectiveService.getById(specificationObjectiveId);
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, specificationObjective);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String processUpdateForm(
            @Valid SpecificationObjective specificationObjective, BindingResult result,
            @ModelAttribute(binding = false) Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId,
            Model model, RedirectAttributes redirectAttributes) {
        if (specificationObjective.getSpecification() == null || !Objects.equals(specification.getId(), specificationObjective.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT;
        }

        specificationObjectiveService.findBySpecificationAndName(specification, specificationObjective.getName())
                .filter(so -> so.getId() != specificationObjectiveId)
                .ifPresent(so -> result.rejectValue("name", "unique", "Specification Objective already exists."));

        specificationObjective.setId(specificationObjectiveId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        specificationObjective = specificationObjectiveService.save(specificationObjective);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Specification Objective updated.");
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_EDIT, specification.getId(), specificationObjective.getId());
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/delete")
    public String processDelete(
            @PathVariable("specificationObjectiveId") int specificationObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            RedirectAttributes redirectAttributes) {
        SpecificationObjective specificationObjective = specificationObjectiveService.getById(specificationObjectiveId);
        if (!Objects.equals(specification.getId(), specificationObjective.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
        } else if (specificationObjective.getStakeholderObjectives() != null && !specificationObjective.getStakeholderObjectives().isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Please delete any Stakeholder Objectives first.");
        } else {
            specificationObjectiveService.delete(specificationObjective);
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully deleted %s.", specificationObjective.getName()));
        }
        return RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateService.findAll());
    }
}
