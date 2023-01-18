package com.soam.web.stakeholderobjective;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
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
public class StakeholderObjectiveFormController extends SoamFormController {
    private static final String VIEWS_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM = "stakeholderObjective/addUpdateStakeholderObjective";
    private static final String REDIRECT_SPECIFICATION_LIST = "redirect:/specification/list";
    private static final String REDIRECT_SPECIFICATION_DETAILS = "redirect:/specification/%s";
    private static final String REDIRECT_STAKEHOLDER_DETAILS = "redirect:/specification/%s/stakeholder/%s";
    private static final String REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS = "redirect:/specification/%s/stakeholder/%s/stakeholderObjective/%s";

    private final StakeholderObjectiveRepository stakeholderObjectives;
    private final SpecificationRepository specificationRepository;
    private final StakeholderRepository stakeholderRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final PriorityRepository priorities;

    public StakeholderObjectiveFormController(
            StakeholderObjectiveRepository stakeholderObjectives, SpecificationRepository specificationRepository,
            StakeholderRepository stakeholderRepository,
            SpecificationObjectiveRepository specificationObjectiveRepository, PriorityRepository priorities) {
        this.stakeholderObjectives = stakeholderObjectives;
        this.specificationRepository = specificationRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.priorities = priorities;
    }

    @ModelAttribute("specification")
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElse(null);
    }

    @ModelAttribute("stakeholder")
    public Stakeholder populateStakeholder(@PathVariable("stakeholderId") int stakeholderId) {
        Optional<Stakeholder> oStakeholder = stakeholderRepository.findById(stakeholderId);
        return oStakeholder.orElse(null);
    }

    @GetMapping("/stakeholderObjective/{stakeholderObjectiveId}")
    public String showStakeholderObjective(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId, Model model) {
        Optional<StakeholderObjective> maybeStakeholderObjective = this.stakeholderObjectives.findById(stakeholderObjectiveId);
        if(maybeStakeholderObjective.isEmpty()) {
            return String.format(REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        }
        model.addAttribute(maybeStakeholderObjective.get());
        return "stakeholderObjective/stakeholderObjectiveDetails";
    }

    @GetMapping("/stakeholderObjective/new")
    public String initCreationForm(
            Specification specification, Stakeholder stakeholder, Model model, RedirectAttributes redirectAttributes) {
        if (specification == null) {
            return REDIRECT_SPECIFICATION_LIST;
        }
        if (stakeholder == null) {
            return String.format(REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }
        if (specification.getSpecificationObjectives().isEmpty()) {
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Specification does not have any Specification Objectives");
            return String.format(REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        }

        SpecificationObjective specificationObjective = new SpecificationObjective();
        specificationObjective.setId(-1);

        StakeholderObjective stakeholderObjective = new StakeholderObjective();
        stakeholderObjective.setStakeholder(stakeholder);
        stakeholderObjective.setSpecificationObjective(specificationObjective);
        stakeholderObjective.setNotes(specificationObjective.getNotes());
        stakeholderObjective.setPriority(specificationObjective.getPriority());

        model.addAttribute("stakeholderObjective", stakeholderObjective);
        this.populateFormModel(model);
        return VIEWS_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholderObjective/new")
    public String processCreationForm(
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            @RequestParam("templateId") int specificationObjectiveId,
            @Valid StakeholderObjective stakeholderObjective, BindingResult result, Model model) {

        Optional<SpecificationObjective> testSpecificationObjective = specificationObjectiveRepository.findById(specificationObjectiveId);
        if (testSpecificationObjective.isEmpty()) {
            SpecificationObjective emptySeSpecificationObjective = new SpecificationObjective();
            emptySeSpecificationObjective.setId(-1);
            stakeholderObjective.setSpecificationObjective(emptySeSpecificationObjective);
            result.rejectValue("specificationObjective", "required", "Specification Objective should not be empty");
        } else {
            stakeholderObjective.setSpecificationObjective(testSpecificationObjective.get());

            Optional<StakeholderObjective> testStakeholderObjective = stakeholderObjectives.findByStakeholderAndSpecificationObjectiveId(stakeholder, specificationObjectiveId);
            if (testStakeholderObjective.isPresent()) {
                testStakeholderObjective.get().setSpecificationObjective(testSpecificationObjective.get());
                result.rejectValue("specificationObjective", "unique", "Stakeholder Objective already exists");
            }
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return VIEWS_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }

        stakeholderObjectives.save(stakeholderObjective);

        return String.format(REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS, specification.getId(), stakeholder.getId(), stakeholderObjective.getId());
    }

    @GetMapping("/stakeholderObjective/{stakeholderObjectiveId}/edit")
    public String initUpdateStakeholderObjectiveForm(
            Specification specification, Stakeholder stakeholder,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId, Model model) {
        Optional<StakeholderObjective> maybeStakeholderObjective = this.stakeholderObjectives.findById(stakeholderObjectiveId);
        if(maybeStakeholderObjective.isEmpty()) {
            return String.format(REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        }
        model.addAttribute(maybeStakeholderObjective.get());
        populateFormModel(model);
        return VIEWS_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/stakeholderObjective/{stakeholderObjectiveId}/edit")
    public String processUpdateStakeholderObjectiveForm(
            @Valid StakeholderObjective stakeholderObjective, BindingResult result,
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder, Model model) {

        if (result.hasErrors()) {
            stakeholderObjective.setId(stakeholderObjectiveId);
            model.addAttribute("stakeholderObjective", stakeholderObjective);
            this.populateFormModel(model);
            return VIEWS_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM;
        }
        stakeholderObjective.setId(stakeholderObjectiveId);
        this.stakeholderObjectives.save(stakeholderObjective);
        return String.format(REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS, specification.getId(), stakeholder.getId(), stakeholderObjectiveId);
    }

    @PostMapping("/stakeholderObjective/{stakeholderObjectiveId}/delete")
    @Transactional
    public String processDeleteStakeholderObjective(
            @PathVariable("stakeholderObjectiveId") int stakeholderObjectiveId,
            @ModelAttribute(binding = false) Specification specification,
            @ModelAttribute(binding = false) Stakeholder stakeholder,
            Model model, BindingResult result, RedirectAttributes redirectAttributes) {

        Optional<StakeholderObjective> maybeStakeholderObjective = stakeholderObjectives.findById(stakeholderObjectiveId);
        //todo: validate stakeholderObjectiveId's name matches the passed in StakeholderObjective's name.

        if (maybeStakeholderObjective.isPresent()) {
            StakeholderObjective fetchedStakeholderObjective = maybeStakeholderObjective.get();
            stakeholderObjectives.delete(fetchedStakeholderObjective);
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", fetchedStakeholderObjective.getSpecificationObjective().getName()));
            return String.format(REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        } else {
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting stakeholder objective");
            return String.format(REDIRECT_STAKEHOLDER_DETAILS, specification.getId(), stakeholder.getId());
        }
    }

    private void populateFormModel(Model model) {
        model.addAttribute("priorities", priorities.findAll());
    }
}
