package com.soam.stakeholderobjective;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.web.ModelConstants;
import com.soam.web.ViewConstants;
import com.soam.web.stakeholderobjective.StakeholderObjectiveFormController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StakeholderObjectiveFormController.class)
public class StakeholderObjectiveFormControllerTest {
    private static Specification TEST_SPECIFICATION = new Specification();
    private static Stakeholder TEST_STAKEHOLDER = new Stakeholder();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_2 = new SpecificationObjective();
    private static StakeholderObjective TEST_STAKEHOLDER_OBJECTIVE_1 = new StakeholderObjective();

    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 999;
    private static final int EMPTY_STAKEHOLDER_OBJECTIVE_ID = 999;

    private static String URL_VIEW_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/{stakeholderObjectiveId}";
    private static String URL_NEW_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/new";
    private static String URL_EDIT_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/{stakeholderObjectiveId}/edit";
    private static String URL_DELETE_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/{stakeholderObjectiveId}/delete";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION.setId(1);
        TEST_SPECIFICATION.setName("Test Specification");

        TEST_STAKEHOLDER.setId(10);
        TEST_STAKEHOLDER.setName("Test Stakeholder");
        TEST_STAKEHOLDER.setSpecification(TEST_SPECIFICATION);

        TEST_SPECIFICATION_OBJECTIVE_1.setId(100);
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification(TEST_SPECIFICATION);
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Spec 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_1.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_1.setPriority(lowPriority);

        TEST_SPECIFICATION_OBJECTIVE_2.setId(200);
        TEST_SPECIFICATION_OBJECTIVE_2.setSpecification(TEST_SPECIFICATION);
        TEST_SPECIFICATION_OBJECTIVE_2.setName("Test Spec 2");
        TEST_SPECIFICATION_OBJECTIVE_2.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_2.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_2.setPriority(lowPriority);

        TEST_STAKEHOLDER_OBJECTIVE_1.setId(1000);
        TEST_STAKEHOLDER_OBJECTIVE_1.setStakeholder(TEST_STAKEHOLDER);
        TEST_STAKEHOLDER_OBJECTIVE_1.setSpecificationObjective(TEST_SPECIFICATION_OBJECTIVE_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setNotes(TEST_SPECIFICATION_OBJECTIVE_1.getNotes());
        TEST_STAKEHOLDER_OBJECTIVE_1.setPriority(TEST_SPECIFICATION_OBJECTIVE_1.getPriority());

        TEST_SPECIFICATION.setSpecificationObjectives(Lists.newArrayList(TEST_SPECIFICATION_OBJECTIVE_1, TEST_SPECIFICATION_OBJECTIVE_2));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;

    @MockBean
    private StakeholderRepository stakeholderRepository;

    @MockBean
    private SpecificationObjectiveRepository specificationObjectiveRepository;

    @MockBean
    private StakeholderObjectiveRepository stakeholderObjectiveRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @Autowired
    private WebConversionService conversionService;

    @BeforeEach
    void setup() {
        given(this.specificationRepository.findById(TEST_SPECIFICATION.getId())).willReturn(Optional.of(TEST_SPECIFICATION));
        given(this.specificationRepository.findById(TEST_SPECIFICATION.getId())).willReturn(Optional.of(TEST_SPECIFICATION));

        given(this.stakeholderRepository.findById(TEST_STAKEHOLDER.getId())).willReturn(Optional.of(TEST_STAKEHOLDER));

        given(this.specificationObjectiveRepository.findById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));
        given(this.specificationObjectiveRepository.findById(TEST_SPECIFICATION_OBJECTIVE_2.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_2));

        given(this.stakeholderObjectiveRepository.findById(TEST_STAKEHOLDER_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_OBJECTIVE_1));
        given(this.stakeholderObjectiveRepository.findByStakeholderAndSpecificationObjectiveId(TEST_STAKEHOLDER, TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_OBJECTIVE_1));

        conversionService.addConverter(String.class, Stakeholder.class, source -> stakeholderRepository.findById(Integer.parseInt(source)).orElse(null));
        conversionService.addConverter(String.class, SpecificationObjective.class, source -> specificationObjectiveRepository.findById(Integer.parseInt(source)).orElse(null));

        given(stakeholderObjectiveRepository.save(any())).will(invocation -> {
            StakeholderObjective stakeholderObjective = invocation.getArgument(0);
            stakeholderObjective.setId(2000);
            return stakeholderObjective;
        });
    }

    @Test
    void testViewStakeholderObjectiveDetails() throws Exception {
        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("notes", is(TEST_STAKEHOLDER_OBJECTIVE_1.getNotes()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_DETAILS));

        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s", TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), 42))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s", TEST_SPECIFICATION.getId())));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("notes", "Stakeholder Objective notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s/stakeholderObjective/%s",
                        TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), 2000)));
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER.getId()))
                        .param("specificationObjective", String.valueOf(EMPTY_SPECIFICATION_OBJECTIVE_ID))
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_OBJECTIVE_ID)))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, "specificationObjective"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId())))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, "specificationObjective"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("stakeholder", is(TEST_STAKEHOLDER_OBJECTIVE_1.getStakeholder()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("specificationObjective", is(TEST_STAKEHOLDER_OBJECTIVE_1.getSpecificationObjective()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("notes", is(TEST_STAKEHOLDER_OBJECTIVE_1.getNotes()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("priority", is(TEST_STAKEHOLDER_OBJECTIVE_1.getPriority()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s",TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())
                        .param("notes", "Updated notes")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s/stakeholderObjective/%s",
                        TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s", TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s", TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));
    }
}