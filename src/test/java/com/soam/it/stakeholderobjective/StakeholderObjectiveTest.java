package com.soam.it.stakeholderobjective;

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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StakeholderObjectiveTest {
    private static final String URL_STAKEHOLDER_DETAILS = "http://localhost/specification/%s/stakeholder/%s";
    private static final String URL_STAKEHOLDER_OBJECTIVE_DETAILS = "http://localhost/specification/%s/stakeholder/%s/stakeholderObjective/%s";

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
    void testCreateStakeholderObjective() throws IOException {
        //prepare test data
        int specificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int stakeholderId = ITUtils.addStakeholder(webClient, specificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");
        int specificationObjectiveId = ITUtils.addSpecificationObjective(webClient, specificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");

        //execute "create stakeholder objective" action
        int stakeholderObjectiveId = ITUtils.addStakeholderObjective(webClient, specificationId, stakeholderId,
                specificationObjectiveId, "Test Stakeholder Objective Notes");

        HtmlPage stakeholderDetailsPage = webClient.getPage(String.format(URL_STAKEHOLDER_DETAILS, specificationId, stakeholderId));
        ITValidationUtils.validateStakeholderDetails(stakeholderDetailsPage, "Test Stakeholder", "Test Stakeholder Description",
                "Test Stakeholder Notes", List.of("Test Specification Objective"));

        HtmlPage stakeholderObjectiveDetailsPage = webClient.getPage(String.format(URL_STAKEHOLDER_OBJECTIVE_DETAILS, specificationId, stakeholderId, stakeholderObjectiveId));
        ITValidationUtils.validateStakeholderObjectiveDetails(stakeholderObjectiveDetailsPage, "Test Specification Objective",
                "Test Stakeholder", "Test Specification Objective Description",
                "Test Stakeholder Objective Notes");
    }
}
