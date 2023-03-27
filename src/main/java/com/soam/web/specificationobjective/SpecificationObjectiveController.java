package com.soam.web.specificationobjective;

import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specificationobjective.SpecificationObjectiveService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/specification/{specificationId}")
public class SpecificationObjectiveController {
    private final SpecificationService specificationService;
    private final SpecificationObjectiveService specificationObjectiveService;

    public SpecificationObjectiveController(
            SpecificationService specificationService,
            SpecificationObjectiveService specificationObjectiveService) {
        this.specificationService = specificationService;
        this.specificationObjectiveService = specificationObjectiveService;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationService.getById(specificationId);
    }

    @GetMapping("/specificationObjective/list")
    public String listAll(Specification specification, Model model) {
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVES, specification.getSpecificationObjectives());
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_LIST;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }
}
