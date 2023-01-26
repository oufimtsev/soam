package com.soam.specification;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.soam.ITUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class SpecificationTemplateTest {
    private static final String URL_SPECIFICATION_TEMPLATE_EDIT = "http://localhost/specification/template/%s/edit";
    private static final String URL_TEMPLATE_LINK_LIST = "http://localhost/templateLink/list";

    private WebClient webClient;

    @BeforeEach
    public void setup(WebApplicationContext context) {
        webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context)
                .build();
        //HtmlUnit is unable to execute Bootstrap JavaScript. We don't need JS processing for simple HTML-based IT,
        //so can safely disable JS processing in HtmlUnit
        //https://github.com/HtmlUnit/htmlunit/issues/232
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    @AfterEach
    public void tearDown() {
        webClient.close();
    }

    @Test
    public void testCreateFromTemplate() throws IOException {
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
        validateSpecificationTemplateEdit(specificationTemplateCopyPage, "Copy of Test Specification Template",
                "Test Specification Template Description", "Test Specification Template Notes");

        validateTemplateLink(specificationTemplateCopyId, new String[][] {
                {"Copy of Test Specification Template", "Test Stakeholder Template", "Test Objective Template"}
        });
    }

    private static void validateSpecificationTemplateEdit(HtmlPage page, String name, String description, String notes) {
        HtmlForm form = (HtmlForm) page.querySelectorAll("form").listIterator().next();

        assertEquals(name, form.getInputByName("name").getValue());
        assertEquals(description, form.getInputByName("description").getValue());
        assertEquals(notes, form.getTextAreaByName("notes").getText());
    }

    private void validateTemplateLink(int specificationTemplateId, String[][] templateLinkNames) throws IOException {
        HtmlPage page = webClient.getPage(URL_TEMPLATE_LINK_LIST);
        HtmlForm form = page.getHtmlElementById("templateLinksForm");
        form.getSelectByName("filterSpecificationTemplate").setSelectedAttribute(String.valueOf(specificationTemplateId), true);

        DomElement button = page.createElement("button");
        button.setAttribute("type", "submit");
        form.appendChild(button);

        Map<String, String[]> templateLinkMap = Arrays.stream(templateLinkNames)
                .collect(Collectors.toMap(linkNames -> linkNames[0], Function.identity()));

        HtmlPage filteredPage = button.click();
        filteredPage.querySelectorAll("table tbody tr").forEach(row -> {
            String actualSpecificationTemplateName = row.querySelector("td:nth-of-type(1)").getTextContent();
            String actualStakeholderTemplateName = row.querySelector("td:nth-of-type(2)").getTextContent();
            String actualObjectiveTemplateName = row.querySelector("td:nth-of-type(3)").getTextContent();
            String[] expectedTemplateLink = templateLinkMap.get(actualSpecificationTemplateName);
            assertEquals(expectedTemplateLink[0], actualSpecificationTemplateName);
            assertEquals(expectedTemplateLink[1], actualStakeholderTemplateName);
            assertEquals(expectedTemplateLink[2], actualObjectiveTemplateName);
        });
    }
}
