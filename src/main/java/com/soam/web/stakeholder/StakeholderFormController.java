package com.soam.web.stakeholder;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}")
public class StakeholderFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());
    private static final String MSG_MALFORMED_REQUEST = "Malformed request.";

    private final StakeholderRepository stakeholderRepository;
    private final StakeholderTemplateRepository stakeholderTemplateRepository;

    private final SpecificationRepository specificationRepository;

    private final PriorityRepository priorityRepository;

    public StakeholderFormController(
            StakeholderRepository stakeholderRepository, StakeholderTemplateRepository stakeholderTemplateRepository,
            SpecificationRepository specificationRepository, PriorityRepository priorityRepository) {
        this.stakeholderRepository = stakeholderRepository;
        this.stakeholderTemplateRepository = stakeholderTemplateRepository;
        this.specificationRepository = specificationRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationRepository.findById(specificationId).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("/stakeholder/{stakeholderId}")
    public String showDetails(
            @PathVariable("specificationId") int specificationId, @PathVariable("stakeholderId") int stakeholderId,
            Model model) {
        return stakeholderRepository.findById(stakeholderId)
                .map(stakeholder -> {
                    model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
                    return ViewConstants.VIEW_STAKEHOLDER_DETAILS;
                })
                .orElse(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId));
    }

    @GetMapping("/stakeholder/new")
    public String initCreationForm(Specification specification, Model model) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setSpecification(specification);

        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @Valid Stakeholder stakeholder, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (stakeholder.getSpecification() == null || !Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }

        stakeholderRepository.findByNameIgnoreCase(stakeholder.getName()).ifPresent(s ->
                result.rejectValue("name", "unique", "Stakeholder already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        stakeholderRepository.save(stakeholder);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
    }

    @GetMapping("/stakeholder/{stakeholderId}/edit")
    public String initUpdateForm(
            @PathVariable("stakeholderId") int stakeholderId, Specification specification,
            Model model, RedirectAttributes redirectAttributes) {
        Optional<Stakeholder> maybeStakeholder = stakeholderRepository.findById(stakeholderId);
        if (maybeStakeholder.isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Stakeholder does not exist.");
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, maybeStakeholder.get());
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
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

        stakeholderRepository.findByNameIgnoreCase(stakeholder.getName())
                .filter(s -> s.getId() != stakeholderId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Stakeholder already exists."));

        stakeholder.setId(stakeholderId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        stakeholderRepository.save(stakeholder);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholderId);
    }

    @PostMapping("/stakeholder/{stakeholderId}/delete")
    public String processDelete(
            @ModelAttribute(binding = false) Specification specification, @PathVariable("stakeholderId") int stakeholderId,
            @RequestParam("id") int formId, RedirectAttributes redirectAttributes) {
        if (stakeholderId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }

        Optional<Stakeholder> maybeStakeholder = stakeholderRepository.findById(stakeholderId);

        if (maybeStakeholder.isPresent()) {
            Stakeholder stakeholder = maybeStakeholder.get();
            if (!Objects.equals(specification.getId(), stakeholder.getSpecification().getId())) {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, MSG_MALFORMED_REQUEST);
            } else if (stakeholder.getStakeholderObjectives() != null && !stakeholder.getStakeholderObjectives().isEmpty()) {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Stakeholder Objectives first.");
                return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholderId);
            }
            stakeholderRepository.delete(stakeholder);
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", stakeholder.getName()));
        } else {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Error deleting Stakeholder.");
        }
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String errorHandler(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Incorrect request parameters.");
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
