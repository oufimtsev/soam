package com.soam.specification;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import com.soam.web.specification.SpecificationTemplateFormController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationTemplateFormController.class)
class SpecificationTemplateFormControllerTest {
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_1 = new SpecificationTemplate();
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_2 = new SpecificationTemplate();
    private static final int EMPTY_SPECIFICATION_ID = 200;

    private static final String URL_NEW_TEMPLATE =  "/specification/template/new";
    private static final String URL_EDIT_TEMPLATE =  "/specification/template/{specificationId}/edit";
    private static final String URL_DELETE_TEMPLATE =  "/specification/template/{specificationId}/delete";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_TEMPLATE_1.setId(100);
        TEST_SPECIFICATION_TEMPLATE_1.setName("Test Spec 1");
        TEST_SPECIFICATION_TEMPLATE_1.setDescription("desc");
        TEST_SPECIFICATION_TEMPLATE_1.setNotes("notes");
        TEST_SPECIFICATION_TEMPLATE_1.setPriority(lowPriority);
        
        TEST_SPECIFICATION_TEMPLATE_2.setId(101);
        TEST_SPECIFICATION_TEMPLATE_2.setName("Test Spec 2");
        TEST_SPECIFICATION_TEMPLATE_2.setDescription("desc");
        TEST_SPECIFICATION_TEMPLATE_2.setNotes("notes");
        TEST_SPECIFICATION_TEMPLATE_2.setPriority(lowPriority);

        StakeholderTemplate testStakeholderTemplate = new StakeholderTemplate();
        testStakeholderTemplate.setId(1000);
        testStakeholderTemplate.setName("Test Stakeholder Template");
        testStakeholderTemplate.setDescription("Test description");
        testStakeholderTemplate.setNotes("Test notes");
        testStakeholderTemplate.setPriority(lowPriority);

        ObjectiveTemplate testObjectiveTemplate = new ObjectiveTemplate();
        testObjectiveTemplate.setId(10000);
        testObjectiveTemplate.setName("Test Objective Template");
        testObjectiveTemplate.setDescription("Test description");
        testObjectiveTemplate.setNotes("Test notes");
        testObjectiveTemplate.setPriority(lowPriority);

        TemplateLink testTemplateLink = new TemplateLink();
        testTemplateLink.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_2);
        testTemplateLink.setStakeholderTemplate(testStakeholderTemplate);
        testTemplateLink.setObjectiveTemplate(testObjectiveTemplate);

        TEST_SPECIFICATION_TEMPLATE_2.setTemplateLinks(Lists.newArrayList(testTemplateLink));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;

    @MockBean
    private SpecificationTemplateRepository specificationTemplateRepository;

    @MockBean
    private TemplateLinkRepository templateLinkRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setup() {
        given( this.specificationTemplateRepository.findByName(TEST_SPECIFICATION_TEMPLATE_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));
        given( this.specificationTemplateRepository.findByNameIgnoreCase(TEST_SPECIFICATION_TEMPLATE_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));
        given( this.specificationTemplateRepository.findById(TEST_SPECIFICATION_TEMPLATE_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));
        given( this.specificationTemplateRepository.findById(TEST_SPECIFICATION_TEMPLATE_2.getId())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_2));
        given( this.specificationTemplateRepository.findById(EMPTY_SPECIFICATION_ID)).willReturn(Optional.empty());
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_TEMPLATE)).andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "templateDeepCopy")
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_TEMPLATE_2.getId())))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", TEST_SPECIFICATION_TEMPLATE_1.getName())
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "name"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "templateDeepCopy")
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_ID)))
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testInitUpdateSpecificationForm() throws Exception {
        Mockito.when(this.specificationTemplateRepository.findById(TEST_SPECIFICATION_TEMPLATE_1.getId())).thenReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, hasProperty("name", is(TEST_SPECIFICATION_TEMPLATE_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, hasProperty("description", is("desc"))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, hasProperty("notes", is("notes"))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessUpdateSpecificationFormSuccess() throws Exception {
        Mockito.when(this.specificationTemplateRepository.findById(EMPTY_SPECIFICATION_ID)).thenReturn(Optional.empty());
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes here")
                        .param("description", "description there")
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                                .param("name", TEST_SPECIFICATION_TEMPLATE_1.getName())
                                .param("notes", "notes here")
                                .param("description", "description there")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, EMPTY_SPECIFICATION_ID)
                        .param("name", TEST_SPECIFICATION_TEMPLATE_1.getName())
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteSpecificationSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("name", TEST_SPECIFICATION_TEMPLATE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessDeleteSpecificationError() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, EMPTY_SPECIFICATION_ID)
                        .param("name", TEST_SPECIFICATION_TEMPLATE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect( flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_2.getId())
                        .param("name", TEST_SPECIFICATION_TEMPLATE_2.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }
}
