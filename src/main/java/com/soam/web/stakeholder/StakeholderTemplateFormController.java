package com.soam.web.stakeholder;

import com.soam.Util;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class StakeholderTemplateFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final StakeholderTemplateRepository stakeholderTemplateRepository;
    private final PriorityRepository priorityRepository;

    public StakeholderTemplateFormController(StakeholderTemplateRepository stakeholderTemplateRepository, PriorityRepository priorityRepository) {
        this.stakeholderTemplateRepository = stakeholderTemplateRepository;
        this.priorityRepository = priorityRepository;
    }

    @GetMapping("/stakeholder/template/new")
    public String initCreationForm(Model model ) {

        StakeholderTemplate stakeholderTemplate = new StakeholderTemplate();
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, stakeholderTemplate);
        this.populateFormModel( model );

        return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/new")
    public String processCreationForm(@Valid StakeholderTemplate stakeholderTemplate, BindingResult result, Model model) {

        Optional<StakeholderTemplate> testTemplate = stakeholderTemplateRepository.findByNameIgnoreCase(stakeholderTemplate.getName());
        if( testTemplate.isPresent() ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel( model );
            return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        this.stakeholderTemplateRepository.save(stakeholderTemplate);
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @GetMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String initUpdateStakeholderForm(@PathVariable("stakeholderTemplateId") int stakeholderId, Model model) {
        Optional<StakeholderTemplate> maybeStakeholderTemplate = this.stakeholderTemplateRepository.findById(stakeholderId);
        if(maybeStakeholderTemplate.isEmpty()){
            //todo: pass error message
            return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
        }
        model.addAttribute(maybeStakeholderTemplate.get());
        this.populateFormModel( model );
        return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String processUpdateStakeholderForm(@Valid StakeholderTemplate stakeholderTemplate, BindingResult result,
                                                 @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, Model model) {

        Optional<StakeholderTemplate> testTemplate = stakeholderTemplateRepository.findByNameIgnoreCase(stakeholderTemplate.getName());
        if( testTemplate.isPresent() && testTemplate.get().getId() != stakeholderTemplateId ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            stakeholderTemplate.setId( stakeholderTemplateId );
            model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholderTemplate);
            this.populateFormModel( model );
            return ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        stakeholderTemplate.setId(stakeholderTemplateId);
        this.stakeholderTemplateRepository.save(stakeholderTemplate);
        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/delete")
    public String processDeleteStakeholder(
            @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, StakeholderTemplate stakeholderTemplate,
            RedirectAttributes redirectAttributes ){

        Optional<StakeholderTemplate> stakeholderTemplateById = stakeholderTemplateRepository.findById(stakeholderTemplateId);
        //todo: validate stakeholderById's name matches the passed in Stakeholder's name.

        if (stakeholderTemplateById.isPresent()) {
            if (stakeholderTemplateById.get().getTemplateLinks() != null && !stakeholderTemplateById.get().getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any template links first.");
            } else {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", stakeholderTemplateById.get().getName()));
                stakeholderTemplateRepository.delete(stakeholderTemplateById.get());
            }
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template");
        }

        return RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST;
    }

    private void populateFormModel( Model model ){
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
