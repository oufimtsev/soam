package com.soam.web.stakeholder;

import com.soam.model.specification.Specification;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.stakeholder.StakeholderService;
import com.soam.service.stakeholder.StakeholderTemplateService;
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
@RequestMapping("/specification/{specificationId}")
public class StakeholderFormController implements SoamFormController {
    private static final String MSG_MALFORMED_REQUEST = "Malformed request.";

    private final SpecificationService specificationService;
    private final StakeholderService stakeholderService;
    private final StakeholderTemplateService stakeholderTemplateService;
    private final PriorityService priorityService;

    public StakeholderFormController(
            SpecificationService specificationService, StakeholderService stakeholderService,
            StakeholderTemplateService stakeholderTemplateService, PriorityService priorityService) {
        this.specificationService = specificationService;
        this.stakeholderService = stakeholderService;
        this.stakeholderTemplateService = stakeholderTemplateService;
        this.priorityService = priorityService;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationService.getById(specificationId);
    }

    @GetMapping("/stakeholder/{stakeholderId}")
    public String showDetails(
            @PathVariable("specificationId") int specificationId, @PathVariable("stakeholderId") int stakeholderId,
            Model model, RedirectAttributes redirectAttributes) {
        Stakeholder stakeholder = stakeholderService.getById(stakeholderId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        return ViewConstants.VIEW_STAKEHOLDER_DETAILS;
    }

    @GetMapping("/stakeholder/new")
    public String initCreationForm(Specification specification, Model model) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setSpecification(specification);

        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @GetMapping("/stakeholder2/new")
    public String initCreationForm2(Specification specification, Model model) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setSpecification(specification);

        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM2;
    }

    @PostMapping("/stakeholder/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @Valid Stakeholder stakeholder, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (stakeholder.getSpecification() == null || !Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }

        stakeholderService.findBySpecificationAndName(specification, stakeholder.getName()).ifPresent(s ->
                result.rejectValue("name", "unique", "Stakeholder already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        stakeholder = stakeholderService.save(stakeholder);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
    }

    @PostMapping("/stakeholder2/new")
    public String processCreationForm2(
            @ModelAttribute(binding = false) Specification specification,
            @Valid Stakeholder stakeholder, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (stakeholder.getSpecification() == null || !Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return RedirectConstants.REDIRECT_TREE_DEFAULT;
        }

        stakeholderService.findBySpecificationAndName(specification, stakeholder.getName()).ifPresent(s ->
                result.rejectValue("name", "unique", "Stakeholder already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM2;
        }

        stakeholder = stakeholderService.save(stakeholder);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Stakeholder created.");
        return String.format(RedirectConstants.REDIRECT_TREE_STAKEHOLDER_EDIT, specification.getId(), stakeholder.getId());
    }

    @GetMapping("/stakeholder/{stakeholderId}/edit")
    public String initUpdateForm(@PathVariable("stakeholderId") int stakeholderId, Model model) {
        Stakeholder stakeholder = stakeholderService.getById(stakeholderId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @GetMapping("/stakeholder2/{stakeholderId}/edit")
    public String initUpdateForm2(@PathVariable("stakeholderId") int stakeholderId, Model model) {
        Stakeholder stakeholder = stakeholderService.getById(stakeholderId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM2;
    }

    @PostMapping("/stakeholder/{stakeholderId}/edit")
    public String processUpdateForm(
            @Valid Stakeholder stakeholder, BindingResult result,
            @ModelAttribute(binding = false) Specification specification,
            @PathVariable("stakeholderId") int stakeholderId, Model model, RedirectAttributes redirectAttributes) {
        if (stakeholder.getSpecification() == null || !Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }

        stakeholderService.findBySpecificationAndName(specification, stakeholder.getName())
                .filter(s -> s.getId() != stakeholderId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Stakeholder already exists."));

        stakeholder.setId(stakeholderId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        stakeholder = stakeholderService.save(stakeholder);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
    }

    @PostMapping("/stakeholder2/{stakeholderId}/edit")
    public String processUpdateForm2(
            @Valid Stakeholder stakeholder, BindingResult result,
            @ModelAttribute(binding = false) Specification specification,
            @PathVariable("stakeholderId") int stakeholderId, Model model, RedirectAttributes redirectAttributes) {
        if (stakeholder.getSpecification() == null || !Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }

        stakeholderService.findBySpecificationAndName(specification, stakeholder.getName())
                .filter(s -> s.getId() != stakeholderId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Stakeholder already exists."));

        stakeholder.setId(stakeholderId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM2;
        }

        stakeholder = stakeholderService.save(stakeholder);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Stakeholder updated.");
        return String.format(RedirectConstants.REDIRECT_TREE_STAKEHOLDER_EDIT, specification.getId(), stakeholder.getId());
    }

    @PostMapping("/stakeholder/{stakeholderId}/delete")
    public String processDelete(
            @ModelAttribute(binding = false) Specification specification, @PathVariable("stakeholderId") int stakeholderId,
            @RequestParam("id") int formId, RedirectAttributes redirectAttributes) {
        if (stakeholderId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }

        Stakeholder stakeholder = stakeholderService.getById(stakeholderId);
        if (!Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
        } else if (stakeholder.getStakeholderObjectives() != null && !stakeholder.getStakeholderObjectives().isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Stakeholder Objectives first.");
            return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholderId);
        }
        stakeholderService.delete(stakeholder);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", stakeholder.getName()));
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateService.findAll());
    }
}
