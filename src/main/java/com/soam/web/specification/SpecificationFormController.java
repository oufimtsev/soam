package com.soam.web.specification;

import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
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

@Controller
public class SpecificationFormController implements SoamFormController {
    public static String CREATE_MODE_COPY_SPECIFICATION = "srcSpecification";
    public static String CREATE_MODE_FROM_TEMPLATE = "templateDeepCopy";

    private final SpecificationService specificationService;
    private final SpecificationTemplateService specificationTemplateService;
    private final PriorityService priorityService;

    public SpecificationFormController(
            SpecificationService specificationService,
            SpecificationTemplateService specificationTemplateService, PriorityService priorityService) {
        this.specificationService = specificationService;
        this.specificationTemplateService = specificationTemplateService;
        this.priorityService = priorityService;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATIONS)
    public List<Specification> populateSpecifications() {
        return specificationService.findAll();
    }

    @GetMapping("/specification/new")
    public String initCreationForm(Model model, @RequestParam(name = "collectionType", required = false) String collectionType) {
        Specification specification = new Specification();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        model.addAttribute(ModelConstants.ATTR_COLLECTION_TYPE, collectionType == null ? "" : collectionType);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @GetMapping("/specification2/new")
    public String initCreationForm2(Model model, @RequestParam(name = "collectionType", required = false) String collectionType) {
        Specification specification = new Specification();
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        model.addAttribute(ModelConstants.ATTR_COLLECTION_TYPE, collectionType == null ? "" : collectionType);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM2;
    }

    @PostMapping("/specification/new")
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
        } else if (CREATE_MODE_FROM_TEMPLATE.equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification Template
            SpecificationTemplate srcSpecificationTemplate = specificationTemplateService.getById(collectionItemId);
            specification = specificationService.saveFromTemplate(srcSpecificationTemplate, specification);
        } else {
            //creating new Specification manually or as a shall copy of existing Specification Template
            specification.setStakeholders(List.of());
            specification.setSpecificationObjectives(List.of());
            specification = specificationService.save(specification);
        }
        redirectAttributes.addFlashAttribute(
                SoamFormController.FLASH_SUCCESS,
                String.format("Created %s", getSpecificationOverviewMessage(specification))
        );
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_EDIT, specification.getId());
    }

    @PostMapping("/specification2/new")
    public String processCreationForm2(
            @Valid Specification specification, BindingResult result,
            @ModelAttribute("collectionType") String collectionType,
            @ModelAttribute("collectionItemId") int collectionItemId,
            Model model, RedirectAttributes redirectAttributes) {
        specificationService.findByName(specification.getName()).ifPresent(s ->
                result.rejectValue("name", "unique", "Specification already exists."));

        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM2;
        }

        if (CREATE_MODE_COPY_SPECIFICATION.equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification
            Specification srcSpecification = specificationService.getById(collectionItemId);
            specification = specificationService.saveDeepCopy(srcSpecification, specification);
        } else if (CREATE_MODE_FROM_TEMPLATE.equals(collectionType)) {
            //creating new Specification as a deep copy of source Specification Template
            SpecificationTemplate srcSpecificationTemplate = specificationTemplateService.getById(collectionItemId);
            specification = specificationService.saveFromTemplate(srcSpecificationTemplate, specification);
        } else {
            //creating new Specification manually or as a shall copy of existing Specification Template
            specification.setStakeholders(List.of());
            specification.setSpecificationObjectives(List.of());
            specification = specificationService.save(specification);
        }
        redirectAttributes.addFlashAttribute(
                SoamFormController.FLASH_SUCCESS,
                String.format("Created %s", getSpecificationOverviewMessage(specification))
        );
        return String.format(RedirectConstants.REDIRECT_TREE_SPECIFICATION_EDIT, specification.getId());
    }

    @GetMapping("/specification/{specificationId}/edit")
    public String initUpdateForm(
            @PathVariable("specificationId") int specificationId, Model model, RedirectAttributes redirectAttributes) {
        Specification specification = specificationService.getById(specificationId);
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @GetMapping("/specification2/{specificationId}/edit")
    public String initUpdateForm2(
            @PathVariable("specificationId") int specificationId, Model model, RedirectAttributes redirectAttributes) {
        Specification specification = specificationService.getById(specificationId);
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION, specification);
        populateFormModel(model);
        return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM2;
    }

    @PostMapping("/specification/{specificationId}/edit")
    public String processUpdateForm(
            @Valid Specification specification, BindingResult result,
            @PathVariable("specificationId") int specificationId, Model model, RedirectAttributes redirectAttributes) {
        specificationService.findByName(specification.getName())
                .filter(s -> s.getId() != specificationId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Specification already exists."));

        specification.setId(specificationId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        specificationService.save(specification);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Specification updated.");
        return String.format(RedirectConstants.REDIRECT_SPECIFICATION_EDIT, specificationId);
    }

    @PostMapping("/specification2/{specificationId}/edit")
    public String processUpdateForm2(
            @Valid Specification specification, BindingResult result,
            @PathVariable("specificationId") int specificationId, Model model, RedirectAttributes redirectAttributes) {
        specificationService.findByName(specification.getName())
                .filter(s -> s.getId() != specificationId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Specification already exists."));

        specification.setId(specificationId);
        if (result.hasErrors()) {
            populateFormModel(model);
            return ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM2;
        }

        specificationService.save(specification);
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, "Specification updated.");
        return String.format(RedirectConstants.REDIRECT_TREE_SPECIFICATION_EDIT, specification.getId());
    }

    @PostMapping("/specification/{specificationId}/delete")
    public String processDelete(@PathVariable("specificationId") int specificationId, RedirectAttributes redirectAttributes) {

//        if (specificationId != formId) {
//            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Malformed request.");
//            return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
//        }

        Specification specification = specificationService.getById(specificationId);
        if ((specification.getStakeholders() != null && !specification.getStakeholders().isEmpty()) ||
                (specification.getSpecificationObjectives() != null && !specification.getSpecificationObjectives().isEmpty())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Please delete any Stakeholders and Specification Objectives first.");
            return RedirectConstants.REDIRECT_TREE_DEFAULT1;
        }
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully deleted %s.", specification.getName()));
        specificationService.delete(specification);

        return RedirectConstants.REDIRECT_TREE_DEFAULT1;
    }

    @PostMapping("/specification2/{specificationId}/delete")
    public String processDelete2(
            @PathVariable("specificationId") int specificationId, RedirectAttributes redirectAttributes) {
        Specification specification = specificationService.getById(specificationId);
        if ((specification.getStakeholders() != null && !specification.getStakeholders().isEmpty()) ||
                (specification.getSpecificationObjectives() != null && !specification.getSpecificationObjectives().isEmpty())) {
            redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Please delete any Stakeholders and Specification Objectives first.");
            return RedirectConstants.REDIRECT_TREE_DEFAULT;
        }
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_SUCCESS, String.format("Successfully deleted %s.", specification.getName()));
        specificationService.delete(specification);

        return RedirectConstants.REDIRECT_TREE_DEFAULT;
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
        model.addAttribute(ModelConstants.ATTR_PRIORITIES, priorityService.findAll());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATES, specificationTemplateService.findAll());
    }
}
