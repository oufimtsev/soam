package com.soam.web.admin;

import com.soam.model.soamenum.SoamEnum;
import com.soam.service.EntityNotFoundException;
import com.soam.service.soamenum.SoamEnumService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SoamEnumFormController {
    private final SoamEnumService soamEnumService;

    public SoamEnumFormController(SoamEnumService soamEnumService) {
        this.soamEnumService = soamEnumService;
    }

    @ModelAttribute(ModelConstants.ATTR_ADMIN_SOAM_ENUM)
    public SoamEnum populateSoamEnum(@PathVariable("soamEnumId") int soamEnumId) {
        return soamEnumService.getById(soamEnumId);
    }

    @GetMapping("/admin/soamEnum/{soamEnumId}/edit")
    public String initUpdateForm() {
        return ViewConstants.VIEW_ADMIN_SOAM_EMUM_UPDATE_FORM;
    }

    @PostMapping("/admin/soamEnum/{soamEnumId}/edit")
    public String processUpdateForm(
            @Valid SoamEnum soamEnum, BindingResult result, @PathVariable("soamEnumId") int soamEnumId) {
        soamEnumService.findBySoamEnumIdAndName(soamEnum.getClass(), soamEnum.getName())
                .filter(s -> s.getId() != soamEnumId)
                .ifPresent(s -> result.rejectValue("name", "unique", "Enum name already exists."));
        soamEnumService.findBySoamEnumIdAndSequence(soamEnum.getClass(), soamEnum.getSequence())
                .filter(s -> s.getId() != soamEnumId)
                .ifPresent(s -> result.rejectValue("sequence", "unique", "Enum sequence already exists."));

        soamEnum.setId(soamEnumId);
        if (result.hasErrors()) {
            return ViewConstants.VIEW_ADMIN_SOAM_EMUM_UPDATE_FORM;
        }

        soamEnumService.save(soamEnum);
        return RedirectConstants.REDIRECT_ADMIN_SOAM_EMUM_LIST;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String errorHandler(EntityNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SoamFormController.FLASH_DANGER, e.getMessage());
        return RedirectConstants.REDIRECT_ADMIN_SOAM_EMUM_LIST;
    }
}
