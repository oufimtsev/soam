package com.soam.it.stakeholder;

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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StakeholderTest {
    private static final String URL_SPECIFICATION_DETAILS = "http://localhost/specification/%s";
    private static final String URL_STAKEHOLDER_DETAILS = "http://localhost/specification/%s/stakeholder/%s";

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
    void testCreateStakeholder() throws IOException {
        //prepare test data
        int specificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);

        //execute "create stakeholder"
        int stakeholderId = ITUtils.addStakeholder(webClient, specificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");

        //make sure that parent SOAM object is not affected by the POST action. This may happen in case Controller
        //does not isolate Model attributes from POST data binding
        HtmlPage specificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, specificationId));
        ITValidationUtils.validateSpecificationDetails(specificationDetailsPage, "Test Specification",
                "Test Specification Description", "Test Specification Notes", List.of("Test Stakeholder"));

        HtmlPage stakeholderDetailsPage = webClient.getPage(String.format(URL_STAKEHOLDER_DETAILS, specificationId, stakeholderId));
        ITValidationUtils.validateStakeholderDetails(stakeholderDetailsPage, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes", List.of());
    }

    @Test
    void testEditStakeholder() throws IOException {
        //prepare test data
        int specificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int stakeholderId = ITUtils.addStakeholder(webClient, specificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");

        //execute "edit stakeholder"
        int editedStakeholderId = ITUtils.editStakeholder(webClient, specificationId, stakeholderId,  "Updated Test Stakeholder",
                "Updated Test Stakeholder Description", "Updated Test Stakeholder Notes");

        assertEquals(stakeholderId, editedStakeholderId);

        //make sure that parent SOAM object is not affected by the POST action. This may happen in case Controller
        //does not isolate Model attributes from POST data binding
        HtmlPage specificationDetailsPage = webClient.getPage(String.format(URL_SPECIFICATION_DETAILS, specificationId));
        ITValidationUtils.validateSpecificationDetails(specificationDetailsPage, "Test Specification",
                "Test Specification Description", "Test Specification Notes", List.of("Updated Test Stakeholder"));

        HtmlPage stakeholderDetailsPage = webClient.getPage(String.format(URL_STAKEHOLDER_DETAILS, specificationId, stakeholderId));
        ITValidationUtils.validateStakeholderDetails(stakeholderDetailsPage, "Updated Test Stakeholder",
                "Updated Test Stakeholder Description", "Updated Test Stakeholder Notes", List.of());
    }
}
