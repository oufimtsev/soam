package com.soam.specification;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.soam.ITUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpecificationTest {
    private static final String URL_SPECIFICATION_DETAILS = "http://localhost/specification/%s";
    private static final String URL_SPECIFICATION_OBJECTIVE_LIST = "http://localhost/specification/%s/specificationObjective/list";

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

    @AfterEach
    public void tearDown() {
        webClient.close();
    }

    @Test
    public void testCopySpecification() throws Exception {
        //prepare test data
        int srcSpecificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int srcStakeholderId = ITUtils.addStakeholder(webClient, srcSpecificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");
        int srcSpecificationObjectiveId = ITUtils.addSpecificationObjective(webClient, srcSpecificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");
        int srcStakeholderObjectiveId = ITUtils.addStakeholderObjective(webClient, srcSpecificationId, srcStakeholderId,
                srcSpecificationObjectiveId, "Test Stakeholder Objective Notes");

        //execute "copy existing specification"
        int dstSpecificationId = ITUtils.addSpecification(webClient, "Copy of Test Specification",
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
        int specificationTemplateId = ITUtils.addSpecificationTemplate(webClient, "Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes");
        int stakeholderTemplate1Id = ITUtils.addStakeholderTemplate(webClient, "Test Stakeholder Template 1",
                "Test Stakeholder Template 1 Description", "Test Stakeholder Template 1 Notes");
        int stakeholderTemplate2Id = ITUtils.addStakeholderTemplate(webClient, "Test Stakeholder Template 2",
                "Test Stakeholder Template 2 Description", "Test Stakeholder Template 2 Notes");
        int objectiveTemplateId = ITUtils.addObjectiveTemplate(webClient, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");
        ITUtils.addTemplateLink(webClient, specificationTemplateId, stakeholderTemplate1Id, objectiveTemplateId);
        ITUtils.addTemplateLink(webClient, specificationTemplateId, stakeholderTemplate2Id, objectiveTemplateId);

        //execute "create from template"
        int specificationId = ITUtils.addSpecification(webClient, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes",
                "templateDeepCopy", specificationTemplateId);

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

    private static void validateSpecificationDetails(HtmlPage page, String name, String description, String notes, List<String> stakeholderNames) throws IOException {
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

    private static void validateSpecificationObjectiveList(HtmlPage page, List<String> specificationObjectiveNames) {
        Set<String> expectedSpecificationObjectiveNames = new HashSet<>(specificationObjectiveNames);
        Set<String> actualSpecificationObjectiveNames = page.querySelectorAll("#specificationObjectives tbody tr td a").stream()
                .map(DomNode::getTextContent)
                .collect(Collectors.toSet());
        assertEquals(expectedSpecificationObjectiveNames, actualSpecificationObjectiveNames);
    }

    private static void validateStakeholderDetails(HtmlPage page, String name, String description, String notes, List<String> stakeholderObjectiveNames) {
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
}
