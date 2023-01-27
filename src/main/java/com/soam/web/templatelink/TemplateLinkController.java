package com.soam.web.templatelink;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
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
public class TemplateLinkController {
    private static final String VIEWS_TEMPLATE_LINK_LIST = "templateLink/templateLinkList";
    private static final String REDIRECT_TEMPLATE_LINK_LIST = "redirect:/templateLink/list";
    //human-friendly template link title in form of 'specification template name / stakeholder template name / objective template name'
    private static final String TEMPLATE_LINK_TITLE = "%s / %s / %s";

    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());
    private static final Sort TEMPLATE_LINK_SORT = Sort.by(List.of(
            Sort.Order.by("specificationTemplate.name").ignoreCase(),
            Sort.Order.by("stakeholderTemplate.name").ignoreCase(),
            Sort.Order.by("objectiveTemplate.name").ignoreCase()
    ));

    private final TemplateLinkRepository templateLinkRepository;
    private final SpecificationTemplateRepository specificationTemplateRepository;
    private final StakeholderTemplateRepository stakeholderTemplateRepository;
    private final ObjectiveTemplateRepository objectiveTemplateRepository;

    public TemplateLinkController(
            TemplateLinkRepository templateLinkRepository,
            SpecificationTemplateRepository specificationTemplateRepository,
            StakeholderTemplateRepository stakeholderTemplateRepository,
            ObjectiveTemplateRepository objectiveTemplateRepository) {
        this.templateLinkRepository = templateLinkRepository;
        this.specificationTemplateRepository = specificationTemplateRepository;
        this.stakeholderTemplateRepository = stakeholderTemplateRepository;
        this.objectiveTemplateRepository = objectiveTemplateRepository;
    }

    @ModelAttribute("specificationTemplates")
    public List<SpecificationTemplate> populateSpecificationTemplates() {
        return specificationTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT);
    }

    @ModelAttribute("stakeholderTemplates")
    public List<StakeholderTemplate> populateStakeholderTemplates() {
        return stakeholderTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT);
    }

    @ModelAttribute("objectiveTemplates")
    public List<ObjectiveTemplate> populateObjectiveTemplates() {
        return objectiveTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT);
    }

    @ModelAttribute("templateLinks")
    public Iterable<TemplateLink> populateTemplateLinks(@ModelAttribute("templateLinkForm") TemplateLinkFormDto templateLinkForm) {
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

    @RequestMapping(value = "/templateLink/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String listTemplateLinks(@ModelAttribute("templateLinkForm") TemplateLinkFormDto templateLinkForm, Model model) {
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
        return VIEWS_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/new")
    public String processCreateForm(@Valid TemplateLinkFormDto templateLinkForm, BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        TemplateLink templateLink = templateLinkForm.getNewTemplateLink();
        Optional<TemplateLink> maybeExistingTemplateLink =
                templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                        templateLink.getSpecificationTemplate(), templateLink.getStakeholderTemplate(),
                        templateLink.getObjectiveTemplate());
        redirectAttributes.addFlashAttribute("templateLinkForm", templateLinkForm);
        if (maybeExistingTemplateLink.isEmpty()) {
            if (bindingResult.hasErrors()) {
                //the UI should never cause this error. This is protection mostly from malformed programmatic POST
                redirectAttributes.addFlashAttribute(Util.DANGER, "New template link data is not complete.");
            } else {
                templateLinkRepository.save(templateLink);
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully created template link %s.", getTemplateLinkTitle(templateLink)));
            }
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, String.format("Template link %s already exists.", getTemplateLinkTitle(templateLink)));
        }
        return REDIRECT_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/delete")
    public String processDeleteTemplateLink(@Valid TemplateLinkFormDto templateLinkForm, BindingResult bindingResult,
                                            RedirectAttributes redirectAttributes) {
        Optional<TemplateLink> maybeTemplateLink = templateLinkRepository.findById(templateLinkForm.getDeleteTemplateLinkId());

        redirectAttributes.addFlashAttribute("templateLinkForm", templateLinkForm);
        if (maybeTemplateLink.isPresent()) {
            if (bindingResult.hasErrors()) {
                //the UI should never cause this error. This is protection mostly from malformed programmatic POST
                redirectAttributes.addFlashAttribute(Util.DANGER, "New form data is malformed.");
            } else {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted template link %s.", getTemplateLinkTitle(maybeTemplateLink.get())));
                templateLinkRepository.delete(maybeTemplateLink.get());
            }
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template link.");
        }
        return REDIRECT_TEMPLATE_LINK_LIST;
    }

    private static String getTemplateLinkTitle(TemplateLink templateLink) {
        return String.format(TEMPLATE_LINK_TITLE,
                templateLink.getSpecificationTemplate().getName(),
                templateLink.getStakeholderTemplate().getName(),
                templateLink.getObjectiveTemplate().getName());
    }
}
