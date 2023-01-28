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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}")
public class StakeholderFormController extends SoamFormController {
    private static final String ATTR_SPECIFICATION = "specification";
    private static final String ATTR_STAKEHOLDER = "stakeholder";
    private static final String ATTR_STAKEHOLDER_TEMPLATES = "stakeholderTemplates";
    private static final String ATTR_PRIORITIES = "priorities";

    private static final String VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM = "stakeholder/addUpdateStakeholder";
    private static final String REDIRECT_STAKEHOLDER_DETAILS = "redirect:/specification/%s/stakeholder/%s";

    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

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

    @ModelAttribute(ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId){
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElse(null);
    }


    @GetMapping("/stakeholder/{stakeholderId}")
    public String showStakeholder( @PathVariable("specificationId") int specificationId,
                                   @PathVariable("stakeholderId") int stakeholderId, Model model) {
        Optional<Stakeholder> maybeStakeholder = this.stakeholders.findById(stakeholderId);
        if(maybeStakeholder.isEmpty()){
            return "redirect:/specification/"+specificationId;
        }
        model.addAttribute(maybeStakeholder.get());
        return "stakeholder/stakeholderDetails";
    }

    @GetMapping("/stakeholder/new")
    public String initCreationForm(@PathVariable("specificationId") int specificationId, Model model) {
        Optional<Specification> maybeSpecification = specificationRepository.findById(specificationId);
        if( maybeSpecification.isEmpty() ){
            //todo: throw error!
            return "redirect:/specification/list";
        }

        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setSpecification( maybeSpecification.get() );

        model.addAttribute(ATTR_STAKEHOLDER, stakeholder);
        this.populateFormModel( model );
        return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/new")
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
        return String.format(REDIRECT_STAKEHOLDER_DETAILS, specificationId, stakeholder.getId() );
    }

    @GetMapping("/stakeholder/{stakeholderId}/edit")
    public String initUpdateStakeholderForm(
            @PathVariable("stakeholderId") int stakeholderId, @PathVariable("specificationId") int specificationId, Model model) {
        Optional<Stakeholder> maybeStakeholder = this.stakeholders.findById(stakeholderId);
        if(maybeStakeholder.isEmpty()){
            return "redirect:/specification/"+specificationId;
        }
        model.addAttribute(maybeStakeholder.get());
        populateFormModel(model);
        return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }



    @PostMapping("/stakeholder/{stakeholderId}/edit")
    public String processUpdateStakeholderForm(@Valid Stakeholder stakeholder, BindingResult result,
                                                 @PathVariable("specificationId") int specificationId,
                                                 @PathVariable("stakeholderId") int stakeholderId, Model model) {

        Optional<Stakeholder> testStakeholder = stakeholders.findByNameIgnoreCase(stakeholder.getName());
        testStakeholder.ifPresent(s-> {
            if( testStakeholder.get().getId() != stakeholderId ){
                result.rejectValue("name", "unique", "Stakeholder already exists");
            }
        });

        stakeholder.setId( stakeholderId );

        if (result.hasErrors()) {
            model.addAttribute(ATTR_STAKEHOLDER, stakeholder );
            this.populateFormModel( model );
            return VIEWS_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }


        this.stakeholders.save(stakeholder);
        return String.format(REDIRECT_STAKEHOLDER_DETAILS,specificationId, stakeholderId);
    }

    @PostMapping("/stakeholder/{stakeholderId}/delete")
    @Transactional
    public String processDeleteStakeholder(
            @PathVariable("specificationId") int specificationId, @PathVariable("stakeholderId") int stakeholderId, Stakeholder stakeholder,
            BindingResult result, Model model, RedirectAttributes redirectAttributes ){

        Optional<Stakeholder> maybeStakeholder = stakeholders.findById(stakeholderId);
        //todo: validate stakeholderById's name matches the passed in Stakeholder's name.

        if(maybeStakeholder.isPresent()) {
            Stakeholder fetchedStakeholder = maybeStakeholder.get();
            if(fetchedStakeholder.getStakeholderObjectives() != null && !fetchedStakeholder.getStakeholderObjectives().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any stakeholder objectives first.");
                return String.format(REDIRECT_STAKEHOLDER_DETAILS, specificationId, stakeholderId);
            }
            stakeholders.delete(fetchedStakeholder);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedStakeholder.getName()));
            return "redirect:/specification/"+fetchedStakeholder.getSpecification().getId();
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting stakeholder");
            return String.format("redirect:/specification/%s", specificationId );
        }
    }

    private void populateFormModel( Model model ){
        model.addAttribute(ATTR_PRIORITIES, priorities.findAll());
        model.addAttribute(ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplates.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
