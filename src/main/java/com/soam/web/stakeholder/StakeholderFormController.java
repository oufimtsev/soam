package com.soam.web.stakeholder;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.SoamFormController;
import jakarta.transaction.Transactional;
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
public class StakeholderFormController extends SoamFormController {
    private static final String VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM = "stakeholder/addUpdateStakeholder";
    private final StakeholderRepository stakeholders;
    private final StakeholderTemplateRepository stakeholderTemplates;

    private final SpecificationRepository specificationRepository;

    private final PriorityRepository priorities;

    public StakeholderFormController(StakeholderRepository stakeholders, StakeholderTemplateRepository stakeholderTemplates, SpecificationRepository specificationRepository, PriorityRepository priorities) {
        this.stakeholders = stakeholders;
        this.stakeholderTemplates = stakeholderTemplates;
        this.specificationRepository = specificationRepository;
        this.priorities = priorities;
    }

    @GetMapping("/specification/{specificationId}/stakeholder/new")
    public String initCreationForm(@PathVariable("specificationId") int specificationId, Model model) {
        Optional<Specification> maybeSpecification = specificationRepository.findById(specificationId);
        if( maybeSpecification.isEmpty() ){
            //todo: throw error!
        }

        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setSpecification( maybeSpecification.get() );

        model.addAttribute("stakeholder", stakeholder);
        this.populateFormModel( model );
        return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/{specificationId}/stakeholder/new")
    public String processCreationForm(
            @PathVariable("specificationId") int specificationId,
            @Valid Stakeholder stakeholder, BindingResult result, Model model) {
        //todo: test specificationId path variable matches bound stakeholder.specificationId

        Optional<Stakeholder> testStakeholder = stakeholders.findByNameIgnoreCase(stakeholder.getName());
        if( testStakeholder.isPresent() ){
            result.rejectValue("name", "unique", "Stakeholder already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        this.stakeholders.save(stakeholder);
        return "redirect:/stakeholder/" + stakeholder.getId();
    }

    @GetMapping("/stakeholder/{stakeholderId}/edit")
    public String initUpdateStakeholderForm(@PathVariable("stakeholderId") int stakeholderId, Model model) {
        Optional<Stakeholder> maybeStakeholder = this.stakeholders.findById(stakeholderId);
        if(maybeStakeholder.isEmpty()){
            return "redirect:/stakeholder/find";
        }
        model.addAttribute(maybeStakeholder.get());
        populateFormModel(model);
        return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }



    @PostMapping("/stakeholder/{stakeholderId}/edit")
    public String processUpdateStakeholderForm(@Valid Stakeholder stakeholder, BindingResult result,
                                                 @PathVariable("stakeholderId") int stakeholderId, Model model) {

        Optional<Stakeholder> testStakeholder = stakeholders.findByNameIgnoreCase(stakeholder.getName());
        testStakeholder.ifPresent(s-> {
            if( testStakeholder.get().getId() != stakeholderId ){
                result.rejectValue("name", "unique", "Stakeholder already exists");
            }
        });
        if (result.hasErrors()) {
            stakeholder.setId( stakeholderId );
            model.addAttribute("stakeholder", stakeholder );
            this.populateFormModel( model );
            return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        stakeholder.setId(stakeholderId);
        this.stakeholders.save(stakeholder);
        return "redirect:/stakeholder/{stakeholderId}";
    }

    @PostMapping("/stakeholder/{stakeholderId}/delete")
    @Transactional
    public String processDeleteStakeholder(
            @PathVariable("stakeholderId") int stakeholderId, Stakeholder stakeholder,
            BindingResult result,  Model model, RedirectAttributes redirectAttributes ){

        Optional<Stakeholder> maybeStakeholder = stakeholders.findById(stakeholderId);
        //todo: validate stakeholderById's name matches the passed in Stakeholder's name.

        if(maybeStakeholder.isPresent()) {
            Stakeholder fetchedStakeholder = maybeStakeholder.get();
            stakeholders.delete(fetchedStakeholder);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedStakeholder.getName()));
            return "redirect:/specification/"+fetchedStakeholder.getSpecification().getId();
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting stakeholder");
            return "redirect:/stakeholder/"+stakeholderId;
        }



    }

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("stakeholderTemplates", stakeholderTemplates.findAll());
    }
}
