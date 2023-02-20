package com.soam.web.stakeholder;

import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StakeholderTemplateFormController implements SoamFormController {
    private final StakeholderTemplateService stakeholderTemplateService;
    private final PriorityService priorityService;

    public StakeholderTemplateFormController(
            StakeholderTemplateService stakeholderTemplateService, PriorityService priorityService) {
        this.stakeholderTemplateService = stakeholderTemplateService;
        this.priorityService = priorityService;
    }

    @GetMapping("/stakeholder/template/new")
    public String initCreationForm(Model model) {
        StakeholderTemplate stakeholderTemplate = new StakeholderTemplate();
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, stakeholderTemplate);
        populateFormModel(model);

        return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/new")
    public String processCreationForm(@Valid StakeholderTemplate stakeholderTemplate, BindingResult result, Model model) {
        stakeholderTemplateService.findByName(stakeholderTemplate.getName()).ifPresent(st ->
                result.rejectValue("name", "unique", "Stakeholder Template already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        stakeholderTemplateService.save(stakeholderTemplate);
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @GetMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("stakeholderTemplateId") int stakeholderId, Model model, RedirectAttributes redirectAttributes) {
        StakeholderTemplate stakeholderTemplate = stakeholderTemplateService.getById(stakeholderId);
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, stakeholderTemplate);
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String processUpdateForm(
            @Valid StakeholderTemplate stakeholderTemplate, BindingResult result,
            @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, Model model) {
        stakeholderTemplateService.findByName(stakeholderTemplate.getName())
                .filter(st -> st.getId() != stakeholderTemplateId)
                .ifPresent(st -> result.rejectValue("name", "unique", "Stakeholder Template already exists."));

        stakeholderTemplate.setId(stakeholderTemplateId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        stakeholderTemplateService.save(stakeholderTemplate);
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/delete")
    public String processDelete(
            @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, @RequestParam("id") int formId,
            RedirectAttributes redirectAttributes) {
        if (stakeholderTemplateId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
        } else {
            StakeholderTemplate stakeholderTemplate = stakeholderTemplateService.getById(stakeholderTemplateId);
            if (stakeholderTemplate.getTemplateLinks() != null && !stakeholderTemplate.getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Template Links first.");
            } else {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", stakeholderTemplate.getName()));
                stakeholderTemplateService.delete(stakeholderTemplate);
            }
        }

        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateService.findAll());
    }
}
