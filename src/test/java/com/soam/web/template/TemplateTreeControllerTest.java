package com.soam.web.template;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.service.templatelink.TemplateLinkService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemplateTreeController.class)
class TemplateTreeControllerTest {
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_1 = new SpecificationTemplate();
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE_1 = new StakeholderTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static final TemplateLink TEST_TEMPLATE_LINK_1 = new TemplateLink();

    static {
        TEST_SPECIFICATION_TEMPLATE_1.setId(10);
        TEST_SPECIFICATION_TEMPLATE_1.setName("Test Specification Template 1");
        TEST_SPECIFICATION_TEMPLATE_1.setDescription("Test Specification Template Description 1");
        TEST_SPECIFICATION_TEMPLATE_1.setNotes("Test Specification Template Notes 1");

        TEST_STAKEHOLDER_TEMPLATE_1.setId(100);
        TEST_STAKEHOLDER_TEMPLATE_1.setName("Test Stakeholder Template 1");
        TEST_STAKEHOLDER_TEMPLATE_1.setDescription("Test Stakeholder Template Description 1");
        TEST_STAKEHOLDER_TEMPLATE_1.setNotes("Test Stakeholder Template Notes 1");

        TEST_OBJECTIVE_TEMPLATE_1.setId(1000);
        TEST_OBJECTIVE_TEMPLATE_1.setName("Test Objective Template 1");
        TEST_OBJECTIVE_TEMPLATE_1.setDescription("Test Objective Template Description 1");
        TEST_OBJECTIVE_TEMPLATE_1.setNotes("Test Objective Template Notes 1");

        TEST_TEMPLATE_LINK_1.setId(10000);
        TEST_TEMPLATE_LINK_1.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_1);
        TEST_TEMPLATE_LINK_1.setStakeholderTemplate(TEST_STAKEHOLDER_TEMPLATE_1);
        TEST_TEMPLATE_LINK_1.setObjectiveTemplate(TEST_OBJECTIVE_TEMPLATE_1);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationTemplateService specificationTemplateService;

    @MockBean
    private StakeholderTemplateService stakeholderTemplateService;

    @MockBean
    private ObjectiveTemplateService objectiveTemplateService;

    @MockBean
    private TemplateLinkService templateLinkService;

    @BeforeEach
    void setup() {
        given(specificationTemplateService.findAll()).willReturn(List.of(TEST_SPECIFICATION_TEMPLATE_1));
        given(stakeholderTemplateService.findAll()).willReturn(List.of(TEST_STAKEHOLDER_TEMPLATE_1));
        given(objectiveTemplateService.findAll()).willReturn(List.of(TEST_OBJECTIVE_TEMPLATE_1));
        given(templateLinkService.findAll()).willReturn(List.of(TEST_TEMPLATE_LINK_1));
    }

    @Test
    void testGetSpecificationTemplates() throws Exception {
        mockMvc.perform(get("/tree/specificationTemplate"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("specificationTemplate")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_SPECIFICATION_TEMPLATE_1.getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_SPECIFICATION_TEMPLATE_1.getName())));
    }

    @Test
    void testGetStakeholderTemplates() throws Exception {
        mockMvc.perform(get("/tree/stakeholderTemplate"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("stakeholderTemplate")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_STAKEHOLDER_TEMPLATE_1.getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_STAKEHOLDER_TEMPLATE_1.getName())));
    }

    @Test
    void testGetObjectiveTemplates() throws Exception {
        mockMvc.perform(get("/tree/objectiveTemplate"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("objectiveTemplate")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_OBJECTIVE_TEMPLATE_1.getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_OBJECTIVE_TEMPLATE_1.getName())));
    }

    @Test
    void testGetTemplateLinks() throws Exception {
        mockMvc.perform(get("/tree/link/templateLink"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("templateLink")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_TEMPLATE_LINK_1.getId()))));
    }
}
