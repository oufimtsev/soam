package com.soam.web.specification;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class SpecificationFormController implements SoamFormController {
    private static final Sort NAME_CASE_INSENSITIVE_SORT = Sort.by(Sort.Order.by("name").ignoreCase());

    public static String CREATE_MODE_COPY_SPECIFICATION = "srcSpecification";
    public static String CREATE_MODE_FROM_TEMPLATE = "templateDeepCopy";

    private final SpecificationService specificationService;
    private final SpecificationTemplateRepository specificationTemplateRepository;
    private final PriorityRepository priorityRepository;

    public SpecificationFormController(
            SpecificationService specificationService,
            SpecificationTemplateRepository specificationTemplateRepository, PriorityRepository priorityRepository) {
        this.specificationService = specificationService;
        this.specificationTemplateRepository = specificationTemplateRepository;
        this.priorityRepository = priorityRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATIONS)
    public List<Specification> populateSpecifications() {
        return specificationService.findAll();
    }

    @GetMapping("/specification/new")
    public String initCreationForm(Model model) {
        Specification specification = new Specification();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/new")
    @Transactional
    public String processCreationForm(
            @Valid Specification specification, BindingResult result,
            @ModelAttribute("collectionType") String collectionType,
            @ModelAttribute("collectionItemId") int collectionItemId,
            Model model, RedirectAttributes redirectAttributes) {
        specificationService.findByName(specification.getName()).ifPresent(s ->
                result.rejectValue("name", "unique", "Specification already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        if (CREATE_MODE_COPY_SPECIFICATION.equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification
            Specification srcSpecification = specificationService.getById(collectionItemId);
            specification = specificationService.saveDeepCopy(srcSpecification, specification);
            redirectAttributes.addFlashAttribute(
                    SoamFormController.FLASH_SUCCESS,
                    String.format("Copied %s", getSpecificationOverviewMessage(specification))
            );
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        } else if (CREATE_MODE_FROM_TEMPLATE.equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification Template
            Optional<SpecificationTemplate> maybeSrcSpecificationTemplate = specificationTemplateRepository.findById(collectionItemId);
            if (maybeSrcSpecificationTemplate.isPresent()) {
                specification = specificationService.saveFromTemplate(maybeSrcSpecificationTemplate.get(), specification);
                redirectAttributes.addFlashAttribute(
                        SoamFormController.FLASH_SUCCESS,
                        String.format("Created %s", getSpecificationOverviewMessage(specification))
                );
                return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
            } else {
                redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Source Specification Template does not exist.");
                return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
            }
        } else {
            //creating new Specification manually or as a shall copy of existing Specification Template
            specification = specificationService.save(specification);
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }
    }

    @GetMapping("/specification/{specificationId}/edit")
    public String initUpdateForm(
            @PathVariable("specificationId") int specificationId, Model model, RedirectAttributes redirectAttributes) {
        Specification specification = specificationService.getById(specificationId);
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/{specificationId}/edit")
    public String processUpdateForm(
            @Valid Specification specification, BindingResult result,
            @PathVariable("specificationId") int specificationId, Model model) {
        specificationService.findByName(specification.getName())
                .filter(s -> s.getId() != specificationId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Specification already exists."));

        specification.setId(specificationId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        specificationService.save(specification);
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
    }

    @PostMapping("/specification/{specificationId}/delete")
    public String processDelete(
            @PathVariable("specificationId") int specificationId, @RequestParam("id") int formId,
            RedirectAttributes redirectAttributes) {
        if (specificationId != formId) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
            return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
        }

        Specification specification = specificationService.getById(specificationId);
        if ((specification.getStakeholders() != null && !specification.getStakeholders().isEmpty()) ||
                (specification.getSpecificationObjectives() != null && !specification.getSpecificationObjectives().isEmpty())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, "Please delete any Stakeholders and Specification Objectives first.");
            return String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specificationId);
        }
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUB_MESSAGE, String.format("Successfully deleted %s.", specification.getName()));
        specificationService.delete(specification);

        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }

    private String getSpecificationOverviewMessage(Specification specification) {
        int stakeholderObjectiveCount = specification.getStakeholders().stream()
                .mapToInt(stakeholder -> stakeholder.getStakeholderObjectives().size())
                .sum();
        return String.format(
                        "Specification %s with %d Stakeholder(s), %d Specification Objective(s) and total of %d Stakeholder Objective(s)",
                        specification.getName(),
                        specification.getStakeholders().size(),
                        specification.getSpecificationObjectives().size(),
                        stakeholderObjectiveCount
        );
    }

    private void populateFormModel(Model model) {
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityRepository.findAll());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplateRepository.findAll(NAME_CASE_INSENSITIVE_SORT));
    }
}
