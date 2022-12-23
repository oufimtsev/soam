package com.soam.web.objective;

import com.soam.Util;
import com.soam.model.objective.Objective;
import com.soam.model.objective.ObjectiveRepository;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.web.SoamFormController;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}/stakeholder/{stakeholderId}")
public class ObjectiveFormController extends SoamFormController {
    private static final String VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM = "objective/addUpdateObjective";
    private final ObjectiveRepository objectives;
    private final ObjectiveTemplateRepository objectiveTemplates;

    private final StakeholderRepository stakeholderRepository;

    private final SpecificationRepository specificationRepository;

    private final PriorityRepository priorities;

    public ObjectiveFormController(ObjectiveRepository objectives, ObjectiveTemplateRepository objectiveTemplates, StakeholderRepository stakeholderRepository, SpecificationRepository specificationRepository, PriorityRepository priorities) {
        this.objectives = objectives;
        this.objectiveTemplates = objectiveTemplates;
        this.stakeholderRepository = stakeholderRepository;
        this.specificationRepository = specificationRepository;
        this.priorities = priorities;
    }

    @ModelAttribute("specification")
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId){
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElseGet(null);
    }

    @ModelAttribute("stakeholder")
    public Stakeholder populateStakeholder(@PathVariable("stakeholderId") int stakeholderId){
        Optional<Stakeholder> oStakeholder = stakeholderRepository.findById(stakeholderId);
        return oStakeholder.orElseGet(null);
    }

    @GetMapping("/objective/{objectiveId}")
    public String showObjective(Specification specification, Stakeholder stakeholder,
                                @PathVariable("objectiveId") int objectiveId, Model model) {
        Optional<Objective> maybeObjective = this.objectives.findById(objectiveId);
        if(maybeObjective.isEmpty()){
            return String.format("redirect:/specification/%s/stakeholder/%s", specification.getId(), stakeholder.getId());
        }
        model.addAttribute(maybeObjective.get());
        return "objective/objectiveDetails";
    }

    @GetMapping("/objective/new")
    public String initCreationForm(@PathVariable("stakeholderId") int stakeholderId, Model model) {
        Optional<Stakeholder> maybeStakeholder = stakeholderRepository.findById(stakeholderId);
        if( maybeStakeholder.isEmpty() ){
            //todo: throw error!
        }

        Objective objective = new Objective();
        objective.setStakeholder( maybeStakeholder.get() );

        model.addAttribute("objective", objective);
        this.populateFormModel( model );
        return VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/new")
    public String processCreationForm(
            @PathVariable("specificationId") int specificationId,
            @PathVariable("stakeholderId") int stakeholderId,
            @Valid Objective objective, BindingResult result, Model model) {
        //todo: test stakeholderId path variable matches bound objective.stakeholderId

        Optional<Objective> testObjective = objectives.findByNameIgnoreCase(objective.getName());
        if( testObjective.isPresent() ){
            result.rejectValue("name", "unique", "Objective already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        this.objectives.save(objective);
        return String.format("redirect:/specification/%s/stakeholder/%s/objective/%s", specificationId, stakeholderId, objective.getId());
    }

    @GetMapping("/objective/{objectiveId}/edit")
    public String initUpdateObjectiveForm(@PathVariable("specificationId") int specificationId, @PathVariable("stakeholderId") int stakeholderId,
                                          @PathVariable("objectiveId") int objectiveId, Model model) {
        Optional<Objective> maybeObjective = this.objectives.findById(objectiveId);
        if(maybeObjective.isEmpty()){
            return String.format( "redirect:/specification/%s/stakeholder/%s", specificationId, stakeholderId );
        }
        model.addAttribute(maybeObjective.get());
        populateFormModel(model);
        return VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }



    @PostMapping("/objective/{objectiveId}/edit")
    public String processUpdateObjectiveForm(@Valid Objective objective, BindingResult result,
                                                 @PathVariable("objectiveId") int objectiveId,
                                             @PathVariable("stakeholderId") int stakeholderId,
                                             @PathVariable("specificationId") int specificationId, Model model) {

        Optional<Objective> testObjective = objectives.findByNameIgnoreCase(objective.getName());
        testObjective.ifPresent(s-> {
            if( testObjective.get().getId() != objectiveId ){
                result.rejectValue("name", "unique", "objective already exists");
            }
        });
        if (result.hasErrors()) {
            objective.setId( objectiveId );
            model.addAttribute("objective", objective );
            this.populateFormModel( model );
            return VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        objective.setId(objectiveId);
        this.objectives.save(objective);
        return String.format("redirect:/specification/%s/stakeholder/%s/objective/%s", specificationId, stakeholderId, objectiveId);
    }

    @PostMapping("/objective/{objectiveId}/delete")
    @Transactional
    public String processDeleteObjective(
            @PathVariable("objectiveId") int objectiveId, Objective objective,
            BindingResult result, @PathVariable("specificationId") int specificationId,
            @PathVariable("stakeholderId") int stakeholderId,
            Model model, RedirectAttributes redirectAttributes ){

        Optional<Objective> maybeObjective = objectives.findById(objectiveId);
        //todo: validate objectiveById's name matches the passed in Stakeholder's name.

        if(maybeObjective.isPresent()) {
            Objective fetchedObjective = maybeObjective.get();
            objectives.delete(fetchedObjective);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedObjective.getName()));
            return String.format("redirect:/specification/%s/stakeholder/%s",specificationId,stakeholderId);
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting stakeholder");
            return String.format("redirect:/specification/%s/stakeholder/%s/objective/%s",
                    specificationId, stakeholderId, objectiveId);
        }
    }

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("objectiveTemplates", objectiveTemplates.findAll());
    }
}
