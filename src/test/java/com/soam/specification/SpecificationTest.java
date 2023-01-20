package com.soam.specification;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern REDIRECT_SPECIFICATION_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)$");
    private static final Pattern REDIRECT_STAKEHOLDER_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/stakeholder/(\\d+)$");
    private static final Pattern REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/specificationObjective/(\\d+)$");
    private static final Pattern REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/stakeholder/(\\d+)/stakeholderObjective/(\\d+)$");

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
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    @Test
    public void testCopySpecification() throws Exception {
        int srcSpecificationId = addSpecification("Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);

        int srcStakeholderId = addStakeholder(srcSpecificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");

        int srcSpecificationObjectiveId = addSpecificationObjective(srcSpecificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");

        int srcStakeholderObjectiveId = addStakeholderObjective(srcSpecificationId, srcStakeholderId,
                srcSpecificationObjectiveId, "Test Stakeholder Objective Notes");

        int dstSpecificationId = addSpecification("Copy of Test Specification",
                "Test Specification Description", "Test Specification Notes",
                "srcSpecification", srcSpecificationId);

        assertNotEquals(srcSpecificationId, dstSpecificationId);

        HtmlPage dstSpecificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, dstSpecificationId));
        //verify Specification details
        assertEquals("Copy of Test Specification",
                dstSpecificationDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(1) td b").getTextContent());
        assertEquals("Test Specification Description",
                dstSpecificationDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(2) td").getTextContent());
        assertEquals("Test Specification Notes",
                dstSpecificationDetailsPage.querySelector("table.table-striped:nth-of-type(1) tr:nth-of-type(3) td").getTextContent());

        //verify Stakeholders list
        assertEquals("Test Stakeholder",
                dstSpecificationDetailsPage.querySelector("#stakeholders tbody tr:nth-of-type(1) td a").getTextContent());

        //verify Specification Objectives list
        HtmlPage dstSpecificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, dstSpecificationId));
        assertEquals("Test Specification Objective",
                dstSpecificationObjectiveListPage.querySelector("#specificationObjectives tbody tr:nth-of-type(1) td a").getTextContent());

        //verify Stakeholder Objectives list
        HtmlAnchor dstStakeholderAnchor = dstSpecificationDetailsPage.querySelector("#stakeholders tbody tr:nth-of-type(1) td a");
        HtmlPage dstStakeholderDetailsPage = (HtmlPage) dstStakeholderAnchor.openLinkInNewWindow();
        assertEquals("Test Specification Objective",
                dstStakeholderDetailsPage.querySelector("#stakeholderObjectives tbody tr:nth-of-type(1) td a").getTextContent());
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
}
