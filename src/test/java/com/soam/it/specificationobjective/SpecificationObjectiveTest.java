package com.soam.it.specificationobjective;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.soam.it.ITUtils;
import com.soam.it.ITValidationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SpecificationObjectiveTest {
    private static final String URL_SPECIFICATION_DETAILS = "http://localhost/specification/%s";
    private static final String URL_SPECIFICATION_OBJECTIVE_LIST = "http://localhost/specification/%s/specificationObjective/list";
    private static final String URL_SPECIFICATION_OBJECTIVE_DETAILS = "http://localhost/specification/%s/specificationObjective/%s";

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
    void testCreateSpecificationObjective() throws IOException {
        //prepare test data
        int specificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);

        //execute "create" action
        int specificationObjectiveId = ITUtils.addSpecificationObjective(webClient, specificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");

        //make sure that parent SOAM object is not affected by the POST action. This may happen in case Controller
        //does not isolate Model attributes from POST data binding
        HtmlPage specificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, specificationId));
        ITValidationUtils.validateSpecificationDetails(specificationDetailsPage, "Test Specification",
                "Test Specification Description", "Test Specification Notes", List.of());

        HtmlPage specificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, specificationId));
        ITValidationUtils.validateSpecificationObjectiveList(specificationObjectiveListPage, List.of("Test Specification Objective"));

        HtmlPage specificationObjectiveDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_DETAILS, specificationId, specificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveDetails(specificationObjectiveDetailsPage, "Test Specification Objective",
                "Test Specification", "Test Specification Objective Description",
                "Test Specification Objective Notes");
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
        HtmlPage specificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, specificationId));
        ITValidationUtils.validateSpecificationDetails(specificationDetailsPage, "Test Specification",
                "Test Specification Description", "Test Specification Notes", List.of());

        HtmlPage specificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, specificationId));
        ITValidationUtils.validateSpecificationObjectiveList(specificationObjectiveListPage, List.of("Updated Test Specification Objective"));

        HtmlPage specificationObjectiveDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_DETAILS, specificationId, editedSpecificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveDetails(specificationObjectiveDetailsPage, "Updated Test Specification Objective",
                "Test Specification", "Updated Test Specification Objective Description",
                "Updated Test Specification Objective Notes");
    }
}
