package com.soam.web.specificationobjective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
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

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}")
public class SpecificationObjectiveFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());
    private static final String MSG_MALFORMED_REQUEST = "Malformed request.";

    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final ObjectiveTemplateRepository objectiveTemplateRepository;
    private final SpecificationRepository specificationRepository;
    private final PriorityRepository priorityRepository;

    public SpecificationObjectiveFormController(
            SpecificationObjectiveRepository specificationObjectiveRepository,
            ObjectiveTemplateRepository objectiveTemplateRepository,
            SpecificationRepository specificationRepository, PriorityRepository priorityRepository) {
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.objectiveTemplateRepository = objectiveTemplateRepository;
        this.specificationRepository = specificationRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationRepository.findById(specificationId).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("/specificationObjective/new")
    public String initCreationForm(Specification specification, Model model) {
        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setSpecification(specification);

        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, specificationObjective);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @Valid SpecificationObjective specificationObjective, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (specificationObjective.getSpecification() == null || !Objects.equals(specification.getId(), specificationObjective.getSpecification().getId())) {
            redirectAttributes.addFlashAttribute(Util.DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
        }

        specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(specification, specificationObjective.getName()).ifPresent(so ->
                result.rejectValue("name", "unique", "Specification Objective already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        specificationObjectiveRepository.save(specificationObjective);
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, specification.getId(), specificationObjective.getId());
    }

    @GetMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String initUpdateForm(
            Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<SpecificationObjective> maybeSpecificationObjective = specificationObjectiveRepository.findById(specificationObjectiveId);
        if (maybeSpecificationObjective.isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Specification Objective does not exist.");
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
        }
        model.addAttribute(maybeSpecificationObjective.get());
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
            redirectAttributes.addFlashAttribute(Util.DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
        }

        specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(specification, specificationObjective.getName())
                .filter(so -> so.getId() != specificationObjectiveId)
                .ifPresent(so -> result.rejectValue("name", "unique", "Specification Objective already exists."));

        if (result.hasErrors()) {
            specificationObjective.setId(specificationObjectiveId);
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        specificationObjective.setId(specificationObjectiveId);
        specificationObjectiveRepository.save(specificationObjective);
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, specification.getId(), specificationObjectiveId);
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/delete")
    @Transactional
    public String processDelete(
            @PathVariable("specificationObjectiveId") int specificationObjectiveId, @RequestParam("id") int formId,
            @ModelAttribute(binding = false) Specification specification,
            RedirectAttributes redirectAttributes) {
        if (specificationObjectiveId != formId) {
            redirectAttributes.addFlashAttribute(Util.DANGER, MSG_MALFORMED_REQUEST);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
        }

        Optional<SpecificationObjective> maybeSpecificationObjective = specificationObjectiveRepository.findById(specificationObjectiveId);

        if (maybeSpecificationObjective.isPresent()) {
            SpecificationObjective specificationObjective = maybeSpecificationObjective.get();
            if (!Objects.equals(specification.getId(), specificationObjective.getSpecification().getId())) {
                redirectAttributes.addFlashAttribute(Util.DANGER, MSG_MALFORMED_REQUEST);
            } else if (specificationObjective.getStakeholderObjectives() != null && !specificationObjective.getStakeholderObjectives().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any Stakeholder Objectives first.");
            } else {
                specificationObjectiveRepository.delete(specificationObjective);
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s.", specificationObjective.getName()));
            }
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting Specification Objective.");
        }
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String errorHandler(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(Util.DANGER, "Incorrect request parameters.");
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
