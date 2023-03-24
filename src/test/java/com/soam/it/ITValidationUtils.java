package com.soam.it;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ITValidationUtils {
    private ITValidationUtils() {
    }

    public static void validateSpecificationDetails(HtmlPage page, String name, String description, String notes, List<String> stakeholderNames) throws IOException {
        //verify Specification details
        assertEquals(name,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals(description,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(2) td").getTextContent());
        assertEquals(notes,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());

        //verify Stakeholders list
        Set<String> expectedStakeholderNames = new HashSet<>(stakeholderNames);
        Set<String> actualStakeholderNames = page.querySelectorAll("#stakeholders tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedStakeholderNames, actualStakeholderNames);
    }

    public static void validateSpecificationObjectiveDetails(HtmlPage page, String name, String specificationName, String description, String notes) throws IOException {
        assertEquals(name,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals(specificationName,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(2) td").getTextContent());
        assertEquals(description,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());
        assertEquals(notes,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(4) td").getTextContent());
    }

    public static void validateSpecificationObjectiveList(HtmlPage page, List<String> specificationObjectiveNames) {
        Set<String> expectedSpecificationObjectiveNames = new HashSet<>(specificationObjectiveNames);
        Set<String> actualSpecificationObjectiveNames = page.querySelectorAll("#specificationObjectives tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedSpecificationObjectiveNames, actualSpecificationObjectiveNames);
    }

    public static void validateStakeholderDetails(HtmlPage page, String name, String description, String notes, List<String> stakeholderObjectiveNames) {
        //verify Stakeholder details
        assertEquals(name,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals(description,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());
        assertEquals(notes,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(4) td").getTextContent());

        Set<String> expectedStakeholderObjectiveNames = new HashSet<>(stakeholderObjectiveNames);
        Set<String> actualStakeholderObjectiveNames = page.querySelectorAll("#stakeholderObjectives tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedStakeholderObjectiveNames, actualStakeholderObjectiveNames);
    }

    public static void validateStakeholderObjectiveDetails(
            HtmlPage page, String name, String stakeholderName, String description, String notes) {
        //verify Stakeholder details
        assertEquals(name,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals(stakeholderName,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(2) td").getTextContent());
        assertEquals(description,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());
        assertEquals(notes,
                page.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(4) td").getTextContent());
    }

    public static void validateSpecificationTemplateEdit(HtmlPage page, String name, String description, String notes) {
        validateTemplateObjectEdit(page, name, description, notes);
    }

    public static void validateStakeholderTemplateEdit(HtmlPage page, String name, String description, String notes) {
        validateTemplateObjectEdit(page, name, description, notes);
    }

    public static void validateObjectiveTemplateEdit(HtmlPage page, String name, String description, String notes) {
        validateTemplateObjectEdit(page, name, description, notes);
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

    private static void validateTemplateObjectEdit(HtmlPage page, String name, String description, String notes) {
        HtmlForm form = (HtmlForm) page.querySelectorAll("form").listIterator().next();

        assertEquals(name, form.getInputByName("name").getValue());
        assertEquals(description, form.getInputByName("description").getValue());
        assertEquals(notes, form.getTextAreaByName("notes").getText());
    }
}
