package com.soam.it.admin;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.soam.it.ITUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DeleteSpecificationTest {
    private static final String URL_SPECIFICATION_LIST = "http://localhost/admin/deleteSpecification/list";

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
    void testDeleteCascadeSpecification() throws IOException {
        //prepare test data
        int srcSpecificationId = ITUtils.addSpecification(webClient, "Test Specification",
                "Test Specification Description", "Test Specification Notes", "", -1);
        int srcStakeholderId = ITUtils.addStakeholder(webClient, srcSpecificationId, "Test Stakeholder",
                "Test Stakeholder Description", "Test Stakeholder Notes");
        int srcSpecificationObjectiveId = ITUtils.addSpecificationObjective(webClient, srcSpecificationId, "Test Specification Objective",
                "Test Specification Objective Description", "Test Specification Objective Notes");
        ITUtils.addStakeholderObjective(webClient, srcSpecificationId, srcStakeholderId,
                srcSpecificationObjectiveId, "Test Stakeholder Objective Notes");

        //validate initial rendering on delete specification page
        HtmlPage page = webClient.getPage(URL_SPECIFICATION_LIST);
        assertEquals("Test Specification",
                page.querySelector("table.table-striped tbody tr:nth-of-type(1) td span").getTextContent());

        //perform delete action
        HtmlButton deleteButton = page.querySelector("table.table-striped tbody tr:nth-of-type(1) button");
        HtmlPage pageAfterDelete = deleteButton.click();

        //validate specification list is empty
        assertNull(pageAfterDelete.querySelector("table.table-striped tbody tr:nth-of-type(1) button"));
    }
}
