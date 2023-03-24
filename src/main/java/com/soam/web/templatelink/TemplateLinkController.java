package com.soam.web.templatelink;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.service.EntityNotFoundException;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.service.templatelink.TemplateLinkService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class TemplateLinkController implements SoamFormController {
    //human-friendly template link title in form of 'specification template name / stakeholder template name / objective template name'
    private static final String TEMPLATE_LINK_TITLE = "%s / %s / %s";

    private final TemplateLinkService templateLinkService;
    private final SpecificationTemplateService specificationTemplateService;
    private final StakeholderTemplateService stakeholderTemplateService;
    private final ObjectiveTemplateService objectiveTemplateService;

    public TemplateLinkController(
            TemplateLinkService templateLinkService,
            SpecificationTemplateService specificationTemplateService,
            StakeholderTemplateService stakeholderTemplateService,
            ObjectiveTemplateService objectiveTemplateService) {
        this.templateLinkService = templateLinkService;
        this.specificationTemplateService = specificationTemplateService;
        this.stakeholderTemplateService = stakeholderTemplateService;
        this.objectiveTemplateService = objectiveTemplateService;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES)
    public List<SpecificationTemplate> populateSpecificationTemplates() {
        return specificationTemplateService.findAll();
    }

    @ModelAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES)
    public List<StakeholderTemplate> populateStakeholderTemplates() {
        return stakeholderTemplateService.findAll();
    }

    @ModelAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES)
    public List<ObjectiveTemplate> populateObjectiveTemplates() {
        return objectiveTemplateService.findAll();
    }

    @ModelAttribute(ModelConstants.ATTR_TEMPLATE_LINKS)
    public Iterable<TemplateLink> populateTemplateLinks(
            @ModelAttribute(ModelConstants.ATTR_TEMPLATE_LINK_FORM) TemplateLinkFormDto templateLinkForm) {
        if (templateLinkForm.getFilterSpecificationTemplate() != null && templateLinkForm.getFilterStakeholderTemplate() != null) {
            return templateLinkService.findBySpecificationTemplateAndStakeholderTemplate(
                    templateLinkForm.getFilterSpecificationTemplate(), templateLinkForm.getFilterStakeholderTemplate());
        } else if (templateLinkForm.getFilterSpecificationTemplate() != null && templateLinkForm.getFilterStakeholderTemplate() == null) {
            return templateLinkService.findBySpecificationTemplate(templateLinkForm.getFilterSpecificationTemplate());
        } else if (templateLinkForm.getFilterSpecificationTemplate() == null && templateLinkForm.getFilterStakeholderTemplate() != null) {
            return templateLinkService.findByStakeholderTemplate(templateLinkForm.getFilterStakeholderTemplate());
        } else {
            return templateLinkService.findAll();
        }
    }

    @GetMapping("/templateLink/list")
    public String listAll() {
        return ViewConstants.VIEW_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/list")
    public String listFiltered(
            @ModelAttribute(ModelConstants.ATTR_TEMPLATE_LINK_FORM) TemplateLinkFormDto templateLinkForm, Model model) {
        if (templateLinkForm.getNewTemplateLink() != null) {
            if (templateLinkForm.getFilterSpecificationTemplate() != null) {
                templateLinkForm.getNewTemplateLink().setSpecificationTemplate(
                        templateLinkForm.getFilterSpecificationTemplate());
            }
            if (templateLinkForm.getFilterStakeholderTemplate() != null) {
                templateLinkForm.getNewTemplateLink().setStakeholderTemplate(
                        templateLinkForm.getFilterStakeholderTemplate());
            }
        }
        return ViewConstants.VIEW_TEMPLATE_LINK_LIST;
    }

//    @PostMapping("/templateLink/new")
    public String processCreateForm(
            @Valid TemplateLinkFormDto templateLinkForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        TemplateLink templateLink = templateLinkForm.getNewTemplateLink();
        Optional<TemplateLink> maybeExistingTemplateLink =
                templateLinkService.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                        templateLink.getSpecificationTemplate(), templateLink.getStakeholderTemplate(),
                        templateLink.getObjectiveTemplate());
        redirectAttributes.addFlashAttribute(ModelConstants.ATTR_TEMPLATE_LINK_FORM, templateLinkForm);
        if (maybeExistingTemplateLink.isEmpty()) {
            if (bindingResult.hasErrors()) {
                //the UI should never cause this error. This is protection mostly from malformed programmatic POST
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "New Template Link data is not complete.");
            } else {
                templateLink = templateLinkService.save(templateLink);
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully created Template Link %s.", getTemplateLinkTitle(templateLink)));
            }
        } else {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, String.format("Template Link %s already exists.", getTemplateLinkTitle(templateLink)));
        }
        return RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/delete")
    public String processDelete(
            @Valid TemplateLinkFormDto templateLinkForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ModelConstants.ATTR_TEMPLATE_LINK_FORM, templateLinkForm);

        TemplateLink templateLink = templateLinkService.getById(templateLinkForm.getDeleteTemplateLinkId());
        if (bindingResult.hasErrors()) {
            //the UI should never cause this error. This is protection mostly from malformed programmatic POST
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "New form data is malformed.");
        } else {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted Template Link %s.", getTemplateLinkTitle(templateLink)));
            templateLinkService.delete(templateLink);
        }
        return RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/{templateLinkId}/delete")
    public String processDelete2(
            @PathVariable("templateLinkId") int templateLinkId, RedirectAttributes redirectAttributes) {
        TemplateLink templateLink = templateLinkService.getById(templateLinkId);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully deleted Template Link %s.", getTemplateLinkTitle(templateLink)));
        templateLinkService.delete(templateLink);
        return RedirectConstants.REDIRECT_TEMPLATE_DEFAULT;
    }

    @GetMapping("/templateLink/new")
    public String initCreationForm(Model model) {
        model.addAttribute(ModelConstants.ATTR_TEMPLATE_LINK, new TemplateLink());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplateService.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateService.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateService.findAll());

        return ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/templateLink/new")
    public String processCreationForm(
            @Valid TemplateLink templateLink, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (templateLink.getSpecificationTemplate().isNew()) {
            result.rejectValue("specificationTemplate.name", "notBlank", "Should not be empty");
        }
        if (templateLink.getStakeholderTemplate().isNew()) {
            result.rejectValue("stakeholderTemplate.name", "notBlank", "Should not be empty");
        }
        if (templateLink.getObjectiveTemplate().isNew()) {
            result.rejectValue("objectiveTemplate.name", "notBlank", "Should not be empty");
        }
        if (result.hasErrors()) {
            return ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM;
        }

        Optional<TemplateLink> maybeExistingTemplateLink =
                templateLinkService.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                        templateLink.getSpecificationTemplate(), templateLink.getStakeholderTemplate(),
                        templateLink.getObjectiveTemplate());
        redirectAttributes.addFlashAttribute(ModelConstants.ATTR_TEMPLATE_LINK, templateLink);
        if (maybeExistingTemplateLink.isEmpty()) {
            templateLink = templateLinkService.save(templateLink);
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully created Template Link %s.", getTemplateLinkTitle(templateLink)));
            return String.format(RedirectConstants.REDIRECT_TEMPLATE_LINK_EDIT, templateLink.getId());
        } else {
            model.addAttribute(SoamFormController.FLASH_DANGER, String.format("Template Link %s already exists.", getTemplateLinkTitle(templateLink)));
            return ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM;
        }
    }

    @GetMapping("/templateLink/{templateLinkId}/edit")
    public String initUpdateForm(
            @PathVariable("templateLinkId") int templateLinkId, Model model, RedirectAttributes redirectAttributes) {
        TemplateLink templateLink = templateLinkService.getById(templateLinkId);
        model.addAttribute(ModelConstants.ATTR_TEMPLATE_LINK, templateLink);
        return ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/templateLink/{templateLinkId}/edit")
    public String processUpdateForm(
            @Valid TemplateLink templateLink, BindingResult result,
            @PathVariable("templateLinkId") int templateLinkId, Model model, RedirectAttributes redirectAttributes) {
        Optional<TemplateLink> maybeExistingTemplateLink =
                templateLinkService.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                        templateLink.getSpecificationTemplate(), templateLink.getStakeholderTemplate(),
                        templateLink.getObjectiveTemplate())
                        .filter(tl -> tl.getId() != templateLinkId);

        templateLink.setId(templateLinkId);
        if (maybeExistingTemplateLink.isEmpty()) {
            templateLink = templateLinkService.save(templateLink);
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully updated Template Link %s.", getTemplateLinkTitle(templateLink)));
            return String.format(RedirectConstants.REDIRECT_TEMPLATE_LINK_EDIT, templateLink.getId());
        } else {
            model.addAttribute(SoamFormController.FLASH_DANGER, String.format("Template Link %s already exists.", getTemplateLinkTitle(templateLink)));
            return ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM;
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST;
    }

    private static String getTemplateLinkTitle(TemplateLink templateLink) {
        return String.format(TEMPLATE_LINK_TITLE,
                templateLink.getSpecificationTemplate().getName(),
                templateLink.getStakeholderTemplate().getName(),
                templateLink.getObjectiveTemplate().getName());
    }
}
