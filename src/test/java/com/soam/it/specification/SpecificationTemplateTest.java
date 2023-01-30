package com.soam.it.specification;

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
class SpecificationTemplateTest {
    private static final String URL_SPECIFICATION_TEMPLATE_EDIT = "http://localhost/specification/template/%s/edit";
    private static final String URL_TEMPLATE_LINK_LIST = "http://localhost/templateLink/list";

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
    void testCreateFromTemplate() throws IOException {
        //prepare test data
        int specificationTemplateId = ITUtils.addSpecificationTemplate(webClient, "Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes");
        int stakeholderTemplateId = ITUtils.addStakeholderTemplate(webClient, "Test Stakeholder Template",
                "Test Stakeholder Template Description", "Test Stakeholder Template Notes");
        int objectiveTemplateId = ITUtils.addObjectiveTemplate(webClient, "Test Objective Template",
                "Test Objective Template Description", "Test Objective Template Notes");
        ITUtils.addTemplateLink(webClient, specificationTemplateId, stakeholderTemplateId, objectiveTemplateId);

        //execute "create from template"
        int specificationTemplateCopyId = ITUtils.copySpecificationTemplate(webClient, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes",
                specificationTemplateId);

        HtmlPage specificationTemplateCopyPage = webClient.getPage(String.format(URL_SPECIFICATION_TEMPLATE_EDIT, specificationTemplateCopyId));
        ITValidationUtils.validateSpecificationTemplateEdit(specificationTemplateCopyPage, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes");

        HtmlPage templateLinkPage = webClient.getPage(URL_TEMPLATE_LINK_LIST);
        ITValidationUtils.validateTemplateLink(templateLinkPage, specificationTemplateCopyId, new String[][] {
                {"Copy of Test Specification Template", "Test Stakeholder Template", "Test Objective Template"}
        });
    }
}
