package com.soam.web.objective;

import com.soam.Util;
import com.soam.model.objective.Objective;
import com.soam.model.objective.ObjectiveRepository;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
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
public class ObjectiveFormController extends SoamFormController {
    private static final String VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM = "objective/addUpdateObjective";
    private final ObjectiveRepository objectives;
    private final ObjectiveTemplateRepository objectiveTemplates;

    private final StakeholderRepository stakeholderRepository;

    private final PriorityRepository priorities;

    public ObjectiveFormController(ObjectiveRepository objectives, ObjectiveTemplateRepository objectiveTemplates, StakeholderRepository stakeholderRepository, PriorityRepository priorities) {
        this.objectives = objectives;
        this.objectiveTemplates = objectiveTemplates;
        this.stakeholderRepository = stakeholderRepository;
        this.priorities = priorities;
    }

    @GetMapping("/stakeholder/{stakeholderId}/objective/new")
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

    @PostMapping("/stakeholder/{stakeholderId}/objective/new")
    public String processCreationForm(
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
        return "redirect:/objective/" + objective.getId();
    }

    @GetMapping("/objective/{objectiveId}/edit")
    public String initUpdateObjectiveForm(@PathVariable("objectiveId") int objectiveId, Model model) {
        Optional<Objective> maybeObjective = this.objectives.findById(objectiveId);
        if(maybeObjective.isEmpty()){
            return "redirect:/objective/find";
        }
        model.addAttribute(maybeObjective.get());
        populateFormModel(model);
        return VIEWS_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }



    @PostMapping("/objective/{objectiveId}/edit")
    public String processUpdateObjectiveForm(@Valid Objective objective, BindingResult result,
                                                 @PathVariable("objectiveId") int objectiveId, Model model) {

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
        return "redirect:/objective/{stakeholderId}";
    }

    @PostMapping("/objective/{objectiveId}/delete")
    @Transactional
    public String processDeleteStakeholder(
            @PathVariable("objectiveId") int objectiveId, Objective objective,
            BindingResult result,  Model model, RedirectAttributes redirectAttributes ){

        Optional<Objective> maybeObjective = objectives.findById(objectiveId);
        //todo: validate objectiveById's name matches the passed in Stakeholder's name.

        if(maybeObjective.isPresent()) {
            Objective fetchedObjective = maybeObjective.get();
            objectives.delete(fetchedObjective);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedObjective.getName()));
            return "redirect:/stakeholder/"+fetchedObjective.getStakeholder().getId();
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting stakeholder");
            return "redirect:/objective/"+objectiveId;
        }



    }

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("objectiveTemplates", objectiveTemplates.findAll());
    }
}
