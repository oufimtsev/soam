package com.soam.web.specification;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.web.SoamFormController;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Optional;

@Controller
public class SpecificationTemplateFormController  extends SoamFormController {

    private static final String VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM = "specification/template/addUpdateSpecificationTemplate";
    private static final String REDIRECT_TEMPLATE_LIST = "redirect:/specification/template/list";

    private final SpecificationTemplateRepository specificationTemplates;
    private final TemplateLinkRepository templateLinkRepository;
    private final PriorityRepository priorities;

    public SpecificationTemplateFormController(
            SpecificationTemplateRepository specificationTemplates, TemplateLinkRepository templateLinkRepository,
            PriorityRepository priorities) {
        this.specificationTemplates = specificationTemplates;
        this.templateLinkRepository = templateLinkRepository;
        this.priorities = priorities;
    }

    @GetMapping("/specification/template/new")
    public String initCreationForm(Model model ) {

        SpecificationTemplate specificationTemplate = new SpecificationTemplate();
        model.addAttribute("specificationTemplate", specificationTemplate);
        this.populateFormModel( model );

        return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/new")
    public String processCreationForm(
            @Valid SpecificationTemplate specificationTemplate, BindingResult result,
            @ModelAttribute("collectionType") String collectionType,
            @ModelAttribute("collectionItemId") int collectionItemId,
            Model model, RedirectAttributes redirectAttributes) {

        Optional<SpecificationTemplate> testTemplate = specificationTemplates.findByNameIgnoreCase(specificationTemplate.getName());
        if (testTemplate.isPresent() ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            this.populateFormModel( model );
            return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
        }

        if ("templateDeepCopy".equals(collectionType)) {
            //creating new Specification Template as a deep copy of source Specification Template
            Optional<SpecificationTemplate> srcSpecificationTemplate = specificationTemplates.findById(collectionItemId);
            if (srcSpecificationTemplate.isPresent()) {
                this.specificationTemplates.save(specificationTemplate);
                deepTemplateCopy(srcSpecificationTemplate.get(), specificationTemplate);
            } else {
                redirectAttributes.addFlashAttribute(Util.DANGER, "Source Specification Template does not exist");
            }
        } else {
            this.specificationTemplates.save(specificationTemplate);
        }
        return REDIRECT_TEMPLATE_LIST;
    }

    @GetMapping("/specification/template/{specificationTemplateId}/edit")
    public String initUpdateSpecificationForm(@PathVariable("specificationTemplateId") int specificationId, Model model) {
        Optional<SpecificationTemplate> maybeSpecificationTemplate = this.specificationTemplates.findById(specificationId);
        if(maybeSpecificationTemplate.isEmpty()){
            //todo: pass error message
            return REDIRECT_TEMPLATE_LIST;
        }
        model.addAttribute(maybeSpecificationTemplate.get());
        this.populateFormModel( model );
        return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
    }

    @PostMapping("/specification/template/{specificationTemplateId}/edit")
    public String processUpdateSpecificationForm(@Valid SpecificationTemplate specificationTemplate, BindingResult result,
                                                 @PathVariable("specificationTemplateId") int specificationTemplateId, Model model) {

        Optional<SpecificationTemplate> testTemplate = specificationTemplates.findByNameIgnoreCase(specificationTemplate.getName());
        if( testTemplate.isPresent() && testTemplate.get().getId() != specificationTemplateId ){
            result.rejectValue("name", "unique", "Template already exists");
        }

        if (result.hasErrors()) {
            specificationTemplate.setId( specificationTemplateId );
            model.addAttribute("specificationTemplate", specificationTemplate );
            this.populateFormModel( model );
            return VIEWS_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM;
        }


        specificationTemplate.setId(specificationTemplateId);
        this.specificationTemplates.save(specificationTemplate);
        return REDIRECT_TEMPLATE_LIST;
    }

    @PostMapping("/specification/template/{specificationTemplateId}/delete")
    public String processDeleteSpecification(
            @PathVariable("specificationTemplateId") int specificationTemplateId, SpecificationTemplate specificationTemplate,
            RedirectAttributes redirectAttributes) {

        Optional<SpecificationTemplate> specificationTemplateById = specificationTemplates.findById(specificationTemplateId);
        //todo: validate specificationById's name matches the passed in Specification's name.

        if( specificationTemplateById.isPresent()) {
            if (specificationTemplateById.get().getTemplateLinks() != null && !specificationTemplateById.get().getTemplateLinks().isEmpty()) {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, "Please delete any template links first.");
            } else {
                redirectAttributes.addFlashAttribute(Util.SUB_FLASH, String.format("Successfully deleted %s", specificationTemplateById.get().getName()));
                specificationTemplates.delete(specificationTemplateById.get());
            }
        }else{
            redirectAttributes.addFlashAttribute(Util.DANGER, "Error deleting template");
        }

        return REDIRECT_TEMPLATE_LIST;

    }

    private void populateFormModel( Model model ){
        model.addAttribute("priorities", priorities.findAll());
        model.addAttribute("specificationTemplates", specificationTemplates.findAll());
    }

    private void deepTemplateCopy(SpecificationTemplate srcSpecificationTemplate, SpecificationTemplate dstSpecificationTemplate) {
        Collection<TemplateLink> templateLinks = srcSpecificationTemplate.getTemplateLinks();

        templateLinks.forEach(templateLink -> {
            TemplateLink newTemplateLink = new TemplateLink();
            newTemplateLink.setSpecificationTemplate(dstSpecificationTemplate);
            newTemplateLink.setStakeholderTemplate(templateLink.getStakeholderTemplate());
            newTemplateLink.setObjectiveTemplate(templateLink.getObjectiveTemplate());
            templateLinkRepository.save(newTemplateLink);
        });
    }
}
