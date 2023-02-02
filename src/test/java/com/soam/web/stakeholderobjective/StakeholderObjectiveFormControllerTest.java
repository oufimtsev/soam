package com.soam.web.stakeholderobjective;

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
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
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
class StakeholderObjectiveFormControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Specification TEST_SPECIFICATION_2 = new Specification();
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final Stakeholder TEST_STAKEHOLDER_2 = new Stakeholder();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_2 = new SpecificationObjective();
    private static final StakeholderObjective TEST_STAKEHOLDER_OBJECTIVE_1 = new StakeholderObjective();

    private static final int EMPTY_SPECIFICATION_ID = 9;
    private static final int EMPTY_STAKEHOLDER_ID = 99;
    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 999;
    private static final int EMPTY_STAKEHOLDER_OBJECTIVE_ID = 9999;

    private static final String URL_VIEW_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/{stakeholderObjectiveId}";
    private static final String URL_NEW_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/new";
    private static final String URL_EDIT_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/{stakeholderObjectiveId}/edit";
    private static final String URL_DELETE_STAKEHOLDER_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/stakeholderObjective/{stakeholderObjectiveId}/delete";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_1.setId(1);
        TEST_SPECIFICATION_1.setName("Test Specification 1");
        TEST_SPECIFICATION_2.setId(2);
        TEST_SPECIFICATION_2.setName("Test Specification 2");

        TEST_STAKEHOLDER_1.setId(10);
        TEST_STAKEHOLDER_1.setName("Test Stakeholder 1");
        TEST_STAKEHOLDER_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_STAKEHOLDER_2.setId(20);
        TEST_STAKEHOLDER_2.setName("Test Stakeholder 2");
        TEST_STAKEHOLDER_2.setSpecification(TEST_SPECIFICATION_2);

        TEST_SPECIFICATION_OBJECTIVE_1.setId(100);
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Spec 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_1.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_1.setPriority(lowPriority);

        TEST_SPECIFICATION_OBJECTIVE_2.setId(200);
        TEST_SPECIFICATION_OBJECTIVE_2.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_OBJECTIVE_2.setName("Test Spec 2");
        TEST_SPECIFICATION_OBJECTIVE_2.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_2.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_2.setPriority(lowPriority);

        TEST_STAKEHOLDER_OBJECTIVE_1.setId(1000);
        TEST_STAKEHOLDER_OBJECTIVE_1.setStakeholder(TEST_STAKEHOLDER_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setSpecificationObjective(TEST_SPECIFICATION_OBJECTIVE_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setNotes(TEST_SPECIFICATION_OBJECTIVE_1.getNotes());
        TEST_STAKEHOLDER_OBJECTIVE_1.setPriority(TEST_SPECIFICATION_OBJECTIVE_1.getPriority());

        TEST_SPECIFICATION_1.setSpecificationObjectives(Lists.newArrayList(TEST_SPECIFICATION_OBJECTIVE_1, TEST_SPECIFICATION_OBJECTIVE_2));
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
        given(specificationRepository.findById(TEST_SPECIFICATION_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_1));
        given(specificationRepository.findById(TEST_SPECIFICATION_2.getId())).willReturn(Optional.of(TEST_SPECIFICATION_2));

        given(stakeholderRepository.findById(TEST_STAKEHOLDER_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given(stakeholderRepository.findById(TEST_STAKEHOLDER_2.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_2));

        given(specificationObjectiveRepository.findById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));
        given(specificationObjectiveRepository.findById(TEST_SPECIFICATION_OBJECTIVE_2.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_2));

        given(stakeholderObjectiveRepository.findById(TEST_STAKEHOLDER_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_OBJECTIVE_1));
        given(stakeholderObjectiveRepository.findByStakeholderAndSpecificationObjectiveId(TEST_STAKEHOLDER_1, TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_OBJECTIVE_1));

        conversionService.addConverter(String.class, Stakeholder.class, source -> stakeholderRepository.findById(Integer.parseInt(source)).orElse(null));
        conversionService.addConverter(String.class, SpecificationObjective.class, source -> specificationObjectiveRepository.findById(Integer.parseInt(source)).orElse(null));

        given(stakeholderObjectiveRepository.save(any())).will(invocation -> {
            StakeholderObjective stakeholderObjective = invocation.getArgument(0);
            stakeholderObjective.setId(2000);
            return stakeholderObjective;
        });
    }

    @Test
    void testViewDetails() throws Exception {
        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("notes", is(TEST_STAKEHOLDER_OBJECTIVE_1.getNotes()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_DETAILS));

        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID, TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_NEW_STAKEHOLDER_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("notes", "Stakeholder Objective notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS,
                        TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), 2000)));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID)
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(EMPTY_SPECIFICATION_OBJECTIVE_ID))
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_OBJECTIVE_ID)))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, "specificationObjective"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId())))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, "specificationObjective"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("stakeholder", is(TEST_STAKEHOLDER_OBJECTIVE_1.getStakeholder()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("specificationObjective", is(TEST_STAKEHOLDER_OBJECTIVE_1.getSpecificationObjective()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("notes", is(TEST_STAKEHOLDER_OBJECTIVE_1.getNotes()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER_OBJECTIVE, hasProperty("priority", is(TEST_STAKEHOLDER_OBJECTIVE_1.getPriority()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID, TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())
                        .param("notes", "Updated notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS,
                        TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_2.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_2.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_2.getId(), TEST_STAKEHOLDER_2.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_2.getId(), TEST_STAKEHOLDER_2.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, EMPTY_SPECIFICATION_OBJECTIVE_ID, TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID, TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())));
    }
}