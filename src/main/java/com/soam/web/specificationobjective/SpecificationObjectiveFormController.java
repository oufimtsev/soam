package com.soam.web.specificationobjective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
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
@RequestMapping("/specification/{specificationId}")
public class SpecificationObjectiveFormController extends SoamFormController {
    private static final String VIEWS_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM = "specificationObjective/addUpdateSpecificationObjective";
    private static final String REDIRECT_SPECIFICATION_LIST = "redirect:/specification/list";
    private static final String REDIRECT_SPECIFICATION_OBJECTIVE_LIST = "redirect:/specification/%s/specificationObjective/list";
    private static final String REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS = "redirect:/specification/%s/specificationObjective/%s";

    private final SpecificationObjectiveRepository specificationObjectives;
    private final ObjectiveTemplateRepository objectiveTemplates;
    private final SpecificationRepository specificationRepository;
    private final PriorityRepository priorities;

    public SpecificationObjectiveFormController(
            SpecificationObjectiveRepository specificationObjectives, ObjectiveTemplateRepository objectiveTemplates,
            SpecificationRepository specificationRepository, PriorityRepository priorities) {
        this.specificationObjectives = specificationObjectives;
        this.objectiveTemplates = objectiveTemplates;
        this.specificationRepository = specificationRepository;
        this.priorities = priorities;
    }

    @ModelAttribute("specification")
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId){
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElse(null);
    }

    @GetMapping("/specificationObjective/new")
    public String initCreationForm(Specification specification, Model model) {
        if (specification == null) {
            //todo: throw error!
            return REDIRECT_SPECIFICATION_LIST;
        }
        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setSpecification(specification);

        model.addAttribute("specificationObjective", specificationObjective);
        this.populateFormModel(model);
        return VIEWS_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @Valid SpecificationObjective specificationObjective, BindingResult result, Model model) {
        Optional<SpecificationObjective> testSpecificationObjective = specificationObjectives.findBySpecificationAndNameIgnoreCase(specification, specificationObjective.getName());
        if( testSpecificationObjective.isPresent()) {
            result.rejectValue("name", "unique", "Specification Objective already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return VIEWS_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        this.specificationObjectives.save(specificationObjective);
        return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, specification.getId(), specificationObjective.getId());
    }

    @GetMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String initUpdateSpecificationObjectiveForm(
            Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId, Model model) {
        Optional<SpecificationObjective> maybeSpecificationObjective = this.specificationObjectives.findById(specificationObjectiveId);
        if (maybeSpecificationObjective.isEmpty()) {
            return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
        }
        model.addAttribute(maybeSpecificationObjective.get());
        populateFormModel(model);
        return VIEWS_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/edit")
    public String processUpdateSpecificationObjectiveForm(
            @Valid SpecificationObjective specificationObjective, BindingResult result,
            @ModelAttribute(binding = false) Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId,
            Model model) {
        Optional<SpecificationObjective> testSpecificationObjective = specificationObjectives.findBySpecificationAndNameIgnoreCase(specification, specificationObjective.getName());
        testSpecificationObjective.ifPresent(s-> {
            if (testSpecificationObjective.get().getId() != specificationObjectiveId) {
                result.rejectValue("name", "unique", "Specification Objective already exists");
            }
        });
        if (result.hasErrors()) {
            specificationObjective.setId(specificationObjectiveId);
            model.addAttribute("specificationObjective", specificationObjective);
            this.populateFormModel(model);
            return VIEWS_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        specificationObjective.setId(specificationObjectiveId);
        this.specificationObjectives.save(specificationObjective);
        return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, specification.getId(), specificationObjectiveId);
    }

    @PostMapping("/specificationObjective/{specificationObjectiveId}/delete")
    @Transactional
    public String processDeleteSpecificationObjective(
            @PathVariable("specificationObjectiveId") int specificationObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            Model model, BindingResult result, RedirectAttributes redirectAttributes) {
        Optional<SpecificationObjective> maybeSpecificationObjective = specificationObjectives.findById(specificationObjectiveId);
        //todo: validate objectiveById's name matches the passed in Stakeholder's name.

        if (maybeSpecificationObjective.isPresent()) {
            SpecificationObjective fetchedSpecificationObjective = maybeSpecificationObjective.get();
            if (fetchedSpecificationObjective.getStakeholderObjectives() != null && !fetchedSpecificationObjective.getStakeholderObjectives().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any stakeholder objectives first.");
                return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
            }

            specificationObjectives.delete(fetchedSpecificationObjective);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedSpecificationObjective.getName()));
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting specification objective");
        }
        return String.format(REDIRECT_SPECIFICATION_OBJECTIVE_LIST, specification.getId());
    }

    private void populateFormModel(Model model) {
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("objectiveTemplates", objectiveTemplates.findAll());
    }
}