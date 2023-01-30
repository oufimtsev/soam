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
import com.soam.web.SoamFormController;
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
public class SpecificationFormController extends SoamFormController {
    private static final String VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM = "specification/addUpdateSpecification";
    private static final String REDIRECT_SPECIFICATION_DETAILS = "redirect:/specification/";

    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    private final SpecificationRepository specifications;
    private final StakeholderRepository stakeholderRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;
    private final StakeholderObjectiveRepository stakeholderObjectiveRepository;
    private final SpecificationTemplateRepository specificationTemplates;
    private final PriorityRepository priorities;

    public SpecificationFormController(
            SpecificationRepository specifications, StakeholderRepository stakeholderRepository,
            SpecificationObjectiveRepository specificationObjectiveRepository,
            StakeholderObjectiveRepository stakeholderObjectiveRepository,
            SpecificationTemplateRepository specificationTemplates, PriorityRepository priorities) {
        this.specifications = specifications;
        this.stakeholderRepository = stakeholderRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
        this.stakeholderObjectiveRepository = stakeholderObjectiveRepository;
        this.specificationTemplates = specificationTemplates;
        this.priorities = priorities;
    }

    @ModelAttribute("specifications")
    public List<Specification> populateSpecifications() {
        return specifications.findAll(NAME_CASE_INSENSITIVE_SORT);
    }

    @GetMapping("/specification/new")
    public String initCreationForm(Model model) {
        Specification specification = new Specification();
        model.addAttribute("specification", specification);
        this.populateFormModel( model );
        return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/new")
    public String processCreationForm(@Valid Specification specification, BindingResult result,
                                      @ModelAttribute("collectionType") String collectionType,
                                      @ModelAttribute("collectionItemId") int collectionItemId,
                                      Model model, RedirectAttributes redirectAttributes) {
        Optional<Specification> testSpecification = specifications.findByNameIgnoreCase(specification.getName());
        if (testSpecification.isPresent()) {
            result.rejectValue("name", "unique", "Specification already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        if ("srcSpecification".equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification
            Optional<Specification> srcSpecification = specifications.findById(collectionItemId);
            if (srcSpecification.isPresent()) {
                this.specifications.save(specification);
                deepSpecificationCopy(srcSpecification.get(), specification);
                return REDIRECT_SPECIFICATION_DETAILS + specification.getId();
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Source Specification does not exist");
                return "redirect:/specification/list";
            }
        } else if ("templateDeepCopy".equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification Template
            Optional<SpecificationTemplate> srcSpecificationTemplate = specificationTemplates.findById(collectionItemId);
            if (srcSpecificationTemplate.isPresent()) {
                this.specifications.save(specification);
                deepTemplateCopy(srcSpecificationTemplate.get(), specification);
                return REDIRECT_SPECIFICATION_DETAILS + specification.getId();
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Source Specification Template does not exist");
                return "redirect:/specification/list";
            }

        } else {
            //creating new Specification manually or as a shall copy of existing Specification Template
            this.specifications.save(specification);
            return REDIRECT_SPECIFICATION_DETAILS + specification.getId();
        }
    }

    @GetMapping("/specification/{specificationId}/edit")
    public String initUpdateSpecificationForm(@PathVariable("specificationId") int specificationId, Model model) {
        Optional<Specification> maybeSpecification = this.specifications.findById(specificationId);
        if(maybeSpecification.isEmpty()){
            return "redirect:/specification/find";
        }
        model.addAttribute(maybeSpecification.get());
        populateFormModel(model);
        return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }



    @PostMapping("/specification/{specificationId}/edit")
    public String processUpdateSpecificationForm(@Valid Specification specification, BindingResult result,
                                                 @PathVariable("specificationId") int specificationId, Model model) {

        Optional<Specification> testSpecification = specifications.findByNameIgnoreCase(specification.getName());
        testSpecification.ifPresent(s-> {
            if( testSpecification.get().getId() != specificationId ){
                result.rejectValue("name", "unique", "Specification already exists");
            }
        });

        specification.setId( specificationId );
        if (result.hasErrors()) {

            model.addAttribute("specification", specification );
            this.populateFormModel( model );
            return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        this.specifications.save(specification);
        return "redirect:/specification/{specificationId}";
    }

    @PostMapping("/specification/{specificationId}/delete")
    public String processDeleteSpecification(
            @PathVariable("specificationId") int specificationId,
            Model model, RedirectAttributes redirectAttributes) {

        Optional<Specification> maybeSpecification = specifications.findById(specificationId);
        //todo: validate specificationById's name matches the passed in Specification's name.

        if(maybeSpecification.isPresent()) {
            Specification specificationById = maybeSpecification.get();
            if((specificationById.getStakeholders() != null && !specificationById.getStakeholders().isEmpty()) ||
                (specificationById.getSpecificationObjectives() != null && !specificationById.getSpecificationObjectives().isEmpty())) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any stakeholders and specification objectives first.");
                return REDIRECT_SPECIFICATION_DETAILS+ specificationId;
            }
            redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", specificationById.getName()));
            specifications.delete(specificationById);
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting specification");
        }
        return "redirect:/specification/list";

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
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("specificationTemplates", specificationTemplates.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
