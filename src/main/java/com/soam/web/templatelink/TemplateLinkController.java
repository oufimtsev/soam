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

    private static final Sort TEMPLATE_LINK_SORT = Sort.by(List.of(
            Sort.Order.by("specificationTemplate.name"),
            Sort.Order.by("stakeholderTemplate.name"),
            Sort.Order.by("objectiveTemplate.name")
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
        return specificationTemplateRepository.findAllByOrderByName();
    }

    @ModelAttribute("stakeholderTemplates")
    public List<StakeholderTemplate> populateStakeholderTemplates() {
        return stakeholderTemplateRepository.findAllByOrderByName();
    }

    @ModelAttribute("objectiveTemplates")
    public List<ObjectiveTemplate> populateObjectiveTemplates() {
        return objectiveTemplateRepository.findAllByOrderByName();
    }

    @ModelAttribute("newTemplateLink")
    public TemplateLink populateNewTemplateLink(TemplateLink newTemplateLink) {
        return newTemplateLink;
    }

    @ModelAttribute("templateLinks")
    public Iterable<TemplateLink> populateTemplateLinks(TemplateLink newTemplateLink) {
        if (newTemplateLink.getSpecificationTemplate() != null && newTemplateLink.getStakeholderTemplate() != null) {
            return templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplate(
                    newTemplateLink.getSpecificationTemplate(), newTemplateLink.getStakeholderTemplate(), TEMPLATE_LINK_SORT);
        } else if (newTemplateLink.getSpecificationTemplate() != null && newTemplateLink.getStakeholderTemplate() == null) {
            return templateLinkRepository.findBySpecificationTemplate(newTemplateLink.getSpecificationTemplate(), TEMPLATE_LINK_SORT);
        } else if (newTemplateLink.getSpecificationTemplate() == null && newTemplateLink.getStakeholderTemplate() != null) {
            return templateLinkRepository.findByStakeholderTemplate(newTemplateLink.getStakeholderTemplate(), TEMPLATE_LINK_SORT);
        } else {
            return templateLinkRepository.findAll(TEMPLATE_LINK_SORT);
        }
    }

    @ModelAttribute("isFiltered")
    public boolean populatedIsFiltered(TemplateLink newTemplateLink) {
        return newTemplateLink.getSpecificationTemplate() != null || newTemplateLink.getStakeholderTemplate() != null;
    }

    @RequestMapping(value = "/templateLink/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String listTemplateLinks() {
        return VIEWS_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/new")
    public String processCreateForm(@Valid TemplateLink templateLink, BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        Optional<TemplateLink> maybeExistingTemplateLink = 
                templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                        templateLink.getSpecificationTemplate(), templateLink.getStakeholderTemplate(),
                        templateLink.getObjectiveTemplate());
        if (maybeExistingTemplateLink.isEmpty()) {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("newTemplateLink", templateLink);
                redirectAttributes.addFlashAttribute(Util.DANGER, "Specify all fields for the new template link.");
            } else {
                templateLinkRepository.save(templateLink);
                redirectAttributes.addFlashAttribute(Util.SUCCESS, String.format("Successfully created %s.", getTemplateLinkTitle(templateLink)));
            }
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, String.format("Template link %s already exists.", getTemplateLinkTitle(templateLink)));
        }
        return REDIRECT_TEMPLATE_LINK_LIST;
    }

    @PostMapping("/templateLink/{templateLinkId}/delete")
    public String processDeleteTemplateLink(@PathVariable("templateLinkId") int templateLinkId,
                                            RedirectAttributes redirectAttributes) {
        Optional<TemplateLink> maybeTemplateLink = templateLinkRepository.findById(templateLinkId);

        if (maybeTemplateLink.isPresent()) {
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", getTemplateLinkTitle(maybeTemplateLink.get())));
            templateLinkRepository.delete(maybeTemplateLink.get());
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template link");
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
