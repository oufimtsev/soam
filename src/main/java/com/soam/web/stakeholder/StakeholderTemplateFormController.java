package com.soam.web.stakeholder;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class StakeholderTemplateFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final StakeholderTemplateRepository stakeholderTemplateRepository;
    private final PriorityRepository priorityRepository;

    public StakeholderTemplateFormController(
            StakeholderTemplateRepository stakeholderTemplateRepository, PriorityRepository priorityRepository) {
        this.stakeholderTemplateRepository = stakeholderTemplateRepository;
        this.priorityRepository = priorityRepository;
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
        stakeholderTemplateRepository.findByNameIgnoreCase(stakeholderTemplate.getName()).ifPresent(st ->
                result.rejectValue("name", "unique", "Stakeholder Template already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        stakeholderTemplateRepository.save(stakeholderTemplate);
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @GetMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String initUpdateForm(
            @PathVariable("stakeholderTemplateId") int stakeholderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<StakeholderTemplate> maybeStakeholderTemplate = stakeholderTemplateRepository.findById(stakeholderId);
        if (maybeStakeholderTemplate.isEmpty()) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Stakeholder Template does not exist.");
            return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
        }
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, maybeStakeholderTemplate.get());
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String processUpdateForm(
            @Valid StakeholderTemplate stakeholderTemplate, BindingResult result,
            @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, Model model) {
        stakeholderTemplateRepository.findByNameIgnoreCase(stakeholderTemplate.getName())
                .filter(st -> st.getId() != stakeholderTemplateId)
                .ifPresent(st -> result.rejectValue("name", "unique", "Stakeholder Template already exists."));

        stakeholderTemplate.setId(stakeholderTemplateId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        stakeholderTemplateRepository.save(stakeholderTemplate);
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/delete")
    public String processDelete(
            @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, @RequestParam("id") int formId,
            RedirectAttributes redirectAttributes) {
        if (stakeholderTemplateId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
        } else {
            Optional<StakeholderTemplate> maybeStakeholderTemplate = stakeholderTemplateRepository.findById(stakeholderTemplateId);

            if (maybeStakeholderTemplate.isPresent()) {
                if (maybeStakeholderTemplate.get().getTemplateLinks() != null && !maybeStakeholderTemplate.get().getTemplateLinks().isEmpty()) {
                    redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Template Links first.");
                } else {
                    redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", maybeStakeholderTemplate.get().getName()));
                    stakeholderTemplateRepository.delete(maybeStakeholderTemplate.get());
                }
            } else {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Error deleting Stakeholder Template.");
            }
        }

        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
