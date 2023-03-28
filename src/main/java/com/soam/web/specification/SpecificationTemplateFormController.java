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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
    public String initCreationForm(Model model, @RequestParam(name = "collectionType", required = false) String collectionType) {
        SpecificationTemplate specificationTemplate = new SpecificationTemplate();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
        model.addAttribute(ModelConstants.ATTR_COLLECTION_TYPE, collectionType == null ? "" : collectionType);
        populateFormModel(model);

        return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/new")
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
        } else {
            specificationTemplate.setTemplateLinks(List.of());
            specificationTemplate = specificationTemplateService.save(specificationTemplate);
        }
        redirectAttributes.addFlashAttribute(
                SoamFormController.FLASH_SUCCESS,
                String.format("Created %s", getSpecificationTemplateOverviewMessage(specificationTemplate))
        );
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_EDIT, specificationTemplate.getId());
    }

    @GetMapping("/specification/template/{specificationTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("specificationTemplateId") int specificationId, Model model) {
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

        specificationTemplate = specificationTemplateService.save(specificationTemplate);
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_EDIT, specificationTemplate.getId());
    }

    @PostMapping("/specification/template/{specificationTemplateId}/delete")
    public String processDelete(
            @PathVariable("specificationTemplateId") int specificationTemplateId,
            RedirectAttributes redirectAttributes) {
        SpecificationTemplate specificationTemplate = specificationTemplateService.getById(specificationTemplateId);
        if (specificationTemplate.getTemplateLinks() != null && !specificationTemplate.getTemplateLinks().isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Please delete any Template Links first.");
        } else {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully deleted %s.", specificationTemplate.getName()));
            specificationTemplateService.delete(specificationTemplate);
        }

        return RedirectConstants.REDIRECT_TEMPLATE_DEFAULT;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_TEMPLATE_DEFAULT;
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
