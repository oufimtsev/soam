package com.soam.web.stakeholder;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.SoamFormController;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class StakeholderTemplateFormController extends SoamFormController {

    private static final String VIEWS_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM = "stakeholder/template/addUpdateStakeholderTemplate";
    private static final String REDIRECT_TEMPLATE_LIST = "redirect:/stakeholder/template/list";

    private final StakeholderRepository stakeholders;
    private final StakeholderTemplateRepository stakeholderTemplates;
    private final PriorityRepository priorities;

    public StakeholderTemplateFormController(StakeholderRepository stakeholders, StakeholderTemplateRepository stakeholderTemplates, PriorityRepository priorities) {
        this.stakeholders = stakeholders;
        this.stakeholderTemplates = stakeholderTemplates;
        this.priorities = priorities;
    }

    @GetMapping("/stakeholder/template/new")
    public String initCreationForm(Model model ) {

        StakeholderTemplate stakeholderTemplate = new StakeholderTemplate();
        model.addAttribute("stakeholderTemplate", stakeholderTemplate);
        this.populateFormModel( model );

        return VIEWS_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/new")
    public String processCreationForm(@Valid StakeholderTemplate stakeholderTemplate, BindingResult result, Model model) {

        Optional<StakeholderTemplate> testTemplate = stakeholderTemplates.findByNameIgnoreCase(stakeholderTemplate.getName());
        if( testTemplate.isPresent() ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel( model );
            return VIEWS_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        this.stakeholderTemplates.save(stakeholderTemplate);
        return REDIRECT_TEMPLATE_LIST;
    }

    @GetMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String initUpdateStakeholderForm(@PathVariable("stakeholderTemplateId") int stakeholderId, Model model) {
        Optional<StakeholderTemplate> maybeStakeholderTemplate = this.stakeholderTemplates.findById(stakeholderId);
        if(maybeStakeholderTemplate.isEmpty()){
            //todo: pass error message
            return REDIRECT_TEMPLATE_LIST;
        }
        model.addAttribute(maybeStakeholderTemplate.get());
        this.populateFormModel( model );
        return VIEWS_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/edit")
    public String processUpdateStakeholderForm(@Valid StakeholderTemplate stakeholderTemplate, BindingResult result,
                                                 @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, Model model) {

        Optional<StakeholderTemplate> testTemplate = stakeholderTemplates.findByNameIgnoreCase(stakeholderTemplate.getName());
        if( testTemplate.isPresent() && testTemplate.get().getId() != stakeholderTemplateId ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            stakeholderTemplate.setId( stakeholderTemplateId );
            model.addAttribute("stakeholder", stakeholderTemplate );
            this.populateFormModel( model );
            return VIEWS_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM;
        }


        stakeholderTemplate.setId(stakeholderTemplateId);
        this.stakeholderTemplates.save(stakeholderTemplate);
        return REDIRECT_TEMPLATE_LIST;
    }

    @PostMapping("/stakeholder/template/{stakeholderTemplateId}/delete")
    public String processDeleteStakeholder(
            @PathVariable("stakeholderTemplateId") int stakeholderTemplateId, StakeholderTemplate stakeholderTemplate,
            RedirectAttributes redirectAttributes ){

        Optional<StakeholderTemplate> stakeholderTemplateById = stakeholderTemplates.findById(stakeholderTemplateId);
        //todo: validate stakeholderById's name matches the passed in Stakeholder's name.

        if( stakeholderTemplateById.isPresent()) {
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", stakeholderTemplateById.get().getName()));
            stakeholderTemplates.delete(stakeholderTemplateById.get());
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template");
        }

        return "redirect:/stakeholder/template/list";

    }

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("stakeholderTemplates", stakeholderTemplates.findAll());
    }


}
