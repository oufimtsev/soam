package com.soam.web.templatelink;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.service.stakeholder.StakeholderTemplateService;
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

import java.util.List;
import java.util.Optional;

@Controller
public class TemplateLinkController implements SoamFormController {
    //human-friendly template link title in form of 'specification template name / stakeholder template name / objective template name'
    private static final String TEMPLATE_LINK_TITLE = "%s / %s / %s";

    private static final Sort TEMPLATE_LINK_SORT = Sort.by(List.of(
            Sort.Order.by("specificationTemplate.name").ignoreCase(),
            Sort.Order.by("stakeholderTemplate.name").ignoreCase(),
            Sort.Order.by("objectiveTemplate.name").ignoreCase()
    ));

    private final TemplateLinkRepository templateLinkRepository;
    private final SpecificationTemplateService specificationTemplateService;
    private final StakeholderTemplateService stakeholderTemplateService;
    private final ObjectiveTemplateService objectiveTemplateService;

    public TemplateLinkController(
            TemplateLinkRepository templateLinkRepository,
            SpecificationTemplateService specificationTemplateService,
            StakeholderTemplateService stakeholderTemplateService,
            ObjectiveTemplateService objectiveTemplateService) {
        this.templateLinkRepository = templateLinkRepository;
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
            return templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplate(
                    templateLinkForm.getFilterSpecificationTemplate(), templateLinkForm.getFilterStakeholderTemplate(), TEMPLATE_LINK_SORT);
        } else if (templateLinkForm.getFilterSpecificationTemplate() != null && templateLinkForm.getFilterStakeholderTemplate() == null) {
            return templateLinkRepository.findBySpecificationTemplate(templateLinkForm.getFilterSpecificationTemplate(), TEMPLATE_LINK_SORT);
        } else if (templateLinkForm.getFilterSpecificationTemplate() == null && templateLinkForm.getFilterStakeholderTemplate() != null) {
            return templateLinkRepository.findByStakeholderTemplate(templateLinkForm.getFilterStakeholderTemplate(), TEMPLATE_LINK_SORT);
        } else {
            return templateLinkRepository.findAll(TEMPLATE_LINK_SORT);
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

    @PostMapping("/templateLink/new")
    public String processCreateForm(
            @Valid TemplateLinkFormDto templateLinkForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        TemplateLink templateLink = templateLinkForm.getNewTemplateLink();
        Optional<TemplateLink> maybeExistingTemplateLink =
                templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                        templateLink.getSpecificationTemplate(), templateLink.getStakeholderTemplate(),
                        templateLink.getObjectiveTemplate());
        redirectAttributes.addFlashAttribute(ModelConstants.ATTR_TEMPLATE_LINK_FORM, templateLinkForm);
        if (maybeExistingTemplateLink.isEmpty()) {
            if (bindingResult.hasErrors()) {
                //the UI should never cause this error. This is protection mostly from malformed programmatic POST
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "New Template Link data is not complete.");
            } else {
                templateLinkRepository.save(templateLink);
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
        Optional<TemplateLink> maybeTemplateLink = templateLinkRepository.findById(templateLinkForm.getDeleteTemplateLinkId());

        redirectAttributes.addFlashAttribute(ModelConstants.ATTR_TEMPLATE_LINK_FORM, templateLinkForm);
        if (maybeTemplateLink.isPresent()) {
            if (bindingResult.hasErrors()) {
                //the UI should never cause this error. This is protection mostly from malformed programmatic POST
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "New form data is malformed.");
            } else {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted Template Link %s.", getTemplateLinkTitle(maybeTemplateLink.get())));
                templateLinkRepository.delete(maybeTemplateLink.get());
            }
        } else {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Error deleting Template Link.");
        }
        return RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST;
    }

    private static String getTemplateLinkTitle(TemplateLink templateLink) {
        return String.format(TEMPLATE_LINK_TITLE,
                templateLink.getSpecificationTemplate().getName(),
                templateLink.getStakeholderTemplate().getName(),
                templateLink.getObjectiveTemplate().getName());
    }
}
