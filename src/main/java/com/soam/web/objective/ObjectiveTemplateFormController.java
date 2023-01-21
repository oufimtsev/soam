package com.soam.web.objective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
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
public class ObjectiveTemplateFormController extends SoamFormController {

    private static final String VIEWS_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM = "objective/template/addUpdateObjectiveTemplate";
    private static final String REDIRECT_TEMPLATE_LIST = "redirect:/objective/template/list";
    private final ObjectiveTemplateRepository objectiveTemplates;
    private final PriorityRepository priorities;

    public ObjectiveTemplateFormController(ObjectiveTemplateRepository objectiveTemplates, PriorityRepository priorities) {
        this.objectiveTemplates = objectiveTemplates;
        this.priorities = priorities;
    }

    @GetMapping("/objective/template/new")
    public String initCreationForm(Model model ) {

        ObjectiveTemplate objectiveTemplate = new ObjectiveTemplate();
        model.addAttribute("objectiveTemplate", objectiveTemplate);
        this.populateFormModel( model );

        return VIEWS_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/new")
    public String processCreationForm(@Valid ObjectiveTemplate objectiveTemplate, BindingResult result, Model model) {

        Optional<ObjectiveTemplate> testTemplate = objectiveTemplates.findByNameIgnoreCase(objectiveTemplate.getName());
        if( testTemplate.isPresent() ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel( model );
            model.addAttribute("objectiveTemplate", objectiveTemplate );
            return VIEWS_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        this.objectiveTemplates.save(objectiveTemplate);
        return REDIRECT_TEMPLATE_LIST;
    }

    @GetMapping("/objective/template/{objectiveTemplateId}/edit")
    public String initUpdateObjectiveForm(@PathVariable("objectiveTemplateId") int objectiveId, Model model) {
        Optional<ObjectiveTemplate> maybeObjectiveTemplate = this.objectiveTemplates.findById(objectiveId);
        if(maybeObjectiveTemplate.isEmpty()){
            //todo: pass error message
            return REDIRECT_TEMPLATE_LIST;
        }
        model.addAttribute(maybeObjectiveTemplate.get());
        this.populateFormModel( model );
        return VIEWS_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/edit")
    public String processUpdateObjectiveForm(@Valid ObjectiveTemplate objectiveTemplate, BindingResult result,
                                                 @PathVariable("objectiveTemplateId") int objectiveTemplateId, Model model) {

        Optional<ObjectiveTemplate> testTemplate = objectiveTemplates.findByNameIgnoreCase(objectiveTemplate.getName());
        if( testTemplate.isPresent() && testTemplate.get().getId() != objectiveTemplateId ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            objectiveTemplate.setId( objectiveTemplateId );
            model.addAttribute("objective", objectiveTemplate );
            this.populateFormModel( model );
            return VIEWS_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }


        objectiveTemplate.setId(objectiveTemplateId);
        this.objectiveTemplates.save(objectiveTemplate);
        return REDIRECT_TEMPLATE_LIST;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/delete")
    public String processDeleteObjective(
            @PathVariable("objectiveTemplateId") int objectiveTemplateId, ObjectiveTemplate objectiveTemplate,
            RedirectAttributes redirectAttributes ){

        Optional<ObjectiveTemplate> objectiveTemplateById = objectiveTemplates.findById(objectiveTemplateId);
        //todo: validate objectiveById's name matches the passed in Objective's name.

        if (objectiveTemplateById.isPresent()) {
            if (objectiveTemplateById.get().getTemplateLinks() != null && !objectiveTemplateById.get().getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any template links first.");
            } else {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", objectiveTemplateById.get().getName()));
                objectiveTemplates.delete(objectiveTemplateById.get());
            }
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template");
        }

        return REDIRECT_TEMPLATE_LIST;

    }

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("objectiveTemplates", objectiveTemplates.findAll());
    }


}
