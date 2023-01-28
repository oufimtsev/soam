package com.soam.web.stakeholder;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
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
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final StakeholderRepository stakeholderRepository;
    private final StakeholderTemplateRepository stakeholderTemplateRepository;

    private final SpecificationRepository specificationRepository;

    private final PriorityRepository priorityRepository;

    public StakeholderFormController(StakeholderRepository stakeholderRepository, StakeholderTemplateRepository stakeholderTemplateRepository, SpecificationRepository specificationRepository, PriorityRepository priorityRepository) {
        this.stakeholderRepository = stakeholderRepository;
        this.stakeholderTemplateRepository = stakeholderTemplateRepository;
        this.specificationRepository = specificationRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId){
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElse(null);
    }

    @GetMapping("/stakeholder/{stakeholderId}")
    public String showStakeholder( @PathVariable("specificationId") int specificationId,
                                   @PathVariable("stakeholderId") int stakeholderId, Model model) {
        Optional<Stakeholder> maybeStakeholder = this.stakeholderRepository.findById(stakeholderId);
        if(maybeStakeholder.isEmpty()) {
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
        }
        model.addAttribute(maybeStakeholder.get());
        return ViewConstants.VIEW_STAKEHOLDER_DETAILS;
    }

    @GetMapping("/stakeholder/new")
    public String initCreationForm(@PathVariable("specificationId") int specificationId, Model model) {
        Optional<Specification> maybeSpecification = specificationRepository.findById(specificationId);
        if( maybeSpecification.isEmpty() ){
            //todo: throw error!
            return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
        }

        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setSpecification( maybeSpecification.get() );

        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder);
        this.populateFormModel( model );
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/new")
    public String processCreationForm(
            @PathVariable("specificationId") int specificationId,
            @Valid Stakeholder stakeholder, BindingResult result, Model model) {
        //todo: test specificationId path variable matches bound stakeholder.specificationId

        Optional<Stakeholder> testStakeholder = stakeholderRepository.findByNameIgnoreCase(stakeholder.getName());
        if( testStakeholder.isPresent() ){
            result.rejectValue("name", "unique", "Stakeholder already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        this.stakeholderRepository.save(stakeholder);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specificationId, stakeholder.getId() );
    }

    @GetMapping("/stakeholder/{stakeholderId}/edit")
    public String initUpdateStakeholderForm(
            @PathVariable("stakeholderId") int stakeholderId, @PathVariable("specificationId") int specificationId, Model model) {
        Optional<Stakeholder> maybeStakeholder = this.stakeholderRepository.findById(stakeholderId);
        if(maybeStakeholder.isEmpty()) {
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
        }
        model.addAttribute(maybeStakeholder.get());
        populateFormModel(model);
        return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholder/{stakeholderId}/edit")
    public String processUpdateStakeholderForm(@Valid Stakeholder stakeholder, BindingResult result,
                                                 @PathVariable("specificationId") int specificationId,
                                                 @PathVariable("stakeholderId") int stakeholderId, Model model) {

        Optional<Stakeholder> testStakeholder = stakeholderRepository.findByNameIgnoreCase(stakeholder.getName());
        testStakeholder.ifPresent(s-> {
            if( testStakeholder.get().getId() != stakeholderId ){
                result.rejectValue("name", "unique", "Stakeholder already exists");
            }
        });

        stakeholder.setId( stakeholderId );

        if (result.hasErrors()) {
            model.addAttribute(ModelConstants.ATTR_STAKEHOLDER, stakeholder );
            this.populateFormModel( model );
            return ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM;
        }

        this.stakeholderRepository.save(stakeholder);
        return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specificationId, stakeholderId);
    }

    @PostMapping("/stakeholder/{stakeholderId}/delete")
    @Transactional
    public String processDeleteStakeholder(
            @PathVariable("specificationId") int specificationId, @PathVariable("stakeholderId") int stakeholderId, Stakeholder stakeholder,
            BindingResult result, Model model, RedirectAttributes redirectAttributes ){

        Optional<Stakeholder> maybeStakeholder = stakeholderRepository.findById(stakeholderId);
        //todo: validate stakeholderById's name matches the passed in Stakeholder's name.

        if(maybeStakeholder.isPresent()) {
            Stakeholder fetchedStakeholder = maybeStakeholder.get();
            if(fetchedStakeholder.getStakeholderObjectives() != null && !fetchedStakeholder.getStakeholderObjectives().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any stakeholder objectives first.");
                return String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, specificationId, stakeholderId);
            }
            stakeholderRepository.delete(fetchedStakeholder);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedStakeholder.getName()));
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, fetchedStakeholder.getSpecification().getId());
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting stakeholder");
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
        }
    }

    private void populateFormModel( Model model ){
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES, stakeholderTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
