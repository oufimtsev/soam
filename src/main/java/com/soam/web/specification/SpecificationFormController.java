package com.soam.web.specification;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.web.SoamFormController;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class SpecificationFormController extends SoamFormController {
    private static final String VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM = "specification/addUpdateSpecification";
    private static final String REDIRECT_SPECIFICATION_DETAILS = "redirect:/specification/";

    private final SpecificationRepository specifications;
    private final SpecificationTemplateRepository specificationTemplates;
    private final PriorityRepository priorities;

    public SpecificationFormController(SpecificationRepository specifications, SpecificationTemplateRepository specificationTemplates, PriorityRepository priorities) {
        this.specifications = specifications;
        this.specificationTemplates = specificationTemplates;
        this.priorities = priorities;
    }


    @GetMapping("/specification/new")
    public String initCreationForm(Model model) {
        Specification specification = new Specification();
        model.addAttribute("specification", specification);
        this.populateFormModel( model );
        return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/new")
    public String processCreationForm(@Valid Specification specification, BindingResult result, Model model) {
        Optional<Specification> testSpecification = specifications.findByNameIgnoreCase(specification.getName());
        if( testSpecification.isPresent() ){
            result.rejectValue("name", "unique", "Specification already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel(model);
            return VIEWS_SPECIFICATION_ADD_OR_UPDATE_FORM;
        }

        this.specifications.save(specification);
        return REDIRECT_SPECIFICATION_DETAILS + specification.getId();
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
            Model model, RedirectAttributes redirectAttributes ){

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

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("specificationTemplates", specificationTemplates.findAll());
    }
}
