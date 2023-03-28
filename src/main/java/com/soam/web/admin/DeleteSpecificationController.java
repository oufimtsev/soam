package com.soam.web.admin;

import com.soam.model.specification.Specification;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DeleteSpecificationController {
    private final SpecificationService specificationService;

    public DeleteSpecificationController(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @GetMapping("/admin/deleteSpecification/list")
    public String listAll(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Specification> specificationResults = specificationService.findAll(page - 1);
        addPaginationModel(page, model, specificationResults);

        return ViewConstants.VIEW_ADMIN_DELETE_SPECIFICATION;
    }

    @PostMapping("/admin/deleteSpecification/{specificationId}/delete")
    public String deleteCascade(
            @PathVariable("specificationId") int specificationId, RedirectAttributes redirectAttributes) {
        Specification specification = specificationService.getById(specificationId);
        specificationService.deleteCascade(specification);
        redirectAttributes.addFlashAttribute(
                SoamFormController.FLASH_SUCCESS,
                String.format("Specification %s deleted", specification.getName())
        );
        return RedirectConstants.REDIRECT_ADMIN_DELETE_SPECIFICATION;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_ADMIN_DELETE_SPECIFICATION;
    }

    private void addPaginationModel(int page, Model model, Page<Specification> paginated) {
        model.addAttribute(ModelConstants.ATTR_PAGINATED, paginated);
        List<Specification> specifications = paginated.getContent();
        model.addAttribute(ModelConstants.ATTR_CURRENT_PAGE, page);
        model.addAttribute(ModelConstants.ATTR_TOTAL_PAGES, paginated.getTotalPages());
        model.addAttribute(ModelConstants.ATTR_TOTAL_ITEMS, paginated.getTotalElements());
        model.addAttribute(ModelConstants.ATTR_SPECIFICATIONS, specifications);
    }
}
