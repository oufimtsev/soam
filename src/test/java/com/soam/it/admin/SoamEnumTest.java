package com.soam.it.admin;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.soam.it.ITUtils;
import com.soam.model.priority.PriorityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SoamEnumTest {
    private static final String URL_SOAM_ENUM_LIST = "http://localhost/admin/soamEnum/list";

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
    void testViewEdit() throws IOException {
        //validate initial list of priority enums
        HtmlPage listPage = webClient.getPage(URL_SOAM_ENUM_LIST);
        validateSoamEnumRow(listPage, 1, PriorityType.class.getSimpleName(), "Low", 1);
        validateSoamEnumRow(listPage, 2, PriorityType.class.getSimpleName(), "Medium", 2);
        validateSoamEnumRow(listPage, 3, PriorityType.class.getSimpleName(), "High", 3);

        //load Soam Enum update page and validate the form has expected values
        HtmlAnchor updatePageAnchor = listPage.querySelector("table.table-striped tbody tr:nth-of-type(2) td:nth-of-type(2) a");
        HtmlPage updatePage = (HtmlPage) updatePageAnchor.openLinkInNewWindow();
        HtmlForm form = updatePage.querySelector("form");
        assertEquals(PriorityType.class.getSimpleName(), form.getInputByName("type").getValue());
        assertEquals("Medium", form.getInputByName("name").getValue());
        assertEquals(String.valueOf(2), form.getInputByName("sequence").getValue());

        //modify Soam Enum name and sequence and submit the update form
        form.getInputByName("name").setValue("Updated Medium");
        form.getInputByName("sequence").setValue("22");
        HtmlButton submitButton = form.querySelector("button");
        HtmlPage listPage2 = submitButton.click();

        //confirm that soam enum updates have applied
        validateSoamEnumRow(listPage2, 3, PriorityType.class.getSimpleName(), "Updated Medium", 22);
    }

    private static void validateSoamEnumRow(HtmlPage page, int row, String type, String name, int sequence) {
        assertEquals(type,
                page.querySelector("table.table-striped tbody tr:nth-of-type(" + row + ") td:nth-of-type(1)").getTextContent());
        assertEquals(name,
                page.querySelector("table.table-striped tbody tr:nth-of-type(" + row + ") td:nth-of-type(2) a").getTextContent());
        assertEquals(String.valueOf(sequence),
                page.querySelector("table.table-striped tbody tr:nth-of-type(" + row + ") td:nth-of-type(3)").getTextContent());
    }
}
