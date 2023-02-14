package com.soam.web.specification;

import com.soam.model.specification.SpecificationTemplate;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SpecificationTemplateFormController implements SoamFormController {
    public static String CREATE_MODE_FROM_TEMPLATE = "templateDeepCopy";

    private final SpecificationTemplateService specificationTemplateService;
    private final PriorityService priorityService;

    public SpecificationTemplateFormController(
            SpecificationTemplateService specificationTemplateService, PriorityService priorityService) {
        this.specificationTemplateService = specificationTemplateService;
        this.priorityService = priorityService;
    }

    @GetMapping("/specification/template/new")
    public String initCreationForm(Model model) {
        SpecificationTemplate specificationTemplate = new SpecificationTemplate();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
        populateFormModel(model);

        return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/new")
    @Transactional
    public String processCreationForm(
            @Valid SpecificationTemplate specificationTemplate, BindingResult result,
            @ModelAttribute(ModelConstants.ATTR_COLLECTION_TYPE) String collectionType,
            @ModelAttribute(ModelConstants.ATTR_COLLECTION_ITEM_ID) int collectionItemId,
            Model model, RedirectAttributes redirectAttributes) {
        specificationTemplateService.findByName(specificationTemplate.getName()).ifPresent(st ->
                result.rejectValue("name", "unique", "Specification Template already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        if (CREATE_MODE_FROM_TEMPLATE.equals(collectionType)) {
            //creating new Specification Template as a deep copy of source Specification Template
            SpecificationTemplate srcSpecificationTemplate = specificationTemplateService.getById(collectionItemId);
            specificationTemplate = specificationTemplateService.saveDeepCopy(srcSpecificationTemplate, specificationTemplate);
            redirectAttributes.addFlashAttribute(
                    SoamFormController.FLASH_SUCCESS,
                    String.format("Created %s", getSpecificationTemplateOverviewMessage(specificationTemplate))
            );
        } else {
            specificationTemplateService.save(specificationTemplate);
        }
        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    @GetMapping("/specification/template/{specificationTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("specificationTemplateId") int specificationId, Model model,
            RedirectAttributes redirectAttributes) {
        SpecificationTemplate specificationTemplate = specificationTemplateService.getById(specificationId);
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/{specificationTemplateId}/edit")
    public String processUpdateForm(
            @Valid SpecificationTemplate specificationTemplate, BindingResult result,
            @PathVariable("specificationTemplateId") int specificationTemplateId, Model model) {
        specificationTemplateService.findByName(specificationTemplate.getName())
                .filter(st -> st.getId() != specificationTemplateId)
                .ifPresent(st -> result.rejectValue("name", "unique", "Specification Template already exists."));

        specificationTemplate.setId(specificationTemplateId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        specificationTemplateService.save(specificationTemplate);
        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    @PostMapping("/specification/template/{specificationTemplateId}/delete")
    public String processDelete(
            @PathVariable("specificationTemplateId") int specificationTemplateId, @RequestParam("id") int formId,
            RedirectAttributes redirectAttributes) {
        if (specificationTemplateId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
        } else {
            SpecificationTemplate specificationTemplate = specificationTemplateService.getById(specificationTemplateId);
            if (specificationTemplate.getTemplateLinks() != null && !specificationTemplate.getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Template Links first.");
            } else {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", specificationTemplate.getName()));
                specificationTemplateService.delete(specificationTemplate);
            }
        }

        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplateService.findAll());
    }

    private String getSpecificationTemplateOverviewMessage(SpecificationTemplate specificationTemplate) {
        return String.format(
                "Specification Template %s with %d Template Link(s)",
                specificationTemplate.getName(),
                specificationTemplate.getTemplateLinks().size()
        );
    }
}
