package com.soam.it.specification;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.soam.it.ITUtils;
import com.soam.it.ITValidationUtils;
import com.soam.web.specification.SpecificationFormController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SpecificationTest {
    private static final String URL_SPECIFICATION_DETAILS = "http://localhost/specification/%s";
    private static final String URL_SPECIFICATION_OBJECTIVE_LIST = "http://localhost/specification/%s/specificationObjective/list";

    private WebClient webClient;

    @BeforeEach
    void setup(WebApplicationContext context) {
        webClient = ITUtils.prepareWebClient(context);
    }

    @AfterEach
    void tearDown() {
        webClient.close();
    }

    @Test
    void testCopySpecification() throws Exception {
        //prepare test data
        int srcSpecificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int srcStakeholderId = ITUtils.addStakeholder(webClient, srcSpecificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");
        int srcSpecificationObjectiveId = ITUtils.addSpecificationObjective(webClient, srcSpecificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");
        ITUtils.addStakeholderObjective(webClient, srcSpecificationId, srcStakeholderId,
                srcSpecificationObjectiveId, "Test Stakeholder Objective Notes");

        //execute "copy existing specification"
        int dstSpecificationId = ITUtils.addSpecification(webClient, "Copy of Test Specification",
                "Test Specification Description", "Test Specification Notes",
                SpecificationFormController.CREATE_MODE_COPY_SPECIFICATION, srcSpecificationId);

        assertNotEquals(srcSpecificationId, dstSpecificationId);

        HtmlPage dstSpecificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, dstSpecificationId));
        ITValidationUtils.validateSpecificationDetails(dstSpecificationDetailsPage, "Copy of Test Specification",
                "Test Specification Description", "Test Specification Notes", List.of("Test Stakeholder"));

        //verify Specification Objectives list
        HtmlPage dstSpecificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, dstSpecificationId));
        ITValidationUtils.validateSpecificationObjectiveList(dstSpecificationObjectiveListPage, List.of("Test Specification Objective"));

        //verify Stakeholder Objectives list
        HtmlAnchor dstStakeholderAnchor = dstSpecificationDetailsPage.querySelector("#stakeholders tbody tr:nth-of-type(1) td a");
        HtmlPage dstStakeholderDetailsPage = (HtmlPage) dstStakeholderAnchor.openLinkInNewWindow();
        ITValidationUtils.validateStakeholderDetails(dstStakeholderDetailsPage, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes", List.of("Test Specification Objective"));
    }

    @Test
    void testCreateFromTemplate() throws IOException {
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
                SpecificationFormController.CREATE_MODE_FROM_TEMPLATE, specificationTemplateId);

        HtmlPage specificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, specificationId));
        ITValidationUtils.validateSpecificationDetails(specificationDetailsPage, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes",
                List.of("Test Stakeholder Template 1", "Test Stakeholder Template 2"));

        //verify Specification Objectives list
        HtmlPage specificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, specificationId));
        ITValidationUtils.validateSpecificationObjectiveList(specificationObjectiveListPage, List.of("Test Objective Template"));

        //verify Stakeholder Objectives list
        HtmlAnchor stakeholderAnchor = specificationDetailsPage.querySelector("#stakeholders tbody tr:nth-of-type(1) td a");
        HtmlPage stakeholderDetailsPage = (HtmlPage) stakeholderAnchor.openLinkInNewWindow();
        ITValidationUtils.validateStakeholderDetails(stakeholderDetailsPage, "Test Stakeholder Template 1",
                "Test Stakeholder Template 1 Description", "Test Stakeholder Template 1 Notes",
                List.of("Test Objective Template"));
    }
}
