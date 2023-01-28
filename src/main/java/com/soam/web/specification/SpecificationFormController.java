package com.soam.web.specification;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class SpecificationFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final SpecificationRepository specificationRepository;
    private final StakeholderRepository stakeholderRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final StakeholderObjectiveRepository stakeholderObjectiveRepository;
    private final SpecificationTemplateRepository specificationTemplateRepository;
    private final PriorityRepository priorityRepository;

    public SpecificationFormController(
            SpecificationRepository specificationRepository, StakeholderRepository stakeholderRepository,
            SpecificationObjectiveRepository specificationObjectiveRepository,
            StakeholderObjectiveRepository stakeholderObjectiveRepository,
            SpecificationTemplateRepository specificationTemplateRepository, PriorityRepository priorityRepository) {
        this.specificationRepository = specificationRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.stakeholderObjectiveRepository = stakeholderObjectiveRepository;
        this.specificationTemplateRepository = specificationTemplateRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATIONS)
    public List<Specification> populateSpecifications() {
        return specificationRepository.findAll(NAME_CASE_INSENSITIVE_SORT);
    }

    @GetMapping("/specification/new")
    public String initCreationForm(Model model) {
        Specification specification = new Specification();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        this.populateFormModel( model );
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/new")
    public String processCreationForm(@Valid Specification specification, BindingResult result,
                                      @ModelAttribute("collectionType") String collectionType,
                                      @ModelAttribute("collectionItemId") int collectionItemId,
                                      Model model, RedirectAttributes redirectAttributes) {
        Optional<Specification> testSpecification = specificationRepository.findByNameIgnoreCase(specification.getName());
        if (testSpecification.isPresent()) {
            result.rejectValue("name", "unique", "Specification already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        if ("srcSpecification".equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification
            Optional<Specification> srcSpecification = specificationRepository.findById(collectionItemId);
            if (srcSpecification.isPresent()) {
                this.specificationRepository.save(specification);
                deepSpecificationCopy(srcSpecification.get(), specification);
                return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Source Specification does not exist");
                return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
            }
        } else if ("templateDeepCopy".equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification Template
            Optional<SpecificationTemplate> srcSpecificationTemplate = specificationTemplateRepository.findById(collectionItemId);
            if (srcSpecificationTemplate.isPresent()) {
                this.specificationRepository.save(specification);
                deepTemplateCopy(srcSpecificationTemplate.get(), specification);
                return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Source Specification Template does not exist");
                return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
            }
        } else {
            //creating new Specification manually or as a shall copy of existing Specification Template
            this.specificationRepository.save(specification);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }
    }

    @GetMapping("/specification/{specificationId}/edit")
    public String initUpdateSpecificationForm(@PathVariable("specificationId") int specificationId, Model model) {
        Optional<Specification> maybeSpecification = this.specificationRepository.findById(specificationId);
        if(maybeSpecification.isEmpty()){
            return RedirectConstants.REDIRECT_FIND_SPECIFICATION;
        }
        model.addAttribute(maybeSpecification.get());
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/{specificationId}/edit")
    public String processUpdateSpecificationForm(@Valid Specification specification, BindingResult result,
                                                 @PathVariable("specificationId") int specificationId, Model model) {

        Optional<Specification> testSpecification = specificationRepository.findByNameIgnoreCase(specification.getName());
        testSpecification.ifPresent(s-> {
            if( testSpecification.get().getId() != specificationId ){
                result.rejectValue("name", "unique", "Specification already exists");
            }
        });

        specification.setId( specificationId );
        if (result.hasErrors()) {
            model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification );
            this.populateFormModel( model );
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        this.specificationRepository.save(specification);
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
    }

    @PostMapping("/specification/{specificationId}/delete")
    public String processDeleteSpecification(
            @PathVariable("specificationId") int specificationId,
            Model model, RedirectAttributes redirectAttributes) {

        Optional<Specification> maybeSpecification = specificationRepository.findById(specificationId);
        //todo: validate specificationById's name matches the passed in Specification's name.

        if(maybeSpecification.isPresent()) {
            Specification specificationById = maybeSpecification.get();
            if((specificationById.getStakeholders() != null && !specificationById.getStakeholders().isEmpty()) ||
                (specificationById.getSpecificationObjectives() != null && !specificationById.getSpecificationObjectives().isEmpty())) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any stakeholders and specification objectives first.");
                return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
            }
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", specificationById.getName()));
            specificationRepository.delete(specificationById);
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting specification");
        }
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    private void deepSpecificationCopy(Specification srcSpecification, Specification dstSpecification) {
        srcSpecification.getSpecificationObjectives().forEach(srcSpecificationObjective -> {
            SpecificationObjective dstSpecificationObjective = new SpecificationObjective();
            dstSpecificationObjective.setName(srcSpecificationObjective.getName());
            dstSpecificationObjective.setDescription(srcSpecificationObjective.getDescription());
            dstSpecificationObjective.setNotes(srcSpecificationObjective.getNotes());
            dstSpecificationObjective.setPriority(srcSpecificationObjective.getPriority());
            dstSpecificationObjective.setSpecification(dstSpecification);
            specificationObjectiveRepository.save(dstSpecificationObjective);
        });
        srcSpecification.getStakeholders().forEach(srcStakeholder -> {
            Stakeholder dstStakeholder = new Stakeholder();
            dstStakeholder.setName(srcStakeholder.getName());
            dstStakeholder.setDescription(srcStakeholder.getDescription());
            dstStakeholder.setNotes(srcStakeholder.getNotes());
            dstStakeholder.setPriority(srcStakeholder.getPriority());
            dstStakeholder.setSpecification(dstSpecification);
            stakeholderRepository.save(dstStakeholder);

            srcStakeholder.getStakeholderObjectives().forEach(srcStakeholderObjective -> {
                Optional<SpecificationObjective> maybeDstSpecificationObjective = specificationObjectiveRepository
                        .findBySpecificationAndNameIgnoreCase(dstSpecification, srcStakeholderObjective.getSpecificationObjective().getName());
                if (maybeDstSpecificationObjective.isPresent()) {
                    StakeholderObjective dstStakeholderObjective = new StakeholderObjective();
                    dstStakeholderObjective.setStakeholder(dstStakeholder);
                    dstStakeholderObjective.setSpecificationObjective(maybeDstSpecificationObjective.get());
                    dstStakeholderObjective.setNotes(srcStakeholderObjective.getNotes());
                    dstStakeholderObjective.setPriority(srcStakeholderObjective.getPriority());
                    stakeholderObjectiveRepository.save(dstStakeholderObjective);
                }
            });
        });
    }

    private void deepTemplateCopy(SpecificationTemplate srcSpecificationTemplate, Specification dstSpecification) {
        Collection<TemplateLink> templateLinks = srcSpecificationTemplate.getTemplateLinks();

        //in the loop below we sacrifice the performance somewhat in favour of predictable memory consumption.
        templateLinks.forEach(templateLink -> {
            Stakeholder stakeholder = stakeholderRepository.findBySpecificationAndNameIgnoreCase(
                    dstSpecification,
                    templateLink.getStakeholderTemplate().getName()
            ).orElseGet(() -> {
                StakeholderTemplate stakeholderTemplate = templateLink.getStakeholderTemplate();
                Stakeholder newStakeholder = new Stakeholder();
                newStakeholder.setName(stakeholderTemplate.getName());
                newStakeholder.setDescription(stakeholderTemplate.getDescription());
                newStakeholder.setNotes(stakeholderTemplate.getNotes());
                newStakeholder.setPriority(stakeholderTemplate.getPriority());
                newStakeholder.setSpecification(dstSpecification);
                stakeholderRepository.save(newStakeholder);
                return newStakeholder;
            });

            SpecificationObjective specificationObjective = specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(
                    dstSpecification,
                    templateLink.getObjectiveTemplate().getName()
            ).orElseGet(() -> {
                ObjectiveTemplate objectiveTemplate = templateLink.getObjectiveTemplate();
                SpecificationObjective newSpecificationObjective = new SpecificationObjective();
                newSpecificationObjective.setName(objectiveTemplate.getName());
                newSpecificationObjective.setDescription(objectiveTemplate.getDescription());
                newSpecificationObjective.setNotes(objectiveTemplate.getNotes());
                newSpecificationObjective.setPriority(objectiveTemplate.getPriority());
                newSpecificationObjective.setSpecification(dstSpecification);
                specificationObjectiveRepository.save(newSpecificationObjective);
                return newSpecificationObjective;
            });

            StakeholderObjective stakeholderObjective = new StakeholderObjective();
            stakeholderObjective.setStakeholder(stakeholder);
            stakeholderObjective.setSpecificationObjective(specificationObjective);
            stakeholderObjective.setNotes(specificationObjective.getNotes());
            stakeholderObjective.setPriority(specificationObjective.getPriority());
            stakeholderObjectiveRepository.save(stakeholderObjective);
        });
    }

    private void populateFormModel( Model model ){
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
