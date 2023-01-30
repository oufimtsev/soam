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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StakeholderTemplateTest {
    private static final String URL_STAKEHOLDER_TEMPLATE_EDIT = "http://localhost/stakeholder/template/%s/edit";

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
    void testCreateStakeholderTemplate() throws IOException {
        //execute "create stakeholder template" action
        int stakeholderTemplateId = ITUtils.addStakeholderTemplate(webClient, "Test Stakeholder Template",
                "Test Stakeholder Template Description", "Test Stakeholder Template Notes");

        HtmlPage stakeholderTemplateEditPage = webClient.getPage(String.format(URL_STAKEHOLDER_TEMPLATE_EDIT, stakeholderTemplateId));
        ITValidationUtils.validateStakeholderTemplateEdit(stakeholderTemplateEditPage, "Test Stakeholder Template",
                "Test Stakeholder Template Description", "Test Stakeholder Template Notes");
    }
}
