package com.soam.it.specification;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.soam.it.ITUtils;
import com.soam.it.ITValidationUtils;
import com.soam.web.specification.SpecificationFormController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SpecificationTest {
    private static final String URL_SPECIFICATION_EDIT = "http://localhost/specification/%s/edit";
    private static final String URL_SPECIFICATION_OBJECTIVE_EDIT = "http://localhost/specification/%s/specificationObjective/%s/edit";
    private static final String URL_STAKEHOLDER_EDIT = "http://localhost/specification/%s/stakeholder/%s/edit";
    private static final String URL_STAKEHOLDER_OBJECTIVE_EDIT = "http://localhost/specification/%s/stakeholder/%s/stakeholderObjective/%s/edit";
    private static final String URL_TREE_SPECIFICATION_OBJECTIVES = "/tree/specification/%s/specificationObjective";
    private static final String URL_TREE_STAKEHOLDERS = "/tree/specification/%s/stakeholder";
    private static final String URL_TREE_STAKEHOLDER_OBJECTIVES = "/tree/stakeholder/%s/stakeholderObjective";

    private WebClient webClient;
    @Autowired
    private TestRestTemplate restTemplate;

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

        HtmlPage dstSpecificationEditPage = webClient.getPage(String.format(URL_SPECIFICATION_EDIT, dstSpecificationId));
        ITValidationUtils.validateSpecificationEdit(dstSpecificationEditPage, "Copy of Test Specification",
                "Test Specification Description", "Test Specification Notes");

        //verify Specification Objectives
        List<Map<String, String>> specificationObjectives = restTemplate.getForObject(String.format(URL_TREE_SPECIFICATION_OBJECTIVES, dstSpecificationId), List.class);
        assertEquals(1, specificationObjectives.size());
        int dstSpecificationObjectiveId = Integer.parseInt(specificationObjectives.get(0).get("id"));
        HtmlPage dstSpecificationObjectiveEditPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_EDIT, dstSpecificationId, dstSpecificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveEdit(dstSpecificationObjectiveEditPage, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");

        //verify Stakeholders
        List<Map<String, String>> stakeholders = restTemplate.getForObject(String.format(URL_TREE_STAKEHOLDERS, dstSpecificationId), List.class);
        assertEquals(1, stakeholders.size());
        int dstStakeholderId = Integer.parseInt(stakeholders.get(0).get("id"));
        HtmlPage dstStakeholderEditPage = webClient.getPage(String.format(URL_STAKEHOLDER_EDIT, dstSpecificationId, dstStakeholderId));
        ITValidationUtils.validateStakeholderEdit(dstStakeholderEditPage, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");

        //verify Stakeholder Objectives
        List<Map<String, String>> stakeholderObjectives = restTemplate.getForObject(String.format(URL_TREE_STAKEHOLDER_OBJECTIVES, dstStakeholderId), List.class);
        assertEquals(1, stakeholderObjectives.size());
        int dstStakeholderObjectiveId = Integer.parseInt(stakeholderObjectives.get(0).get("id"));
        HtmlPage dstStakeholderObjectiveEditPage = webClient.getPage(String.format(URL_STAKEHOLDER_OBJECTIVE_EDIT, dstSpecificationId, dstStakeholderId, dstStakeholderObjectiveId));
        ITValidationUtils.validateStakeholderObjectiveEdit(dstStakeholderObjectiveEditPage, "Test Specification Objective",
                "Test Specification Objective Description", "Test Stakeholder Objective Notes");
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

        HtmlPage specificationEditPage = webClient.getPage(String.format(URL_SPECIFICATION_EDIT, specificationId));
        ITValidationUtils.validateSpecificationEdit(specificationEditPage, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes");

        //verify Specification Objectives
        List<Map<String, String>> specificationObjectives = restTemplate.getForObject(String.format(URL_TREE_SPECIFICATION_OBJECTIVES, specificationId), List.class);
        assertEquals(1, specificationObjectives.size());
        int dstSpecificationObjectiveId = Integer.parseInt(specificationObjectives.get(0).get("id"));
        HtmlPage dstSpecificationObjectiveEditPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_EDIT, specificationId, dstSpecificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveEdit(dstSpecificationObjectiveEditPage, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");

        //verify Stakeholders
        List<Map<String, String>> stakeholders = restTemplate.getForObject(String.format(URL_TREE_STAKEHOLDERS, specificationId), List.class);
        assertEquals(2, stakeholders.size());
        int stakeholder1Id = stakeholders.stream()
                .filter(stakeholderData -> "Test Stakeholder Template 1".equals(stakeholderData.get("name")))
                .map(stakeholderData -> Integer.parseInt(stakeholderData.get("id")))
                .findAny().get();
        HtmlPage dstStakeholder1EditPage = webClient.getPage(String.format(URL_STAKEHOLDER_EDIT, specificationId, stakeholder1Id));
        ITValidationUtils.validateStakeholderEdit(dstStakeholder1EditPage, "Test Stakeholder Template 1",
                "Test Stakeholder Template 1 Description", "Test Stakeholder Template 1 Notes");
        int stakeholder2Id = stakeholders.stream()
                .filter(stakeholderData -> "Test Stakeholder Template 2".equals(stakeholderData.get("name")))
                .map(stakeholderData -> Integer.parseInt(stakeholderData.get("id")))
                .findAny().get();
        HtmlPage dstStakeholder2EditPage = webClient.getPage(String.format(URL_STAKEHOLDER_EDIT, specificationId, stakeholder2Id));
        ITValidationUtils.validateStakeholderEdit(dstStakeholder2EditPage, "Test Stakeholder Template 2",
                "Test Stakeholder Template 2 Description", "Test Stakeholder Template 2 Notes");

        //verify Stakeholder Objectives
        List<Map<String, String>> stakeholder1Objectives = restTemplate.getForObject(String.format(URL_TREE_STAKEHOLDER_OBJECTIVES, stakeholder1Id), List.class);
        assertEquals(1, stakeholder1Objectives.size());
        int stakeholderObjective1Id = Integer.parseInt(stakeholder1Objectives.get(0).get("id"));
        HtmlPage dstStakeholderObjective1EditPage = webClient.getPage(String.format(URL_STAKEHOLDER_OBJECTIVE_EDIT, specificationId, stakeholder1Id, stakeholderObjective1Id));
        ITValidationUtils.validateStakeholderObjectiveEdit(dstStakeholderObjective1EditPage, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");
        List<Map<String, String>> stakeholder2Objectives = restTemplate.getForObject(String.format(URL_TREE_STAKEHOLDER_OBJECTIVES, stakeholder2Id), List.class);
        assertEquals(1, stakeholder2Objectives.size());
        int stakeholderObjective2Id = Integer.parseInt(stakeholder2Objectives.get(0).get("id"));
        HtmlPage dstStakeholderObjective2EditPage = webClient.getPage(String.format(URL_STAKEHOLDER_OBJECTIVE_EDIT, specificationId, stakeholder2Id, stakeholderObjective2Id));
        ITValidationUtils.validateStakeholderObjectiveEdit(dstStakeholderObjective2EditPage, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");
    }
}
