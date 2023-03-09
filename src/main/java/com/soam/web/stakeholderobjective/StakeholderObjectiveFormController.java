package com.soam.web.stakeholderobjective;

import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specificationobjective.SpecificationObjectiveService;
import com.soam.service.stakeholder.StakeholderService;
import com.soam.service.stakeholderobjective.StakeholderObjectiveService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/specification/{specificationId}/stakeholder/{stakeholderId}")
public class StakeholderObjectiveFormController implements SoamFormController {
    private final SpecificationService specificationService;
    private final StakeholderService stakeholderService;
    private final SpecificationObjectiveService specificationObjectiveService;
    private final StakeholderObjectiveService stakeholderObjectiveService;
    private final PriorityService priorityService;

    public StakeholderObjectiveFormController(
            SpecificationService specificationService, StakeholderService stakeholderService,
            SpecificationObjectiveService specificationObjectiveService,
            StakeholderObjectiveService stakeholderObjectiveService, PriorityService priorityService) {
        this.specificationService = specificationService;
        this.stakeholderService = stakeholderService;
        this.specificationObjectiveService = specificationObjectiveService;
        this.stakeholderObjectiveService = stakeholderObjectiveService;
        this.priorityService = priorityService;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationService.getById(specificationId);
    }

    @ModelAttribute(ModelConstants.ATTR_STAKEHOLDER)
    public Stakeholder populateStakeholder(
            @PathVariable("stakeholderId") int stakeholderId, @ModelAttribute(binding = false) Specification specification) {
        Stakeholder stakeholder = stakeholderService.getById(stakeholderId);
        if (stakeholder.getSpecification().getId().equals(specification.getId())) {
            return stakeholder;
        } else {
            throw new IllegalStakeholderIdException(specification);
        }
    }

    @GetMapping("/stakeholderObjective/{stakeholderObjectiveId}")
    public String showDetails(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            Model model, RedirectAttributes redirectAttributes) {
        StakeholderObjective stakeholderObjective = stakeholderObjectiveService.getById(stakeholderObjectiveId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, stakeholderObjective);
        return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_DETAILS;
    }

