package com.soam.web.specificationobjective;

import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
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
    private final SpecificationRepository specificationRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;

    public SpecificationObjectiveController(
            SpecificationRepository specificationRepository,
            SpecificationObjectiveRepository specificationObjectiveRepository) {
        this.specificationRepository = specificationRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
    }

    @ModelAttribute(ModelConstants.ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        return specificationRepository.findById(specificationId).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("/specificationObjective/list")
    public String listAll(Specification specification, Model model) {
        model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVES, specification.getSpecificationObjectives());
        return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_LIST;
    }

    @GetMapping("/specificationObjective/{specificationObjectiveId}")
    public String showDetails(
            Specification specification, @PathVariable("specificationObjectiveId") int specificationObjectiveId, Model model) {
        return specificationObjectiveRepository.findById(specificationObjectiveId)
                .map(specificationObjective -> {
                    model.addAttribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, specificationObjective);
                    return ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_DETAILS;
                })
                .orElse(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, specification.getId()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String errorHandler(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, "Incorrect request parameters.");
        return RedirectConstants.REDIRECT_SPECIFICATION_LIST;
    }
}
