package com.soam.specification;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SpecificationTest {
    private static final String URL_NEW_SPECIFICATION = "http://localhost/specification/new";
    private static final String URL_SPECIFICATION_DETAILS = "http://localhost/specification/%s";
    private static final String URL_NEW_STAKEHOLDER = "http://localhost/specification/%s/stakeholder/new";
    private static final String URL_NEW_SPECIFICATION_OBJECTIVE = "http://localhost/specification/%s/specificationObjective/new";
    private static final String URL_SPECIFICATION_OBJECTIVE_LIST = "http://localhost/specification/%s/specificationObjective/list";
    private static final String URL_NEW_STAKEHOLDER_OBJECTIVE = "http://localhost/specification/%s/stakeholder/%s/stakeholderObjective/new";
    private static final String URL_NEW_SPECIFICATION_TEMPLATE = "http://localhost/specification/template/new";
    private static final String URL_NEW_STAKEHOLDER_TEMPLATE = "http://localhost/stakeholder/template/new";
    private static final String URL_NEW_OBJECTIVE_TEMPLATE = "http://localhost/objective/template/new";
    private static final String URL_TEMPLATE_LINK_LIST = "http://localhost/templateLink/list";

    private static final Pattern REDIRECT_SPECIFICATION_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)$");
    private static final Pattern REDIRECT_STAKEHOLDER_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/stakeholder/(\\d+)$");
    private static final Pattern REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/specificationObjective/(\\d+)$");
    private static final Pattern REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/stakeholder/(\\d+)/stakeholderObjective/(\\d+)$");
    private static final Pattern REDIRECT_SPECIFICATION_TEMPLATE_EDIT = Pattern.compile("^http://localhost/specification/template/(\\d+)/edit$");
    private static final Pattern REDIRECT_STAKEHOLDER_TEMPLATE_EDIT = Pattern.compile("^http://localhost/stakeholder/template/(\\d+)/edit$");
    private static final Pattern REDIRECT_OBJECTIVE_TEMPLATE_EDIT = Pattern.compile("^http://localhost/objective/template/(\\d+)/edit$");

    @Autowired
    private MockMvc mockMvc;

    private WebClient webClient;

    @BeforeEach
    public void setup(WebApplicationContext context) {
        webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context)
                .build();
        //HtmlUnit is unable to execute Bootstrap JavaScript. We don't need JS processing for simple HTML-based IT,
        //so can safely disable JS processing in HtmlUnit
        //https://github.com/HtmlUnit/htmlunit/issues/232
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    @Test
    public void testCopySpecification() throws Exception {
        //prepare test data
        int srcSpecificationId = addSpecification("Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int srcStakeholderId = addStakeholder(srcSpecificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");
        int srcSpecificationObjectiveId = addSpecificationObjective(srcSpecificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");
        int srcStakeholderObjectiveId = addStakeholderObjective(srcSpecificationId, srcStakeholderId,
                srcSpecificationObjectiveId, "Test Stakeholder Objective Notes");

        //execute "copy existing specification"
        int dstSpecificationId = addSpecification("Copy of Test Specification",
                "Test Specification Description", "Test Specification Notes",
                "srcSpecification", srcSpecificationId);

        assertNotEquals(srcSpecificationId, dstSpecificationId);

        HtmlPage dstSpecificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, dstSpecificationId));
        validateSpecificationDetails(dstSpecificationDetailsPage, "Copy of Test Specification",
                "Test Specification Description", "Test Specification Notes", List.of("Test Stakeholder"));

        //verify Specification Objectives list
        HtmlPage dstSpecificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, dstSpecificationId));
        validateSpecificationObjectiveList(dstSpecificationObjectiveListPage, List.of("Test Specification Objective"));

        //verify Stakeholder Objectives list
        HtmlAnchor dstStakeholderAnchor = dstSpecificationDetailsPage.querySelector("#stakeholders tbody tr:nth-of-type(1) td a");
        HtmlPage dstStakeholderDetailsPage = (HtmlPage) dstStakeholderAnchor.openLinkInNewWindow();
        validateStakeholderDetails(dstStakeholderDetailsPage, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes", List.of("Test Specification Objective"));
    }

    @Test
    public void testCreateFromTemplate() throws IOException {
        //prepare test data
        int specificationTemplateId = addSpecificationTemplate("Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes");
        int stakeholderTemplate1Id = addStakeholderTemplate("Test Stakeholder Template 1",
                "Test Stakeholder Template 1 Description", "Test Stakeholder Template 1 Notes");
        int stakeholderTemplate2Id = addStakeholderTemplate("Test Stakeholder Template 2",
                "Test Stakeholder Template 2 Description", "Test Stakeholder Template 2 Notes");
        int objectiveTemplateId = addObjectiveTemplate("Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");
        addTemplateLink(specificationTemplateId, stakeholderTemplate1Id, objectiveTemplateId);
        addTemplateLink(specificationTemplateId, stakeholderTemplate2Id, objectiveTemplateId);

        //execute "create from template"
        int specificationId = addSpecification("Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes",
                "srcTemplate", specificationTemplateId);

        HtmlPage specificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, specificationId));
        validateSpecificationDetails(specificationDetailsPage, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes",
                List.of("Test Stakeholder Template 1", "Test Stakeholder Template 2"));

        //verify Specification Objectives list
        HtmlPage specificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, specificationId));
        validateSpecificationObjectiveList(specificationObjectiveListPage, List.of("Test Objective Template"));

        //verify Stakeholder Objectives list
        HtmlAnchor stakeholderAnchor = specificationDetailsPage.querySelector("#stakeholders tbody tr:nth-of-type(1) td a");
        HtmlPage stakeholderDetailsPage = (HtmlPage) stakeholderAnchor.openLinkInNewWindow();
        validateStakeholderDetails(stakeholderDetailsPage, "Test Stakeholder Template 1",
                "Test Stakeholder Template 1 Description", "Test Stakeholder Template 1 Notes",
                List.of("Test Objective Template"));
    }

    private void validateSpecificationDetails(HtmlPage specificationDetailsPage, String name, String description, String notes, List<String> stakeholderNames) throws IOException {
        //verify Specification details
        assertEquals(name,
                specificationDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals(description,
                specificationDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(2) td").getTextContent());
        assertEquals(notes,
                specificationDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());

        //verify Stakeholders list
        Set<String> expectedStakeholderNames = new HashSet<>(stakeholderNames);
        Set<String> actualStakeholderNames = specificationDetailsPage.querySelectorAll("#stakeholders tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedStakeholderNames, actualStakeholderNames);
    }

    private void validateSpecificationObjectiveList(HtmlPage specificationObjectiveListPage, List<String> specificationObjectiveNames) {
        Set<String> expectedSpecificationObjectiveNames = new HashSet<>(specificationObjectiveNames);
        Set<String> actualSpecificationObjectiveNames = specificationObjectiveListPage.querySelectorAll("#specificationObjectives tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedSpecificationObjectiveNames, actualSpecificationObjectiveNames);
    }

    private void validateStakeholderDetails(HtmlPage stakeholderDetailsPage, String name, String description, String notes, List<String> stakeholderObjectiveNames) {
        //verify Stakeholder details
        assertEquals(name,
                stakeholderDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals(description,
                stakeholderDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());
        assertEquals(notes,
                stakeholderDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(4) td").getTextContent());

        Set<String> expectedStakeholderObjectiveNames = new HashSet<>(stakeholderObjectiveNames);
        Set<String> actualStakeholderObjectiveNames = stakeholderDetailsPage.querySelectorAll("#stakeholderObjectives tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedStakeholderObjectiveNames, actualStakeholderObjectiveNames);
    }

    private int addSpecification(String name, String description, String notes, String collectionType, int collectionItemId) throws IOException {
        HtmlPage page = webClient.getPage(URL_NEW_SPECIFICATION);
        HtmlForm form = page.getHtmlElementById("add-specification-form");
        form.getInputByName("name").setValue(name);
        form.getInputByName("description").setValue(description);
        form.getTextAreaByName("notes").setText(notes);
        form.getInputByName("collectionType").setValue(collectionType);
        form.getInputByName("collectionItemId").setValue(String.valueOf(collectionItemId));
        HtmlPage detailsPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        Matcher redirectMatcher = REDIRECT_SPECIFICATION_DETAILS.matcher(detailsPage.getUrl().toString());
        assertTrue(redirectMatcher.matches());
        return Integer.parseInt(redirectMatcher.group(1));
    }

    private int addStakeholder(int specificationId, String name, String description, String notes) throws IOException {
        HtmlPage page = webClient.getPage(String.format(URL_NEW_STAKEHOLDER, specificationId));
        HtmlForm form = page.getHtmlElementById("add-stakeholder-form");
        form.getInputByName("name").setValue(name);
        form.getInputByName("description").setValue(description);
        form.getTextAreaByName("notes").setText(notes);
        HtmlPage detailsPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        Matcher redirectMatcher = REDIRECT_STAKEHOLDER_DETAILS.matcher(detailsPage.getUrl().toString());
        assertTrue(redirectMatcher.matches());
        assertEquals(specificationId, Integer.parseInt(redirectMatcher.group(1)));
        return Integer.parseInt(redirectMatcher.group(2));
    }

    private int addSpecificationObjective(int specificationId, String name, String description, String notes) throws IOException {
        HtmlPage page = webClient.getPage(String.format(URL_NEW_SPECIFICATION_OBJECTIVE, specificationId));
        HtmlForm form = page.getHtmlElementById("add-specification-objective-form");
        form.getInputByName("name").setValue(name);
        form.getInputByName("description").setValue(description);
        form.getTextAreaByName("notes").setText(notes);
        HtmlPage detailsPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        Matcher redirectMatcher = REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS.matcher(detailsPage.getUrl().toString());
        assertTrue(redirectMatcher.matches());
        assertEquals(specificationId, Integer.parseInt(redirectMatcher.group(1)));
        return Integer.parseInt(redirectMatcher.group(2));
    }

    private int addStakeholderObjective(int specificationId, int stakeholderId, int specificationObjectiveId,
                                        String notes) throws IOException {
        HtmlPage page = webClient.getPage(String.format(URL_NEW_STAKEHOLDER_OBJECTIVE, specificationId, stakeholderId));
        HtmlForm form = page.getHtmlElementById("add-stakeholder-objective-form");
        form.getInputByName("stakeholder").setValue(String.valueOf(stakeholderId));
        form.getInputByName("collectionItemId").setValue(String.valueOf(specificationObjectiveId));
        form.getTextAreaByName("notes").setText(notes);
        HtmlPage detailsPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        Matcher redirectMatcher = REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS.matcher(detailsPage.getUrl().toString());
        assertTrue(redirectMatcher.matches());
        assertEquals(specificationId, Integer.parseInt(redirectMatcher.group(1)));
        assertEquals(stakeholderId, Integer.parseInt(redirectMatcher.group(2)));
        return Integer.parseInt(redirectMatcher.group(3));
    }

    private int addTemplateObject(String addPageUrl, Pattern editPage, String name, String description, String notes) throws IOException {
        HtmlPage page = webClient.getPage(addPageUrl);
        HtmlForm form = page.querySelector("form");
        form.getInputByName("name").setValue(name);
        form.getInputByName("description").setValue(description);
        form.getTextAreaByName("notes").setText(notes);
        HtmlPage listingPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        HtmlAnchor a = (HtmlAnchor) listingPage.querySelectorAll("table tbody tr td:nth-of-type(1) a").stream()
                .filter(node -> name.equals(node.getTextContent()))
                .findFirst()
                .orElseThrow();
        URL detailsPageUrl = listingPage.getFullyQualifiedUrl(a.getHrefAttribute());
        Matcher redirectMatcher = editPage.matcher(detailsPageUrl.toString());
        assertTrue(redirectMatcher.matches());
        return Integer.parseInt(redirectMatcher.group(1));
    }

    private int addSpecificationTemplate(String name, String description, String notes) throws IOException {
        return addTemplateObject(URL_NEW_SPECIFICATION_TEMPLATE, REDIRECT_SPECIFICATION_TEMPLATE_EDIT, name, description, notes);
    }

    private int addStakeholderTemplate(String name, String description, String notes) throws IOException {
        return addTemplateObject(URL_NEW_STAKEHOLDER_TEMPLATE, REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, name, description, notes);
    }

    private int addObjectiveTemplate(String name, String description, String notes) throws IOException {
        return addTemplateObject(URL_NEW_OBJECTIVE_TEMPLATE, REDIRECT_OBJECTIVE_TEMPLATE_EDIT, name, description, notes);
    }

    private void addTemplateLink(int specificationTemplateId, int stakeholderTemplateId, int objectiveTemplateId) throws IOException {
        HtmlPage page = webClient.getPage(URL_TEMPLATE_LINK_LIST);
        HtmlForm form = page.getHtmlElementById("templateLinksForm");
        form.getSelectByName("newTemplateLink.specificationTemplate").setSelectedAttribute(String.valueOf(specificationTemplateId), true);
        form.getSelectByName("newTemplateLink.stakeholderTemplate").setSelectedAttribute(String.valueOf(stakeholderTemplateId), true);
        form.getSelectByName("newTemplateLink.objectiveTemplate").setSelectedAttribute(String.valueOf(objectiveTemplateId), true);

        HtmlButton addButton = form.querySelector("form tfoot button");
        addButton.click();
    }
}
