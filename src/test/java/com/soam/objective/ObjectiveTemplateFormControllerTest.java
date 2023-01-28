package com.soam.objective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.web.objective.ObjectiveTemplateFormController;
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

@WebMvcTest(ObjectiveTemplateFormController.class)
public class ObjectiveTemplateFormControllerTest {

    private static final ObjectiveTemplate TEST_OBJECTIVE_1 = new ObjectiveTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_2 = new ObjectiveTemplate();
    private static final int EMPTY_OBJECTIVE_ID = 200;

    private static final String URL_NEW_TEMPLATE =  "/objective/template/new";
    private static final String URL_EDIT_TEMPLATE =  "/objective/template/{objectiveId}/edit";
    private static final String URL_DELETE_TEMPLATE =  "/objective/template/{objectiveId}/delete";
    
    private static final String VIEW_ADD_UPDATE_TEMPLATE = "objective/template/addUpdateObjectiveTemplate";
    private static final String VIEW_REDIRECT_LIST_TEMPLATE = "redirect:/objective/template/list";

    static {

        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);


        TEST_OBJECTIVE_1.setId(100);
        TEST_OBJECTIVE_1.setName("Test Spec 1");
        TEST_OBJECTIVE_1.setDescription("desc");
        TEST_OBJECTIVE_1.setNotes("notes");
        TEST_OBJECTIVE_1.setPriority(lowPriority);

        TEST_OBJECTIVE_2.setId(101);
        TEST_OBJECTIVE_2.setName("Test Spec 2");
        TEST_OBJECTIVE_2.setDescription("desc");
        TEST_OBJECTIVE_2.setNotes("notes");
        TEST_OBJECTIVE_2.setPriority(lowPriority);

        SpecificationTemplate testSpecificationTemplate = new SpecificationTemplate();
        testSpecificationTemplate.setId(1000);
        testSpecificationTemplate.setName("Test Specification Template");
        testSpecificationTemplate.setDescription("Test description");
        testSpecificationTemplate.setNotes("Test notes");
        testSpecificationTemplate.setPriority(lowPriority);

        StakeholderTemplate testStakeholderTemplate = new StakeholderTemplate();
        testStakeholderTemplate.setId(10000);
        testStakeholderTemplate.setName("Test Stakeholder Template");
        testStakeholderTemplate.setDescription("Test description");
        testStakeholderTemplate.setNotes("Test notes");
        testStakeholderTemplate.setPriority(lowPriority);

        TemplateLink testTemplateLink = new TemplateLink();
        testTemplateLink.setSpecificationTemplate(testSpecificationTemplate);
        testTemplateLink.setStakeholderTemplate(testStakeholderTemplate);
        testTemplateLink.setObjectiveTemplate(TEST_OBJECTIVE_2);

        TEST_OBJECTIVE_2.setTemplateLinks(Lists.newArrayList(testTemplateLink));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectiveTemplateRepository objectiveTemplateRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setup() {
        given( this.objectiveTemplateRepository.findByName(TEST_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_OBJECTIVE_1));
        given( this.objectiveTemplateRepository.findByNameIgnoreCase(TEST_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_OBJECTIVE_1));
        given( this.objectiveTemplateRepository.findById(TEST_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_OBJECTIVE_1));
        given( this.objectiveTemplateRepository.findById(EMPTY_OBJECTIVE_ID)).willReturn(Optional.empty());

    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_TEMPLATE)).andExpect(status().isOk())
                .andExpect(model().attributeExists("objectiveTemplate"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("objectiveTemplates"))
                .andExpect(view().name(VIEW_ADD_UPDATE_TEMPLATE));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", TEST_OBJECTIVE_1.getName())
                        .param("notes", "spec notes").param("description", "Description"))
                        .andExpect(model().attributeHasErrors("objectiveTemplate"))
                          .andExpect(model().attributeHasFieldErrors("objectiveTemplate", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_ADD_UPDATE_TEMPLATE));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", ""))
                .andExpect(model().attributeHasErrors("objectiveTemplate"))
                .andExpect(model().attributeHasFieldErrorCode("objectiveTemplate", "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_ADD_UPDATE_TEMPLATE));
    }

    @Test
    void testInitUpdateObjectiveForm() throws Exception {
        Mockito.when(this.objectiveTemplateRepository.findById(TEST_OBJECTIVE_1.getId())).thenReturn(Optional.of(TEST_OBJECTIVE_1));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, TEST_OBJECTIVE_1.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists("objectiveTemplate"))
                .andExpect(model().attribute("objectiveTemplate", hasProperty("name", is(TEST_OBJECTIVE_1.getName()))))
                .andExpect(model().attribute("objectiveTemplate", hasProperty("description", is("desc"))))
                .andExpect(model().attribute("objectiveTemplate", hasProperty("notes", is("notes"))))
                .andExpect(view().name(VIEW_ADD_UPDATE_TEMPLATE));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, EMPTY_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(VIEW_REDIRECT_LIST_TEMPLATE));
    }

    @Test
    void testProcessUpdateObjectiveFormSuccess() throws Exception {
        Mockito.when(this.objectiveTemplateRepository.findById(EMPTY_OBJECTIVE_ID)).thenReturn(Optional.empty());
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_OBJECTIVE_1.getId())
                        .param("name", "New Test Objective")
                        .param("notes", "notes here")
                        .param("description", "description there")
                        )
                    .andExpect(status().is3xxRedirection())
                .andExpect(view().name(VIEW_REDIRECT_LIST_TEMPLATE));


        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_OBJECTIVE_1.getId())
                                .param("name", TEST_OBJECTIVE_1.getName())
                                .param("notes", "notes here")
                                .param("description", "description there")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(VIEW_REDIRECT_LIST_TEMPLATE));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_OBJECTIVE_1.getId())
                        .param("name", "New Test Objective")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("objectiveTemplate"))
                .andExpect(model().attributeHasFieldErrors("objectiveTemplate", "description"))
                .andExpect(view().name(VIEW_ADD_UPDATE_TEMPLATE));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, EMPTY_OBJECTIVE_ID)
                        .param("name", TEST_OBJECTIVE_1.getName())
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("objectiveTemplate"))
                .andExpect(model().attributeHasFieldErrors("objectiveTemplate", "name"))
                .andExpect(view().name(VIEW_ADD_UPDATE_TEMPLATE));
    }

    @Test
    void testProcessDeleteObjectiveSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_OBJECTIVE_1.getId())
                        .param("name", TEST_OBJECTIVE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(VIEW_REDIRECT_LIST_TEMPLATE));

    }

    @Test
    void testProcessDeleteObjectiveError() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, EMPTY_OBJECTIVE_ID)
                        .param("name", TEST_OBJECTIVE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect( flash().attributeExists(Util.DANGER))
                .andExpect(view().name(VIEW_REDIRECT_LIST_TEMPLATE));

        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_OBJECTIVE_2.getId())
                        .param("name", TEST_OBJECTIVE_2.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(VIEW_REDIRECT_LIST_TEMPLATE));
    }
}
