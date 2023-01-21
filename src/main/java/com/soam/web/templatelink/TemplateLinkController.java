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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class TemplateLinkController {
    private static final String VIEWS_TEMPLATE_LINK_LIST = "templateLink/templateLinkList";
    private static final String REDIRECT_TEMPLATE_LINK_LIST = "redirect:/templateLink/list";
    //human-friendly template link title in form of 'specification template name / stakeholder template name / objective template name'
    private static final String TEMPLATE_LINK_TITLE = "%s / %s / %s";

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
        return specificationTemplateRepository.findAll();
    }

    @ModelAttribute("stakeholderTemplates")
    public List<StakeholderTemplate> populateStakeholderTemplates() {
        return stakeholderTemplateRepository.findAll();
    }

    @ModelAttribute("objectiveTemplates")
    public List<ObjectiveTemplate> populateObjectiveTemplates() {
        return objectiveTemplateRepository.findAll();
    }

    @ModelAttribute("newTemplateLink")
    public TemplateLink populateNewTemplateLink() {
        return new TemplateLink();
    }

    @GetMapping("/templateLink/list")
    public String listTemplateLinks(Model model) {
        model.addAttribute("templateLinks", templateLinkRepository.findAll());
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
            templateLinkRepository.save(templateLink);
            redirectAttributes.addFlashAttribute(Util.SUCCESS, String.format("Successfully created %s.", getTemplateLinkTitle(templateLink)));
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
