package com.soam.web;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

public interface SoamFormController {
    //Flash constants that are used with RedirectAttributes to move flash messages
    //between requests.
    String FLASH_SUCCESS = "success";
    String FLASH_DANGER = "danger";
    String FLASH_SUB_MESSAGE = "subFlashMessage";

    @InitBinder
    default void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    /**
     * Trim all incoming Strings
     * @param dataBinder
     */
    @InitBinder
    default void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
}
