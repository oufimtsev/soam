package com.soam.it.specificationobjective;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.soam.it.ITUtils;
import com.soam.it.ITValidationUtils;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SpecificationObjectiveTest {
    private static final String URL_SPECIFICATION_EDIT = "http://localhost/specification/%s/edit";
    private static final String URL_TREE_SPECIFICATION_OBJECTIVES = "/tree/specification/%s/specificationObjective";
    private static final String URL_SPECIFICATION_OBJECTIVE_EDIT = "http://localhost/specification/%s/specificationObjective/%s/edit";

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
    void testCreateSpecificationObjective() throws IOException {
        //prepare test data
        int specificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);

        //execute "create" action
        int specificationObjectiveId = ITUtils.addSpecificationObjective(webClient, specificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");

        //make sure that parent SOAM object is not affected by the POST action. This may happen in case Controller
        //does not isolate Model attributes from POST data binding
        HtmlPage specificationEditPage = webClient.getPage(String.format(URL_SPECIFICATION_EDIT, specificationId));
        ITValidationUtils.validateSpecificationEdit(specificationEditPage, "Test Specification",
                "Test Specification Description", "Test Specification Notes");

        List<Map<String, String>> specificationObjectives = restTemplate.getForObject(String.format(URL_TREE_SPECIFICATION_OBJECTIVES, specificationId), List.class);
        assertEquals(1, specificationObjectives.size());

        HtmlPage specificationObjectiveEditPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_EDIT, specificationId, specificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveEdit(specificationObjectiveEditPage, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");
    }

    @Test
    void testEditSpecificationObjective() throws IOException {
        //prepare test data
        int specificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int specificationObjectiveId = ITUtils.addSpecificationObjective(webClient, specificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");

        //execute "edit" action
        int editedSpecificationObjectiveId = ITUtils.editSpecificationObjective(webClient, specificationId, specificationObjectiveId,
                "Updated Test Specification Objective", "Updated Test Specification Objective Description",
                "Updated Test Specification Objective Notes");

        assertEquals(specificationObjectiveId, editedSpecificationObjectiveId);

        //make sure that parent SOAM object is not affected by the POST action. This may happen in case Controller
        //does not isolate Model attributes from POST data binding
        HtmlPage specificationEditPage = webClient.getPage(String.format(URL_SPECIFICATION_EDIT, specificationId));
        ITValidationUtils.validateSpecificationEdit(specificationEditPage, "Test Specification",
                "Test Specification Description", "Test Specification Notes");

        List<Map<String, String>> specificationObjectives = restTemplate.getForObject(String.format(URL_TREE_SPECIFICATION_OBJECTIVES, specificationId), List.class);
        assertEquals(1, specificationObjectives.size());

        HtmlPage specificationObjectiveEditPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_EDIT, specificationId, editedSpecificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveEdit(specificationObjectiveEditPage, "Updated Test Specification Objective",
                "Updated Test Specification Objective Description", "Updated Test Specification Objective Notes");
    }
}
