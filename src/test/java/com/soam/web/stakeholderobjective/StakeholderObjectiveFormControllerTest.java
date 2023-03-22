package com.soam.web.stakeholderobjective;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specificationobjective.SpecificationObjectiveService;
import com.soam.service.stakeholder.StakeholderService;
import com.soam.service.stakeholderobjective.StakeholderObjectiveService;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StakeholderObjectiveFormController.class)
@Import(SoamConfiguration.class)
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
    private SpecificationService specificationService;

    @MockBean
    private StakeholderService stakeholderService;

    @MockBean
    private SpecificationObjectiveService specificationObjectiveService;

    @MockBean
    private StakeholderObjectiveService stakeholderObjectiveService;

    @MockBean
    private PriorityService priorityService;

    @Autowired
    private WebConversionService conversionService;

    @BeforeEach
    void setup() {
        given(specificationService.getById(TEST_SPECIFICATION_1.getId())).willReturn(TEST_SPECIFICATION_1);
        given(specificationService.getById(TEST_SPECIFICATION_2.getId())).willReturn(TEST_SPECIFICATION_2);
        given(specificationService.getById(EMPTY_SPECIFICATION_ID)).willThrow(new EntityNotFoundException("Specification", EMPTY_SPECIFICATION_ID));

        given(stakeholderService.getById(TEST_STAKEHOLDER_1.getId())).willReturn(TEST_STAKEHOLDER_1);
        given(stakeholderService.getById(TEST_STAKEHOLDER_2.getId())).willReturn(TEST_STAKEHOLDER_2);
        given(stakeholderService.getById(EMPTY_STAKEHOLDER_ID)).willThrow(new EntityNotFoundException("Stakeholder", EMPTY_SPECIFICATION_ID));

        given(specificationObjectiveService.getById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(TEST_SPECIFICATION_OBJECTIVE_1);
        given(specificationObjectiveService.getById(TEST_SPECIFICATION_OBJECTIVE_2.getId())).willReturn(TEST_SPECIFICATION_OBJECTIVE_2);
        given(specificationObjectiveService.getById(EMPTY_SPECIFICATION_OBJECTIVE_ID)).willThrow(new EntityNotFoundException("Specification Objective", EMPTY_SPECIFICATION_ID));

        given(stakeholderObjectiveService.getById(TEST_STAKEHOLDER_OBJECTIVE_1.getId())).willReturn(TEST_STAKEHOLDER_OBJECTIVE_1);
        given(stakeholderObjectiveService.getById(EMPTY_STAKEHOLDER_OBJECTIVE_ID)).willThrow(new EntityNotFoundException("Stakeholder Objective", EMPTY_SPECIFICATION_ID));
        given(stakeholderObjectiveService.existsForStakeholderAndSpecificationObjective(TEST_STAKEHOLDER_1, TEST_SPECIFICATION_OBJECTIVE_1)).willReturn(true);

        conversionService.addConverter(String.class, Stakeholder.class, source -> stakeholderService.getById(Integer.parseInt(source)));
        conversionService.addConverter(String.class, SpecificationObjective.class, source -> specificationObjectiveService.getById(Integer.parseInt(source)));

        given(stakeholderObjectiveService.save(any())).will(invocation -> {
            StakeholderObjective stakeholderObjective = invocation.getArgument(0);
            if (stakeholderObjective.getId() == null) {
                stakeholderObjective.setId(2000);
            }
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
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID, TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(get(URL_VIEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));
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
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(get(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("notes", "Stakeholder Objective notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_EDIT,
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
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID)
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId()))
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("stakeholder", String.valueOf(TEST_STAKEHOLDER_1.getId()))
                        .param("specificationObjective", String.valueOf(-1))
                        .param("collectionItemId", String.valueOf(-1)))
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
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID, TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())
                        .param("notes", "Updated notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_OBJECTIVE_EDIT,
                        TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId())));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUCCESS))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_2.getId(), TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_2.getId(), TEST_STAKEHOLDER_2.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId(), TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID, TEST_STAKEHOLDER_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId(), EMPTY_STAKEHOLDER_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_DEFAULT));
    }
}