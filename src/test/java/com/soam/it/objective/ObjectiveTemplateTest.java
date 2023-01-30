package com.soam.it.objective;

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
class ObjectiveTemplateTest {
    private static final String URL_OBJECTIVE_TEMPLATE_EDIT = "http://localhost/objective/template/%s/edit";

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
    void testCreateObjectiveTemplate() throws IOException {
        //execute "create objective template" action
        int objectiveTemplateId = ITUtils.addObjectiveTemplate(webClient, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");

        HtmlPage objectiveTemplateEditPage = webClient.getPage(String.format(URL_OBJECTIVE_TEMPLATE_EDIT, objectiveTemplateId));
        ITValidationUtils.validateObjectiveTemplateEdit(objectiveTemplateEditPage, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");
    }
}
