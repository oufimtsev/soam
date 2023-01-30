package com.soam.web.stakeholderobjective;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
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
import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}/stakeholder/{stakeholderId}")
public class StakeholderObjectiveFormController implements SoamFormController {
    private final StakeholderObjectiveRepository stakeholderObjectiveRepository;
    private final SpecificationRepository specificationRepository;
    private final StakeholderRepository stakeholderRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final PriorityRepository priorityRepository;

    public StakeholderObjectiveFormController(
            StakeholderObjectiveRepository stakeholderObjectiveRepository, SpecificationRepository specificationRepository,
            StakeholderRepository stakeholderRepository,
            SpecificationObjectiveRepository specificationObjectiveRepository, PriorityRepository priorityRepository) {
        this.stakeholderObjectiveRepository = stakeholderObjectiveRepository;
        this.specificationRepository = specificationRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationRepository.findById(specificationId)
                .orElseThrow(IllegalSpecificationIdException::new);
    }

    @ModelAttribute(ModelConstants.ATTR_STAKEHOLDER)
    public Stakeholder populateStakeholder(
            @PathVariable("stakeholderId") int stakeholderId, @ModelAttribute Specification specification) {
        return stakeholderRepository.findById(stakeholderId)
                .filter(stakeholder1 -> stakeholder1.getSpecification().getId().equals(specification.getId()))
                .orElseThrow(() -> new IllegalStakeholderIdException(specification));
    }

    @GetMapping("/stakeholderObjective/{stakeholderObjectiveId}")
    public String showDetails(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId, Model model) {
        return stakeholderObjectiveRepository.findById(stakeholderObjectiveId)
                .map(stakeholderObjective -> {
                    model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, stakeholderObjective);
                    return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_DETAILS;
                })
                .orElse(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId()));
    }

    @GetMapping("/stakeholderObjective/new")
    public String initCreationForm(
            Specification specification, Stakeholder stakeholder, Model model, RedirectAttributes redirectAttributes) {
        if (specification.getSpecificationObjectives().isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Specification does not have any Specification Objectives.");
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

    @PostMapping("/stakeholderObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            @RequestParam("collectionItemId") int specificationObjectiveId,
            @Valid StakeholderObjective stakeholderObjective, BindingResult result, Model model) {
        Optional<SpecificationObjective> maybeSpecificationObjective = specificationObjectiveRepository.findById(specificationObjectiveId);
        if (maybeSpecificationObjective.isEmpty()) {
            SpecificationObjective emptySeSpecificationObjective = new SpecificationObjective();
            emptySeSpecificationObjective.setId(-1);
            stakeholderObjective.setSpecificationObjective(emptySeSpecificationObjective);
            result.rejectValue("specificationObjective", "required", "Specification Objective should not be empty.");
        } else {
            stakeholderObjective.setSpecificationObjective(maybeSpecificationObjective.get());

            stakeholderObjectiveRepository.findByStakeholderAndSpecificationObjectiveId(stakeholder, specificationObjectiveId).ifPresent(so ->
                    result.rejectValue("specificationObjective", "unique", "Stakeholder Objective already exists."));
        }

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        stakeholderObjectiveRepository.save(stakeholderObjective);

        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS, specification.getId(), stakeholder.getId(), stakeholderObjective.getId());
    }

    @GetMapping("/stakeholderObjective/{stakeholderObjectiveId}/edit")
    public String initUpdateForm(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<StakeholderObjective> maybeStakeholderObjective = stakeholderObjectiveRepository.findById(stakeholderObjectiveId);
        if (maybeStakeholderObjective.isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Stakeholder Objective does not exist.");
            return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        }
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, maybeStakeholderObjective.get());
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
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

        stakeholderObjectiveRepository.save(stakeholderObjective);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS, specification.getId(), stakeholder.getId(), stakeholderObjectiveId);
    }

    @PostMapping("/stakeholderObjective/{stakeholderObjectiveId}/delete")
    public String processDelete(
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            RedirectAttributes redirectAttributes) {
        Optional<StakeholderObjective> maybeStakeholderObjective = stakeholderObjectiveRepository.findById(stakeholderObjectiveId);

        if (maybeStakeholderObjective.isPresent()) {
            StakeholderObjective stakeholderObjective = maybeStakeholderObjective.get();
            if (!Objects.equals(stakeholder.getId(), stakeholderObjective.getStakeholder().getId())) {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Malformed request.");
            } else {
                stakeholderObjectiveRepository.delete(stakeholderObjective);
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s.", stakeholderObjective.getSpecificationObjective().getName()));
            }
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting Stakeholder Objective.");
        }
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
    }

    @ExceptionHandler(IllegalSpecificationIdException.class)
    public String errorHandlerSpecification(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(Util.DANGER, "Incorrect request parameters.");
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    @ExceptionHandler(IllegalStakeholderIdException.class)
    public String errorHandlerStakeholder(RedirectAttributes redirectAttributes, IllegalStakeholderIdException e) {
        redirectAttributes.addFlashAttribute(Util.DANGER, "Incorrect request parameters.");
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, e.specification.getId());
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
    }

    static class IllegalSpecificationIdException extends IllegalArgumentException {
    }

    static class IllegalStakeholderIdException extends IllegalArgumentException {
        private final Specification specification;

        IllegalStakeholderIdException(Specification specification) {
            this.specification = specification;
        }
    }
}
