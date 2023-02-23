package com.soam.web.admin;

import com.soam.model.soamenum.SoamEnum;
import com.soam.service.soamenum.SoamEnumService;
import com.soam.web.ModelConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.Collection;

@Controller
public class SoamEnumController implements SoamFormController {
    private final SoamEnumService soamEnumService;

    public SoamEnumController(SoamEnumService soamEnumService, ConfigurableConversionService conversionService) {
        this.soamEnumService = soamEnumService;
        conversionService.addConverter(
                String.class, SoamEnumType.class, source -> {
                    int id = Integer.parseInt(source);
                    return id == -1 ? null : SoamEnumType.values()[id];
                });
        conversionService.addConverter(
                SoamEnumType.class, String.class, source -> String.valueOf(source.getId()));
    }

    @ModelAttribute(ModelConstants.ATTR_ADMIN_SOAM_ENUM_TYPES)
    public Collection<SoamEnumType> populateSoamEnumTypes() {
        return Arrays.asList(SoamEnumType.values());
    }

    @ModelAttribute(ModelConstants.ATTR_ADMIN_SOAM_ENUMS)
    public Collection<SoamEnum> populateSoamEnums(
            @ModelAttribute(ModelConstants.ATTR_ADMIN_SOAM_ENUM_FORM) SoamEnumFormDto soamEnumFormDto) {
        if (soamEnumFormDto.getFilterSoamEnumType() != null) {
            return soamEnumService.findByType(soamEnumFormDto.getFilterSoamEnumType().getEnumClass());
        } else {
            return soamEnumService.findAll();
        }
    }

    @RequestMapping(value = "/admin/soamEnum/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String listAll() {
        return ViewConstants.VIEW_ADMIN_SOAM_EMUM_LIST;
    }
}
