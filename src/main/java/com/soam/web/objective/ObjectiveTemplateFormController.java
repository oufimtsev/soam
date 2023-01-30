package com.soam.web.objective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ObjectiveTemplateFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final ObjectiveTemplateRepository objectiveTemplateRepository;
    private final PriorityRepository priorityRepository;

    public ObjectiveTemplateFormController(
            ObjectiveTemplateRepository objectiveTemplateRepository, PriorityRepository priorityRepository) {
        this.objectiveTemplateRepository = objectiveTemplateRepository;
        this.priorityRepository = priorityRepository;
    }

    @GetMapping("/objective/template/new")
    public String initCreationForm(Model model) {
        ObjectiveTemplate objectiveTemplate = new ObjectiveTemplate();
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, objectiveTemplate);
        populateFormModel(model);

        return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/new")
    public String processCreationForm(@Valid ObjectiveTemplate objectiveTemplate, BindingResult result, Model model) {
        objectiveTemplateRepository.findByNameIgnoreCase(objectiveTemplate.getName()).ifPresent(ot ->
                result.rejectValue("name", "unique", "Objective Template already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        objectiveTemplateRepository.save(objectiveTemplate);
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @GetMapping("/objective/template/{objectiveTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("objectiveTemplateId") int objectiveId, Model model, RedirectAttributes redirectAttributes) {
        Optional<ObjectiveTemplate> maybeObjectiveTemplate = objectiveTemplateRepository.findById(objectiveId);
        if (maybeObjectiveTemplate.isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Objective Template does not exist.");
            return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
        }
        model.addAttribute(maybeObjectiveTemplate.get());
        populateFormModel(model);
        return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/edit")
    public String processUpdateForm(
            @Valid ObjectiveTemplate objectiveTemplate, BindingResult result,
            @PathVariable("objectiveTemplateId") int objectiveTemplateId, Model model) {
        objectiveTemplateRepository.findByNameIgnoreCase(objectiveTemplate.getName())
                .filter(ot -> ot.getId() != objectiveTemplateId)
                .ifPresent(ot -> result.rejectValue("name", "unique", "Objective Template already exists."));

        if (result.hasErrors()) {
            objectiveTemplate.setId(objectiveTemplateId);
            populateFormModel(model);
            return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        objectiveTemplate.setId(objectiveTemplateId);
        objectiveTemplateRepository.save(objectiveTemplate);
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/delete")
    public String processDelete(
            @PathVariable("objectiveTemplateId") int objectiveTemplateId, @RequestParam("id") int formId,
            ObjectiveTemplate objectiveTemplate, RedirectAttributes redirectAttributes) {
        if (objectiveTemplateId != formId) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Malformed request.");
        } else {
            Optional<ObjectiveTemplate> objectiveTemplateById = objectiveTemplateRepository.findById(objectiveTemplateId);

            if (objectiveTemplateById.isPresent()) {
                if (objectiveTemplateById.get().getTemplateLinks() != null && !objectiveTemplateById.get().getTemplateLinks().isEmpty()) {
                    redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any Template Links first.");
                } else {
                    redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s.", objectiveTemplateById.get().getName()));
                    objectiveTemplateRepository.delete(objectiveTemplateById.get());
                }
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting Objective Template.");
            }
        }

        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
