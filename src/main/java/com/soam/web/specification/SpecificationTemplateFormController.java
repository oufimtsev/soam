package com.soam.web.specification;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Controller
public class SpecificationTemplateFormController  implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final SpecificationTemplateRepository specificationTemplateRepository;
    private final TemplateLinkRepository templateLinkRepository;
    private final PriorityRepository priorityRepository;

    public SpecificationTemplateFormController(
            SpecificationTemplateRepository specificationTemplateRepository,
            TemplateLinkRepository templateLinkRepository, PriorityRepository priorityRepository) {
        this.specificationTemplateRepository = specificationTemplateRepository;
        this.templateLinkRepository = templateLinkRepository;
        this.priorityRepository = priorityRepository;
    }

    @GetMapping("/specification/template/new")
    public String initCreationForm(Model model) {
        SpecificationTemplate specificationTemplate = new SpecificationTemplate();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, specificationTemplate);
        populateFormModel(model);

        return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/new")
    public String processCreationForm(
            @Valid SpecificationTemplate specificationTemplate, BindingResult result,
            @ModelAttribute(ModelConstants.ATTR_COLLECTION_TYPE) String collectionType,
            @ModelAttribute(ModelConstants.ATTR_COLLECTION_ITEM_ID) int collectionItemId,
            Model model, RedirectAttributes redirectAttributes) {
        specificationTemplateRepository.findByNameIgnoreCase(specificationTemplate.getName()).ifPresent(st ->
                result.rejectValue("name", "unique", "Specification Template already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        if ("templateDeepCopy".equals(collectionType)) {
            //creating new Specification Template as a deep copy of source Specification Template
            Optional<SpecificationTemplate> maybeSrcSpecificationTemplate = specificationTemplateRepository.findById(collectionItemId);
            if (maybeSrcSpecificationTemplate.isPresent()) {
                specificationTemplateRepository.save(specificationTemplate);
                deepTemplateCopy(maybeSrcSpecificationTemplate.get(), specificationTemplate);
                redirectAttributes.addFlashAttribute(
                        Util.SUCCESS,
                        String.format("Created %s", getSpecificationTemplateOverviewMessage(specificationTemplate))
                );
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Source Specification Template does not exist.");
            }
        } else {
            specificationTemplateRepository.save(specificationTemplate);
        }
        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    @GetMapping("/specification/template/{specificationTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("specificationTemplateId") int specificationId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<SpecificationTemplate> maybeSpecificationTemplate = specificationTemplateRepository.findById(specificationId);
        if (maybeSpecificationTemplate.isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Specification Template does not exist.");
            return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
        }
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, maybeSpecificationTemplate.get());
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/{specificationTemplateId}/edit")
    public String processUpdateForm(
            @Valid SpecificationTemplate specificationTemplate, BindingResult result,
            @PathVariable("specificationTemplateId") int specificationTemplateId, Model model) {
        specificationTemplateRepository.findByNameIgnoreCase(specificationTemplate.getName())
                .filter(st -> st.getId() != specificationTemplateId)
                .ifPresent(st -> result.rejectValue("name", "unique", "Specification Template already exists."));

        specificationTemplate.setId(specificationTemplateId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        specificationTemplateRepository.save(specificationTemplate);
        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    @PostMapping("/specification/template/{specificationTemplateId}/delete")
    public String processDelete(
            @PathVariable("specificationTemplateId") int specificationTemplateId, @RequestParam("id") int formId,
            RedirectAttributes redirectAttributes) {
        if (specificationTemplateId != formId) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Malformed request.");
        } else {
            Optional<SpecificationTemplate> maybeSpecificationTemplate = specificationTemplateRepository.findById(specificationTemplateId);

            if (maybeSpecificationTemplate.isPresent()) {
                if (maybeSpecificationTemplate.get().getTemplateLinks() != null && !maybeSpecificationTemplate.get().getTemplateLinks().isEmpty()) {
                    redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any Template Links first.");
                } else {
                    redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s.", maybeSpecificationTemplate.get().getName()));
                    specificationTemplateRepository.delete(maybeSpecificationTemplate.get());
                }
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting Specification Template.");
            }
        }

        return RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }

    private void deepTemplateCopy(
            SpecificationTemplate srcSpecificationTemplate, SpecificationTemplate dstSpecificationTemplate) {
        dstSpecificationTemplate.setTemplateLinks(new LinkedList<>());

        Collection<TemplateLink> templateLinks = srcSpecificationTemplate.getTemplateLinks();

        templateLinks.forEach(templateLink -> {
            TemplateLink newTemplateLink = new TemplateLink();
            newTemplateLink.setSpecificationTemplate(dstSpecificationTemplate);
            newTemplateLink.setStakeholderTemplate(templateLink.getStakeholderTemplate());
            newTemplateLink.setObjectiveTemplate(templateLink.getObjectiveTemplate());
            dstSpecificationTemplate.getTemplateLinks().add(newTemplateLink);
            templateLinkRepository.save(newTemplateLink);
        });
    }

    private String getSpecificationTemplateOverviewMessage(SpecificationTemplate specificationTemplate) {
        return String.format(
                "Specification Template %s with %d Template Link(s)",
                specificationTemplate.getName(),
                specificationTemplate.getTemplateLinks().size()
        );
    }
}
