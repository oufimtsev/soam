package com.soam.web.templatelink;

import com.soam.config.SoamConfiguration;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.service.EntityNotFoundException;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.service.templatelink.TemplateLinkService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplateLinkController.class)
@Import(SoamConfiguration.class)
class TemplateLinkControllerTest {
    private static final TemplateLink TEST_TEMPLATE_LINK = new TemplateLink();
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE = new SpecificationTemplate();
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE = new StakeholderTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_2 = new ObjectiveTemplate();

    private static final int NEW_TEMPLATE_LINK_ID = 5000;
    private static final int EMPTY_TEMPLATE_LINK_ID = 10000;

    private static final String URL_VIEW_FIND_TEMPLATE_LINK = "/templateLink/find";
    private static final String URL_NEW_TEMPLATE_LINK = "/templateLink/new";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_TEMPLATE.setId(1);
        TEST_SPECIFICATION_TEMPLATE.setName("Test Specification Template");
        TEST_SPECIFICATION_TEMPLATE.setDescription("Test description");
        TEST_SPECIFICATION_TEMPLATE.setNotes("Test notes");
        TEST_SPECIFICATION_TEMPLATE.setPriority(lowPriority);

        TEST_STAKEHOLDER_TEMPLATE.setId(10);
        TEST_STAKEHOLDER_TEMPLATE.setName("Test Stakeholder Template");
        TEST_STAKEHOLDER_TEMPLATE.setDescription("Test description");
        TEST_STAKEHOLDER_TEMPLATE.setNotes("Test notes");
        TEST_STAKEHOLDER_TEMPLATE.setPriority(lowPriority);

        TEST_OBJECTIVE_TEMPLATE_1.setId(100);
        TEST_OBJECTIVE_TEMPLATE_1.setName("Test Objective Template 1");
        TEST_OBJECTIVE_TEMPLATE_1.setDescription("Test description");
        TEST_OBJECTIVE_TEMPLATE_1.setNotes("Test notes");
        TEST_OBJECTIVE_TEMPLATE_1.setPriority(lowPriority);

        TEST_OBJECTIVE_TEMPLATE_2.setId(101);
        TEST_OBJECTIVE_TEMPLATE_2.setName("Test Objective Template 2");
        TEST_OBJECTIVE_TEMPLATE_2.setDescription("Test description");
        TEST_OBJECTIVE_TEMPLATE_2.setNotes("Test notes");
        TEST_OBJECTIVE_TEMPLATE_2.setPriority(lowPriority);

        TEST_TEMPLATE_LINK.setId(1000);
        TEST_TEMPLATE_LINK.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE);
        TEST_TEMPLATE_LINK.setStakeholderTemplate(TEST_STAKEHOLDER_TEMPLATE);
        TEST_TEMPLATE_LINK.setObjectiveTemplate(TEST_OBJECTIVE_TEMPLATE_1);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemplateLinkService templateLinkService;

    @MockBean
    private SpecificationTemplateService specificationTemplateService;

    @MockBean
    private StakeholderTemplateService stakeholderTemplateService;

    @MockBean
    private ObjectiveTemplateService objectiveTemplateService;

    @Autowired
    private WebConversionService conversionService;

    @BeforeEach
    void setup() {
        given(templateLinkService.getById(TEST_TEMPLATE_LINK.getId())).willReturn(TEST_TEMPLATE_LINK);
        given(templateLinkService.getById(EMPTY_TEMPLATE_LINK_ID)).willThrow(new EntityNotFoundException("Template Link", EMPTY_TEMPLATE_LINK_ID));
        given(templateLinkService.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                TEST_SPECIFICATION_TEMPLATE, TEST_STAKEHOLDER_TEMPLATE, TEST_OBJECTIVE_TEMPLATE_1)
        ).willReturn(Optional.of(TEST_TEMPLATE_LINK));
        given(specificationTemplateService.getById(TEST_SPECIFICATION_TEMPLATE.getId())).willReturn(TEST_SPECIFICATION_TEMPLATE);
        given(stakeholderTemplateService.getById(TEST_STAKEHOLDER_TEMPLATE.getId())).willReturn(TEST_STAKEHOLDER_TEMPLATE);
        given(objectiveTemplateService.getById(TEST_OBJECTIVE_TEMPLATE_1.getId())).willReturn(TEST_OBJECTIVE_TEMPLATE_1);
        given(objectiveTemplateService.getById(TEST_OBJECTIVE_TEMPLATE_2.getId())).willReturn(TEST_OBJECTIVE_TEMPLATE_2);

        given(templateLinkService.save(any())).will(invocation -> {
            TemplateLink templateLink = invocation.getArgument(0);
            if (templateLink.getId() == null) {
                templateLink.setId(NEW_TEMPLATE_LINK_ID);
            }
            return templateLink;
        });

        conversionService.addConverter(String.class, SpecificationTemplate.class, source -> specificationTemplateService.getById(Integer.parseInt(source)));
        conversionService.addConverter(String.class, StakeholderTemplate.class, source -> stakeholderTemplateService.getById(Integer.parseInt(source)));
        conversionService.addConverter(String.class, ObjectiveTemplate.class, source -> objectiveTemplateService.getById(Integer.parseInt(source)));
    }

    @Test
    void testFind() throws Exception {
        mockMvc.perform(get(URL_VIEW_FIND_TEMPLATE_LINK))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_TEMPLATE_LINKS))
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_OBJECTIVE_TEMPLATES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK_FORM))
                .andExpect(view().name(ViewConstants.VIEW_TEMPLATE_LINK_LIST));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE_LINK)
                        .param("specificationTemplate", String.valueOf(TEST_SPECIFICATION_TEMPLATE.getId()))
                        .param("stakeholderTemplate", String.valueOf(TEST_STAKEHOLDER_TEMPLATE.getId()))
                        .param("objectiveTemplate", String.valueOf(TEST_OBJECTIVE_TEMPLATE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK))
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUCCESS))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_TEMPLATE_LINK_EDIT, NEW_TEMPLATE_LINK_ID)));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE_LINK)
                        .param("specificationTemplate", String.valueOf(TEST_SPECIFICATION_TEMPLATE.getId()))
                        .param("stakeholderTemplate", String.valueOf(TEST_STAKEHOLDER_TEMPLATE.getId()))
                        .param("objectiveTemplate", String.valueOf(TEST_OBJECTIVE_TEMPLATE_1.getId())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK))
                .andExpect(model().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_TEMPLATE_LINK)
                        .param("specificationTemplate", "-1")
                        .param("stakeholderTemplate", String.valueOf(TEST_STAKEHOLDER_TEMPLATE.getId()))
                        .param("objectiveTemplate", String.valueOf(TEST_OBJECTIVE_TEMPLATE_1.getId())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_TEMPLATE_LINK, "specificationTemplate.name"))
                .andExpect(view().name(ViewConstants.VIEW_TEMPLATE_LINK_ADD_OR_UPDATE_FORM));
    }
}