    @GetMapping("/stakeholderObjective/new")
    public String initCreationForm(
            Specification specification, Stakeholder stakeholder, Model model, RedirectAttributes redirectAttributes) {
        if (specification.getSpecificationObjectives().isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Specification does not have any Specification Objectives.");
            return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        }

        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setId(-1);

        StakeholderObjective stakeholderObjective = new StakeholderObjective();
        stakeholderObjective.setStakeholder(stakeholder);
        stakeholderObjective.setSpecificationObjective(specificationObjective);
        stakeholderObjective.setNotes(specificationObjective.getNotes());
        stakeholderObjective.setPriority(specificationObjective.getPriority());

        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, stakeholderObjective);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @GetMapping("/stakeholderObjective2/new")
    public String initCreationForm2(
            Specification specification, Stakeholder stakeholder, Model model, RedirectAttributes redirectAttributes) {
        if (specification.getSpecificationObjectives().isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Specification does not have any Specification Objectives.");
            return RedirectConstants.REDIRECT_TREE_DEFAULT;
        }

        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setId(-1);

        StakeholderObjective stakeholderObjective = new StakeholderObjective();
        stakeholderObjective.setStakeholder(stakeholder);
        stakeholderObjective.setSpecificationObjective(specificationObjective);
        stakeholderObjective.setNotes(specificationObjective.getNotes());
        stakeholderObjective.setPriority(specificationObjective.getPriority());

        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, stakeholderObjective);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM2;
    }

    @PostMapping("/stakeholderObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            @RequestParam("collectionItemId") int specificationObjectiveId,
            @Valid StakeholderObjective stakeholderObjective, BindingResult result, Model model) {
        if (specificationObjectiveId == -1) {
            SpecificationObjective emptySeSpecificationObjective = new SpecificationObjective();
            emptySeSpecificationObjective.setId(-1);
            stakeholderObjective.setSpecificationObjective(emptySeSpecificationObjective);
            result.rejectValue("specificationObjective", "required", "Specification Objective should not be empty.");
        } else {
            SpecificationObjective specificationObjective = specificationObjectiveService.getById(specificationObjectiveId);
            stakeholderObjective.setSpecificationObjective(specificationObjective);

            if (stakeholderObjectiveService.existsForStakeholderAndSpecificationObjective(
                    stakeholder, specificationObjective)) {
                result.rejectValue("specificationObjective", "unique",
                        "Stakeholder Objective already exists.");
            }
        }

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        stakeholderObjective = stakeholderObjectiveService.save(stakeholderObjective);

        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS, specification.getId(), stakeholder.getId(), stakeholderObjective.getId());
    }

    @PostMapping("/stakeholderObjective2/new")
    public String processCreationForm2(
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            @RequestParam("collectionItemId") int specificationObjectiveId,
            @Valid StakeholderObjective stakeholderObjective, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (specificationObjectiveId == -1) {
            SpecificationObjective emptySeSpecificationObjective = new SpecificationObjective();
            emptySeSpecificationObjective.setId(-1);
            stakeholderObjective.setSpecificationObjective(emptySeSpecificationObjective);
            result.rejectValue("specificationObjective.name", "required", "Specification Objective should not be empty.");
        } else {
            SpecificationObjective specificationObjective = specificationObjectiveService.getById(specificationObjectiveId);
            stakeholderObjective.setSpecificationObjective(specificationObjective);

            if (stakeholderObjectiveService.existsForStakeholderAndSpecificationObjective(
                    stakeholder, specificationObjective)) {
                result.rejectValue("specificationObjective.name", "unique",
                        "Stakeholder Objective already exists.");
            }
        }

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM2;
        }

        stakeholderObjective = stakeholderObjectiveService.save(stakeholderObjective);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Stakeholder Objective created.");
        return String.format(RedirectConstants.REDIRECT_TREE_STAKEHOLDER_OBJECTIVE_EDIT, specification.getId(), stakeholder.getId(), stakeholderObjective.getId());
    }

    @GetMapping("/stakeholderObjective/{stakeholderObjectiveId}/edit")
    public String initUpdateForm(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId, Model model,
            RedirectAttributes redirectAttributes) {
        StakeholderObjective stakeholderObjective = stakeholderObjectiveService.getById(stakeholderObjectiveId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, stakeholderObjective);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @GetMapping("/stakeholderObjective2/{stakeholderObjectiveId}/edit")
    public String initUpdateForm2(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId, Model model,
            RedirectAttributes redirectAttributes) {
        StakeholderObjective stakeholderObjective = stakeholderObjectiveService.getById(stakeholderObjectiveId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, stakeholderObjective);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM2;
    }

    @PostMapping("/stakeholderObjective/{stakeholderObjectiveId}/edit")
    public String processUpdateForm(
            @Valid StakeholderObjective stakeholderObjective, BindingResult result,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder, Model model) {
        stakeholderObjective.setId(stakeholderObjectiveId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        stakeholderObjective = stakeholderObjectiveService.save(stakeholderObjective);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS, specification.getId(), stakeholder.getId(), stakeholderObjective.getId());
    }

    @PostMapping("/stakeholderObjective2/{stakeholderObjectiveId}/edit")
    public String processUpdateForm2(
            @Valid StakeholderObjective stakeholderObjective, BindingResult result,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder, Model model, RedirectAttributes redirectAttributes) {
        stakeholderObjective.setId(stakeholderObjectiveId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM2;
        }

        stakeholderObjective = stakeholderObjectiveService.save(stakeholderObjective);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Stakeholder Objective updated.");
        return String.format(RedirectConstants.REDIRECT_TREE_STAKEHOLDER_OBJECTIVE_EDIT, specification.getId(), stakeholder.getId(), stakeholderObjective.getId());
    }

    @PostMapping("/stakeholderObjective/{stakeholderObjectiveId}/delete")
    public String processDelete(
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            RedirectAttributes redirectAttributes) {
        StakeholderObjective stakeholderObjective = stakeholderObjectiveService.getById(stakeholderObjectiveId);
        if (!Objects.equals(stakeholder.getId(), stakeholderObjective.getStakeholder().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
        } else {
            stakeholderObjectiveService.delete(stakeholderObjective);
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", stakeholderObjective.getSpecificationObjective().getName()));
        }
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
    }

    @PostMapping("/stakeholderObjective2/{stakeholderObjectiveId}/delete")
    public String processDelete2(
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            RedirectAttributes redirectAttributes) {
        StakeholderObjective stakeholderObjective = stakeholderObjectiveService.getById(stakeholderObjectiveId);
        if (!Objects.equals(stakeholder.getId(), stakeholderObjective.getStakeholder().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
        } else {
            stakeholderObjectiveService.delete(stakeholderObjective);
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully deleted %s.", stakeholderObjective.getSpecificationObjective().getName()));
        }
        return RedirectConstants.REDIRECT_TREE_DEFAULT;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandlerSpecification(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    @ExceptionHandler(IllegalStakeholderIdException.class)
    public String errorHandlerStakeholder(RedirectAttributes redirectAttributes, IllegalStakeholderIdException e) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Incorrect request parameters.");
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, e.specification.getId());
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
    }

    static class IllegalStakeholderIdException extends IllegalArgumentException {
        private final Specification specification;

        IllegalStakeholderIdException(Specification specification) {
            this.specification = specification;
        }
    }
}
