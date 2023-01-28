package com.soam.stakeholder;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import com.soam.web.stakeholder.StakeholderTemplateFormController;
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

@WebMvcTest(StakeholderTemplateFormController.class)
class StakeholderTemplateFormControllerTest {
    private static final StakeholderTemplate TEST_STAKEHOLDER_1 = new StakeholderTemplate();
    private static final StakeholderTemplate TEST_STAKEHOLDER_2 = new StakeholderTemplate();
    private static final int EMPTY_STAKEHOLDER_ID = 200;

    private static final String URL_NEW_TEMPLATE =  "/stakeholder/template/new";
    private static final String URL_EDIT_TEMPLATE =  "/stakeholder/template/{stakeholderId}/edit";
    private static final String URL_DELETE_TEMPLATE =  "/stakeholder/template/{stakeholderId}/delete";
    
    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_STAKEHOLDER_1.setId(100);
        TEST_STAKEHOLDER_1.setName("Test Spec 1");
        TEST_STAKEHOLDER_1.setDescription("desc");
        TEST_STAKEHOLDER_1.setNotes("notes");
        TEST_STAKEHOLDER_1.setPriority(lowPriority);
        
        TEST_STAKEHOLDER_2.setId(101);
        TEST_STAKEHOLDER_2.setName("Test Spec 2");
        TEST_STAKEHOLDER_2.setDescription("desc");
        TEST_STAKEHOLDER_2.setNotes("notes");
        TEST_STAKEHOLDER_2.setPriority(lowPriority);

        SpecificationTemplate testSpecificationTemplate = new SpecificationTemplate();
        testSpecificationTemplate.setId(1000);
        testSpecificationTemplate.setName("Test Specification Template");
        testSpecificationTemplate.setDescription("Test description");
        testSpecificationTemplate.setNotes("Test notes");
        testSpecificationTemplate.setPriority(lowPriority);

        ObjectiveTemplate testObjectiveTemplate = new ObjectiveTemplate();
        testObjectiveTemplate.setId(10000);
        testObjectiveTemplate.setName("Test Objective Template");
        testObjectiveTemplate.setDescription("Test description");
        testObjectiveTemplate.setNotes("Test notes");
        testObjectiveTemplate.setPriority(lowPriority);

        TemplateLink testTemplateLink = new TemplateLink();
        testTemplateLink.setSpecificationTemplate(testSpecificationTemplate);
        testTemplateLink.setStakeholderTemplate(TEST_STAKEHOLDER_2);
        testTemplateLink.setObjectiveTemplate(testObjectiveTemplate);

        TEST_STAKEHOLDER_2.setTemplateLinks(Lists.newArrayList(testTemplateLink));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StakeholderTemplateRepository stakeholderTemplateRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setup() {
        given( this.stakeholderTemplateRepository.findByName(TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderTemplateRepository.findByNameIgnoreCase(TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderTemplateRepository.findById(TEST_STAKEHOLDER_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderTemplateRepository.findById(EMPTY_STAKEHOLDER_ID)).willReturn(Optional.empty());
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_TEMPLATE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "spec notes").param("description", "Description"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, "description", "NotBlank"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testInitUpdateStakeholderForm() throws Exception {
        Mockito.when(this.stakeholderTemplateRepository.findById(TEST_STAKEHOLDER_1.getId())).thenReturn(Optional.of(TEST_STAKEHOLDER_1));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, hasProperty("name", is(TEST_STAKEHOLDER_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, hasProperty("description", is("desc"))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, hasProperty("notes", is("notes"))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, EMPTY_STAKEHOLDER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));
    }

    @Test
    void testProcessUpdateStakeholderFormSuccess() throws Exception {
        Mockito.when(this.stakeholderTemplateRepository.findById(EMPTY_STAKEHOLDER_ID)).thenReturn(Optional.empty());
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_STAKEHOLDER_1.getId())
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes here")
                        .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_STAKEHOLDER_1.getId())
                                .param("name", TEST_STAKEHOLDER_1.getName())
                                .param("notes", "notes here")
                                .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_STAKEHOLDER_1.getId())
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, "description"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, EMPTY_STAKEHOLDER_ID)
                        .param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteStakeholderSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_STAKEHOLDER_1.getId())
                        .param("name", TEST_STAKEHOLDER_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));
    }

    @Test
    void testProcessDeleteStakeholderError() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, EMPTY_STAKEHOLDER_ID)
                        .param("name", TEST_STAKEHOLDER_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));

        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_STAKEHOLDER_2.getId())
                        .param("name", TEST_STAKEHOLDER_2.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_LIST));
    }
}
