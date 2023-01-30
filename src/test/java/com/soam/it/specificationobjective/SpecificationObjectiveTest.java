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

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SpecificationObjectiveTest {
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

        HtmlPage specificationObjectiveListPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_LIST, specificationId));
        ITValidationUtils.validateSpecificationObjectiveList(specificationObjectiveListPage, List.of("Test Specification Objective"));

        HtmlPage specificationObjectiveDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_OBJECTIVE_DETAILS, specificationId, specificationObjectiveId));
        ITValidationUtils.validateSpecificationObjectiveDetails(specificationObjectiveDetailsPage, "Test Specification Objective",
                "Test Specification", "Test Specification Objective Description",
                "Test Specification Objective Notes");
    }
}
