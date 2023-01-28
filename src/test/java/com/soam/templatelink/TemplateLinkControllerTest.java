package com.soam.templatelink;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import com.soam.web.templatelink.TemplateLinkController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplateLinkController.class)
class TemplateLinkControllerTest {
    private static TemplateLink TEST_TEMPLATE_LINK = new TemplateLink();
    private static SpecificationTemplate TEST_SPECIFICATION_TEMPLATE = new SpecificationTemplate();
    private static StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE = new StakeholderTemplate();
    private static ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_2 = new ObjectiveTemplate();

    private static final int EMPTY_TEMPLATE_LINK_ID = 10000;

    private static String URL_VIEW_TEMPLATE_LINK_LIST = "/templateLink/list";
    private static String URL_NEW_TEMPLATE_LINK = "/templateLink/new";
    private static String URL_DELETE_TEMPLATE_LINK = "/templateLink/delete";

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
    private TemplateLinkRepository templateLinkRepository;

    @MockBean
    private SpecificationTemplateRepository specificationTemplateRepository;

    @MockBean
    private StakeholderTemplateRepository stakeholderTemplateRepository;

    @MockBean
    private ObjectiveTemplateRepository objectiveTemplateRepository;

    @Autowired
    private WebConversionService conversionService;

    @BeforeEach
    void setup() {
        given(templateLinkRepository.findById(TEST_TEMPLATE_LINK.getId())).willReturn(Optional.of(TEST_TEMPLATE_LINK));
        given(templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(
                TEST_SPECIFICATION_TEMPLATE, TEST_STAKEHOLDER_TEMPLATE, TEST_OBJECTIVE_TEMPLATE_1)
        ).willReturn(Optional.of(TEST_TEMPLATE_LINK));
        given(specificationTemplateRepository.findById(TEST_SPECIFICATION_TEMPLATE.getId())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE));
        given(stakeholderTemplateRepository.findById(TEST_STAKEHOLDER_TEMPLATE.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_TEMPLATE));
        given(objectiveTemplateRepository.findById(TEST_OBJECTIVE_TEMPLATE_1.getId())).willReturn(Optional.of(TEST_OBJECTIVE_TEMPLATE_1));
        given(objectiveTemplateRepository.findById(TEST_OBJECTIVE_TEMPLATE_2.getId())).willReturn(Optional.of(TEST_OBJECTIVE_TEMPLATE_2));

        conversionService.addConverter(String.class, SpecificationTemplate.class, source -> specificationTemplateRepository.findById(Integer.parseInt(source)).orElse(null));
        conversionService.addConverter(String.class, StakeholderTemplate.class, source -> stakeholderTemplateRepository.findById(Integer.parseInt(source)).orElse(null));
        conversionService.addConverter(String.class, ObjectiveTemplate.class, source -> objectiveTemplateRepository.findById(Integer.parseInt(source)).orElse(null));
    }

    @Test
    void testListAllTemplateLinks() throws Exception {
        mockMvc.perform(get(URL_VIEW_TEMPLATE_LINK_LIST))
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
                        .param("newTemplateLink.specificationTemplate", String.valueOf(TEST_SPECIFICATION_TEMPLATE.getId()))
                        .param("newTemplateLink.stakeholderTemplate", String.valueOf(TEST_STAKEHOLDER_TEMPLATE.getId()))
                        .param("newTemplateLink.objectiveTemplate", String.valueOf(TEST_OBJECTIVE_TEMPLATE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK_FORM))
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE_LINK)
                        .param("newTemplateLink.specificationTemplate", String.valueOf(TEST_SPECIFICATION_TEMPLATE.getId()))
                        .param("newTemplateLink.stakeholderTemplate", String.valueOf(TEST_STAKEHOLDER_TEMPLATE.getId()))
                        .param("newTemplateLink.objectiveTemplate", String.valueOf(TEST_OBJECTIVE_TEMPLATE_1.getId())))
                .andExpect(flash().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK_FORM))
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST));

        mockMvc.perform(post(URL_NEW_TEMPLATE_LINK)
                        .param("newTemplateLink.specificationTemplate", "-1")
                        .param("newTemplateLink.stakeholderTemplate", String.valueOf(TEST_STAKEHOLDER_TEMPLATE.getId()))
                        .param("newTemplateLink.objectiveTemplate", String.valueOf(TEST_OBJECTIVE_TEMPLATE_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK_FORM))
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST));
    }

    @Test
    void testProcessDeleteTemplateLinkSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE_LINK)
                        .param("deleteTemplateLinkId", String.valueOf(TEST_TEMPLATE_LINK.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(flash().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK_FORM))
                .andExpect(view().name(RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST));
    }

    @Test
    void testProcessDeleteTemplateLinkError() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE_LINK, EMPTY_TEMPLATE_LINK_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(flash().attributeExists(ModelConstants.ATTR_TEMPLATE_LINK_FORM))
                .andExpect(view().name(RedirectConstants.REDIRECT_TEMPLATE_LINK_LIST));
    }
}
