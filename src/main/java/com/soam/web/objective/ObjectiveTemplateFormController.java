package com.soam.web.objective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
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
public class ObjectiveTemplateFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final ObjectiveTemplateRepository objectiveTemplateRepository;
    private final PriorityRepository priorityRepository;

    public ObjectiveTemplateFormController(ObjectiveTemplateRepository objectiveTemplateRepository, PriorityRepository priorityRepository) {
        this.objectiveTemplateRepository = objectiveTemplateRepository;
        this.priorityRepository = priorityRepository;
    }

    @GetMapping("/objective/template/new")
    public String initCreationForm(Model model ) {

        ObjectiveTemplate objectiveTemplate = new ObjectiveTemplate();
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, objectiveTemplate);
        this.populateFormModel( model );

        return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/new")
    public String processCreationForm(@Valid ObjectiveTemplate objectiveTemplate, BindingResult result, Model model) {

        Optional<ObjectiveTemplate> testTemplate = objectiveTemplateRepository.findByNameIgnoreCase(objectiveTemplate.getName());
        if( testTemplate.isPresent() ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel( model );
            model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, objectiveTemplate );
            return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        this.objectiveTemplateRepository.save(objectiveTemplate);
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @GetMapping("/objective/template/{objectiveTemplateId}/edit")
    public String initUpdateObjectiveForm(@PathVariable("objectiveTemplateId") int objectiveId, Model model,
                                          RedirectAttributes redirectAttributes) {
        Optional<ObjectiveTemplate> maybeObjectiveTemplate = this.objectiveTemplateRepository.findById(objectiveId);
        if (maybeObjectiveTemplate.isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Objective template does not exist");
            return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
        }
        model.addAttribute(maybeObjectiveTemplate.get());
        this.populateFormModel( model );
        return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/edit")
    public String processUpdateObjectiveForm(@Valid ObjectiveTemplate objectiveTemplate, BindingResult result,
                                                 @PathVariable("objectiveTemplateId") int objectiveTemplateId, Model model) {

        Optional<ObjectiveTemplate> testTemplate = objectiveTemplateRepository.findByNameIgnoreCase(objectiveTemplate.getName());
        if( testTemplate.isPresent() && testTemplate.get().getId() != objectiveTemplateId ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            objectiveTemplate.setId( objectiveTemplateId );
            model.addAttribute("objective", objectiveTemplate );
            this.populateFormModel( model );
            return ViewConstants.VIEW_OBJECTIVE_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        objectiveTemplate.setId(objectiveTemplateId);
        this.objectiveTemplateRepository.save(objectiveTemplate);
        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    @PostMapping("/objective/template/{objectiveTemplateId}/delete")
    public String processDeleteObjective(
            @PathVariable("objectiveTemplateId") int objectiveTemplateId, ObjectiveTemplate objectiveTemplate,
            RedirectAttributes redirectAttributes ){

        Optional<ObjectiveTemplate> objectiveTemplateById = objectiveTemplateRepository.findById(objectiveTemplateId);
        //todo: validate objectiveById's name matches the passed in Objective's name.

        if (objectiveTemplateById.isPresent()) {
            if (objectiveTemplateById.get().getTemplateLinks() != null && !objectiveTemplateById.get().getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any template links first.");
            } else {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", objectiveTemplateById.get().getName()));
                objectiveTemplateRepository.delete(objectiveTemplateById.get());
            }
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template");
        }

        return RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_LIST;
    }

    private void populateFormModel( Model model ){
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_OBJECTIVE_TEMPLATES, objectiveTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
