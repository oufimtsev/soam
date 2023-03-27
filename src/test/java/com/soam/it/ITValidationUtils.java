package com.soam.it;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ITValidationUtils {
    private ITValidationUtils() {
    }

    public static void validateSpecificationEdit(HtmlPage page, String name, String description, String notes) {
        validateSoamObjectEdit(page, name, description, notes);
    }

    public static void validateSpecificationObjectiveEdit(HtmlPage page, String name, String description, String notes) {
        validateSoamObjectEdit(page, name, description, notes);
    }

    public static void validateStakeholderEdit(HtmlPage page, String name, String description, String notes) {
        validateSoamObjectEdit(page, name, description, notes);
    }

    public static void validateStakeholderObjectiveEdit(
            HtmlPage page, String name, String description, String notes) {
        HtmlForm form = (HtmlForm) page.querySelectorAll("form").listIterator().next();

        assertEquals(name, form.getInputByName("specificationObjective.name").getValue());
        assertEquals(description, form.getInputByName("specificationObjective.description").getValue());
        assertEquals(notes, form.getTextAreaByName("notes").getText());
    }

    public static void validateSpecificationTemplateEdit(HtmlPage page, String name, String description, String notes) {
        validateSoamObjectEdit(page, name, description, notes);
    }

    public static void validateStakeholderTemplateEdit(HtmlPage page, String name, String description, String notes) {
        validateSoamObjectEdit(page, name, description, notes);
    }

    public static void validateObjectiveTemplateEdit(HtmlPage page, String name, String description, String notes) {
        validateSoamObjectEdit(page, name, description, notes);
    }

    public static void validateTemplateLink(HtmlPage page, int specificationTemplateId, String[][] templateLinkNames) throws IOException {
        HtmlForm form = page.getHtmlElementById("templateLinksForm");
        form.getSelectByName("filterSpecificationTemplate").setSelectedAttribute(String.valueOf(specificationTemplateId), true);

        DomElement button = page.createElement("button");
        button.setAttribute("type", "submit");
        form.appendChild(button);

        Map<String, String[]> templateLinkMap = Arrays.stream(templateLinkNames)
                .collect(Collectors.toMap(linkNames -> linkNames[0], Function.identity()));

        HtmlPage filteredPage = button.click();
        filteredPage.querySelectorAll("table tbody tr").forEach(row -> {
            String actualSpecificationTemplateName = row.querySelector("td:nth-of-type(2) a").getTextContent();
            String actualStakeholderTemplateName = row.querySelector("td:nth-of-type(3) a").getTextContent();
            String actualObjectiveTemplateName = row.querySelector("td:nth-of-type(4) a").getTextContent();
            String[] expectedTemplateLink = templateLinkMap.get(actualSpecificationTemplateName);
            assertEquals(expectedTemplateLink[0], actualSpecificationTemplateName);
            assertEquals(expectedTemplateLink[1], actualStakeholderTemplateName);
            assertEquals(expectedTemplateLink[2], actualObjectiveTemplateName);
        });
    }

    private static void validateSoamObjectEdit(HtmlPage page, String name, String description, String notes) {
        HtmlForm form = (HtmlForm) page.querySelectorAll("form").listIterator().next();

        assertEquals(name, form.getInputByName("name").getValue());
        assertEquals(description, form.getInputByName("description").getValue());
        assertEquals(notes, form.getTextAreaByName("notes").getText());
    }
}
