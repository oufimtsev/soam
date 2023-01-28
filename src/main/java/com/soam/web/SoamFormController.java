package com.soam.web;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

public interface SoamFormController {

    @InitBinder
    public default void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    /**
     * Trim all incoming Strings
     * @param dataBinder
     */
    @InitBinder
    public default void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

}
