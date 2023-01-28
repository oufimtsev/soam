package com.soam.web.specificationobjective;

import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/specification/{specificationId}")
public class SpecificationObjectiveController {
    private static final String ATTR_SPECIFICATION = "specification";
    private static final String ATTR_SPECIFICATION_OBJECTIVES = "specificationObjectives";

    private static final String VIEWS_SPECIFICATION_OBJECTIVE_LIST = "specificationObjective/specificationObjectiveList";
    private static final String VIEWS_SPECIFICATION_OBJECTIVE_DETAILS = "specificationObjective/specificationObjectiveDetails";
    private static final String REDIRECT_SPECIFICATION_DETAILS =  "redirect:/specification/%s";

    private final SpecificationRepository specificationRepository;
    private final SpecificationObjectiveRepository specificationObjectiveRepository;

    public SpecificationObjectiveController(SpecificationRepository specificationRepository, SpecificationObjectiveRepository specificationObjectiveRepository) {
        this.specificationRepository = specificationRepository;
        this.specificationObjectiveRepository = specificationObjectiveRepository;
    }

    @ModelAttribute(ATTR_SPECIFICATION)
    public Specification populateSpecification(@PathVariable("specificationId") int specificationId) {
        Optional<Specification> oSpecification = specificationRepository.findById(specificationId);
        return oSpecification.orElse(null);
    }

    @GetMapping("/specificationObjective/list")
    public String listSpecificationObjectives(Specification specification, Model model) {
        model.addAttribute(ATTR_SPECIFICATION_OBJECTIVES, specification.getSpecificationObjectives());
        return VIEWS_SPECIFICATION_OBJECTIVE_LIST;
    }

    @GetMapping("/specificationObjective/{specificationObjectiveId}")
    public String showSpecificationObjective(
            Specification specification,
            @PathVariable("specificationObjectiveId") int specificationObjectiveId, Model model) {
        Optional<SpecificationObjective> maybeSpecificationObjective = this.specificationObjectiveRepository.findById(specificationObjectiveId);
        if (maybeSpecificationObjective.isEmpty()) {
            return String.format(REDIRECT_SPECIFICATION_DETAILS, specification.getId());
        }
        model.addAttribute(maybeSpecificationObjective.get());
        return VIEWS_SPECIFICATION_OBJECTIVE_DETAILS;
    }
}
