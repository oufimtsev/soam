package com.soam.web.objective;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.service.EntityNotFoundException;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.priority.PriorityService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ObjectiveTemplateFormController implements SoamFormController {
    private final ObjectiveTemplateService objectiveTemplateService;
    private final PriorityService priorityService;

    public ObjectiveTemplateFormController(
            ObjectiveTemplateService objectiveTemplateService, PriorityService priorityService) {
        this.objectiveTemplateService = objectiveTemplateService;
        this.priorityService = priorityService;
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
        objectiveTemplateService.findByName(objectiveTemplate.getName()).ifPresent(ot ->
                result.rejectValue("name", "unique", "Objective Template already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        objectiveTemplateService.save(objectiveTemplate);
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @GetMapping("/objective/template/{objectiveTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("objectiveTemplateId") int objectiveId, Model model, RedirectAttributes redirectAttributes) {
        ObjectiveTemplate objectiveTemplate = objectiveTemplateService.getById(objectiveId);
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, objectiveTemplate);
        populateFormModel(model);
        return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/edit")
    public String processUpdateForm(
            @Valid ObjectiveTemplate objectiveTemplate, BindingResult result,
            @PathVariable("objectiveTemplateId") int objectiveTemplateId, Model model) {
        objectiveTemplateService.findByName(objectiveTemplate.getName())
                .filter(ot -> ot.getId() != objectiveTemplateId)
                .ifPresent(ot -> result.rejectValue("name", "unique", "Objective Template already exists."));

        objectiveTemplate.setId(objectiveTemplateId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        objectiveTemplateService.save(objectiveTemplate);
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/delete")
    public String processDelete(
            @PathVariable("objectiveTemplateId") int objectiveTemplateId, @RequestParam("id") int formId,
            RedirectAttributes redirectAttributes) {
        if (objectiveTemplateId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
        } else {
            ObjectiveTemplate objectiveTemplate = objectiveTemplateService.getById(objectiveTemplateId);
            if (objectiveTemplate.getTemplateLinks() != null && !objectiveTemplate.getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Template Links first.");
            } else {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", objectiveTemplate.getName()));
                objectiveTemplateService.delete(objectiveTemplate);
            }
        }

        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateService.findAll());
    }
}
