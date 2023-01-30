package com.soam.it;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Helper utilities for writing integration tests.
 */
public final class ITUtils {
    private static final String URL_NEW_SPECIFICATION = "http://localhost/specification/new";
    private static final String URL_NEW_STAKEHOLDER = "http://localhost/specification/%s/stakeholder/new";
    private static final String URL_NEW_SPECIFICATION_OBJECTIVE = "http://localhost/specification/%s/specificationObjective/new";
    private static final String URL_NEW_STAKEHOLDER_OBJECTIVE = "http://localhost/specification/%s/stakeholder/%s/stakeholderObjective/new";
    private static final String URL_NEW_SPECIFICATION_TEMPLATE = "http://localhost/specification/template/new";
    private static final String URL_NEW_STAKEHOLDER_TEMPLATE = "http://localhost/stakeholder/template/new";
    private static final String URL_NEW_OBJECTIVE_TEMPLATE = "http://localhost/objective/template/new";
    private static final String URL_TEMPLATE_LINK_LIST = "http://localhost/templateLink/list";

    private static final Pattern REDIRECT_SPECIFICATION_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)$");
    private static final Pattern REDIRECT_STAKEHOLDER_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/stakeholder/(\\d+)$");
    private static final Pattern REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/specificationObjective/(\\d+)$");
    private static final Pattern REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS = Pattern.compile("^http://localhost/specification/(\\d+)/stakeholder/(\\d+)/stakeholderObjective/(\\d+)$");
    private static final Pattern REDIRECT_SPECIFICATION_TEMPLATE_EDIT = Pattern.compile("^http://localhost/specification/template/(\\d+)/edit$");
    private static final Pattern REDIRECT_STAKEHOLDER_TEMPLATE_EDIT = Pattern.compile("^http://localhost/stakeholder/template/(\\d+)/edit$");
    private static final Pattern REDIRECT_OBJECTIVE_TEMPLATE_EDIT = Pattern.compile("^http://localhost/objective/template/(\\d+)/edit$");

    public static WebClient prepareWebClient(WebApplicationContext context) {
        WebClient webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context)
                .build();
        //HtmlUnit is unable to execute Bootstrap JavaScript. We don't need JS processing for simple HTML-based IT,
        //so can safely disable JS processing in HtmlUnit
        //https://github.com/HtmlUnit/htmlunit/issues/232
        webClient.getOptions().setJavaScriptEnabled(false);
        return webClient;
    }

    public static int addSpecification(WebClient webClient, String name, String description, String notes, String collectionType, int collectionItemId) throws IOException {
        return addSoamObject(webClient, URL_NEW_SPECIFICATION, REDIRECT_SPECIFICATION_DETAILS,
                name, description, notes, collectionType, collectionItemId);
    }

    public static int addStakeholder(WebClient webClient, int specificationId, String name, String description, String notes) throws IOException {
        return addSoamObject(webClient, String.format(URL_NEW_STAKEHOLDER, specificationId), REDIRECT_STAKEHOLDER_DETAILS,
                name, description, notes, null, -1);
    }

    public static int addSpecificationObjective(WebClient webClient, int specificationId, String name, String description, String notes) throws IOException {
        return addSoamObject(webClient, String.format(URL_NEW_SPECIFICATION_OBJECTIVE, specificationId), REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS,
                name, description, notes, null, -1);
    }

    public static int addStakeholderObjective(
            WebClient webClient, int specificationId, int stakeholderId, int specificationObjectiveId,
            String notes) throws IOException {
        HtmlPage page = webClient.getPage(String.format(URL_NEW_STAKEHOLDER_OBJECTIVE, specificationId, stakeholderId));
        HtmlForm form = page.getHtmlElementById("add-stakeholder-objective-form");
        form.getInputByName("stakeholder").setValue(String.valueOf(stakeholderId));
        form.getInputByName("collectionItemId").setValue(String.valueOf(specificationObjectiveId));
        form.getTextAreaByName("notes").setText(notes);
        HtmlPage detailsPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        Matcher redirectMatcher = REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS.matcher(detailsPage.getUrl().toString());
        assertTrue(redirectMatcher.matches());
        assertEquals(specificationId, Integer.parseInt(redirectMatcher.group(1)));
        assertEquals(stakeholderId, Integer.parseInt(redirectMatcher.group(2)));
        return Integer.parseInt(redirectMatcher.group(3));
    }

    public static int addSpecificationTemplate(WebClient webClient, String name, String description, String notes) throws IOException {
        return addTemplateObject(webClient, URL_NEW_SPECIFICATION_TEMPLATE, REDIRECT_SPECIFICATION_TEMPLATE_EDIT, name,
                description, notes, "template", -1);
    }

    public static int copySpecificationTemplate(
            WebClient webClient, String name, String description, String notes,
            int srcSpecificationTemplateId) throws IOException {
        return addTemplateObject(webClient, URL_NEW_SPECIFICATION_TEMPLATE, REDIRECT_SPECIFICATION_TEMPLATE_EDIT, name,
                description, notes, "templateDeepCopy", srcSpecificationTemplateId);
    }

    public static int addStakeholderTemplate(WebClient webClient, String name, String description, String notes) throws IOException {
        return addTemplateObject(webClient, URL_NEW_STAKEHOLDER_TEMPLATE, REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, name,
                description, notes, null, -1);
    }

    public static int addObjectiveTemplate(WebClient webClient, String name, String description, String notes) throws IOException {
        return addTemplateObject(webClient, URL_NEW_OBJECTIVE_TEMPLATE, REDIRECT_OBJECTIVE_TEMPLATE_EDIT, name,
                description, notes, null, -1);
    }

    public static void addTemplateLink(WebClient webClient, int specificationTemplateId, int stakeholderTemplateId, int objectiveTemplateId) throws IOException {
        HtmlPage page = webClient.getPage(URL_TEMPLATE_LINK_LIST);
        HtmlForm form = page.getHtmlElementById("templateLinksForm");
        form.getSelectByName("newTemplateLink.specificationTemplate").setSelectedAttribute(String.valueOf(specificationTemplateId), true);
        form.getSelectByName("newTemplateLink.stakeholderTemplate").setSelectedAttribute(String.valueOf(stakeholderTemplateId), true);
        form.getSelectByName("newTemplateLink.objectiveTemplate").setSelectedAttribute(String.valueOf(objectiveTemplateId), true);

        HtmlButton addButton = form.querySelector("form tfoot button");
        addButton.click();
    }

    private static int addSoamObject(
            WebClient webClient, String addPageUrl, Pattern detailsPagePattern, String name, String description,
            String notes, String collectionType, int collectionItemId) throws IOException {
        HtmlPage page = webClient.getPage(addPageUrl);
        HtmlForm form = page.querySelector("form");
        form.getInputByName("name").setValue(name);
        form.getInputByName("description").setValue(description);
        form.getTextAreaByName("notes").setText(notes);
        if (collectionType != null) {
            form.getInputByName("collectionType").setValue(collectionType);
            form.getInputByName("collectionItemId").setValue(String.valueOf(collectionItemId));
        }
        HtmlPage detailsPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        Matcher redirectMatcher = detailsPagePattern.matcher(detailsPage.getUrl().toString());
        assertTrue(redirectMatcher.matches());
        return Integer.parseInt(redirectMatcher.group(redirectMatcher.groupCount()));
    }

    private static int addTemplateObject(
            WebClient webClient, String addPageUrl, Pattern editPagePattern, String name, String description, String notes,
            String collectionType, int collectionItemId) throws IOException {
        HtmlPage page = webClient.getPage(addPageUrl);
        HtmlForm form = page.querySelector("form");
        form.getInputByName("name").setValue(name);
        form.getInputByName("description").setValue(description);
        if (collectionType != null) {
            form.getInputByName("collectionType").setValue(collectionType);
            form.getInputByName("collectionItemId").setValue(String.valueOf(collectionItemId));
        }
        form.getTextAreaByName("notes").setText(notes);
        HtmlPage listingPage = form.getOneHtmlElementByAttribute("button", "type", "submit").click();
        HtmlAnchor a = (HtmlAnchor) listingPage.querySelectorAll("table tbody tr td:nth-of-type(1) a").stream()
                .filter(node -> name.equals(node.getTextContent()))
                .findFirst()
                .orElseThrow();
        URL detailsPageUrl = listingPage.getFullyQualifiedUrl(a.getHrefAttribute());
        Matcher redirectMatcher = editPagePattern.matcher(detailsPageUrl.toString());
        assertTrue(redirectMatcher.matches());
        return Integer.parseInt(redirectMatcher.group(1));
    }

    private ITUtils() {
    }
}
