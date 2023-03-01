package com.soam.web.stakeholder;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.stakeholder.StakeholderService;
import com.soam.service.stakeholder.StakeholderTemplateService;
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

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StakeholderFormController.class)
@Import(SoamConfiguration.class)
class StakeholderFormControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Specification TEST_SPECIFICATION_2 = new Specification();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE = new SpecificationObjective();
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final Stakeholder TEST_STAKEHOLDER_2 = new Stakeholder();
    private static final Stakeholder TEST_STAKEHOLDER_3 = new Stakeholder();
    private static final StakeholderObjective TEST_STAKEHOLDER_2_OBJECTIVE = new StakeholderObjective();

    private static final int EMPTY_SPECIFICATION_ID = 99;
    private static final int EMPTY_STAKEHOLDER_ID = 999;

    private static final String URL_NEW_STAKEHOLDER = "/specification/{specificationId}/stakeholder/new";
    private static final String URL_EDIT_STAKEHOLDER = "/specification/{specificationId}/stakeholder/{stakeholderId}/edit";
    private static final String URL_DELETE_STAKEHOLDER = "/specification/{specificationId}/stakeholder/{stakeholderId}/delete";
    
    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        PriorityType highPriority = new PriorityType();
        lowPriority.setName("High");
        lowPriority.setId(3);
        lowPriority.setSequence(3);

        TEST_SPECIFICATION_1.setId(1);
        TEST_SPECIFICATION_1.setName("Test Specification 1");
        TEST_SPECIFICATION_2.setId(2);
        TEST_SPECIFICATION_2.setName("Test Specification 2");

        TEST_SPECIFICATION_OBJECTIVE.setId(10);
        TEST_SPECIFICATION_OBJECTIVE.setName("Test Specification Objective");
        TEST_SPECIFICATION_OBJECTIVE.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE.setPriority(lowPriority);
        TEST_SPECIFICATION_OBJECTIVE.setSpecification(TEST_SPECIFICATION_1);

        TEST_STAKEHOLDER_1.setId(100);
        TEST_STAKEHOLDER_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_STAKEHOLDER_1.setName("Test Spec 1");
        TEST_STAKEHOLDER_1.setDescription("desc");
        TEST_STAKEHOLDER_1.setNotes("notes");
        TEST_STAKEHOLDER_1.setPriority(lowPriority);
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));

        TEST_STAKEHOLDER_2.setId(200);
        TEST_STAKEHOLDER_2.setSpecification(TEST_SPECIFICATION_1);
        TEST_STAKEHOLDER_2.setName("Test Spec 2");
        TEST_STAKEHOLDER_2.setDescription("desc");
        TEST_STAKEHOLDER_2.setNotes("notes");
        TEST_STAKEHOLDER_2.setPriority(highPriority);

        TEST_STAKEHOLDER_2_OBJECTIVE.setId(1000);
        TEST_STAKEHOLDER_2_OBJECTIVE.setStakeholder(TEST_STAKEHOLDER_2);
        TEST_STAKEHOLDER_2_OBJECTIVE.setSpecificationObjective(TEST_SPECIFICATION_OBJECTIVE);
        SortedSet<StakeholderObjective> stakeholderObjectives = new TreeSet<>(new StakeholderObjectiveComparator());
        stakeholderObjectives.add(TEST_STAKEHOLDER_2_OBJECTIVE);
        TEST_STAKEHOLDER_2.setStakeholderObjectives(stakeholderObjectives);

        TEST_STAKEHOLDER_3.setId(300);
        TEST_STAKEHOLDER_3.setSpecification(TEST_SPECIFICATION_1);
        TEST_STAKEHOLDER_3.setName("Spec 3");
        TEST_STAKEHOLDER_3.setDescription("desc");
        TEST_STAKEHOLDER_3.setNotes("notes");
        TEST_STAKEHOLDER_3.setPriority(lowPriority);

        TEST_SPECIFICATION_1.setStakeholders(Lists.newArrayList(TEST_STAKEHOLDER_1, TEST_STAKEHOLDER_1,  TEST_STAKEHOLDER_1));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationService specificationService;

    @MockBean
    private StakeholderService stakeholderService;

    @MockBean
    private StakeholderTemplateService stakeholderTemplateService;

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
        given(stakeholderService.getById(TEST_STAKEHOLDER_3.getId())).willReturn(TEST_STAKEHOLDER_3);
        given(stakeholderService.getById(EMPTY_STAKEHOLDER_ID)).willThrow(new EntityNotFoundException("Stakeholder", EMPTY_STAKEHOLDER_ID));

        given(stakeholderService.findBySpecificationAndName(TEST_SPECIFICATION_1, TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));

        given(stakeholderService.save(any())).will(invocation -> {
            Stakeholder stakeholder = invocation.getArgument(0);
            if (stakeholder.getId() == null) {
                stakeholder.setId(400);
            }
            return stakeholder;
        });

        conversionService.addConverter(String.class, Specification.class, source -> specificationService.getById(Integer.parseInt(source)));
    }

    @Test
    void testViewDetails() throws Exception {
        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}",
                        TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("name", is(TEST_STAKEHOLDER_1.getName()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_DETAILS));

        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}", EMPTY_SPECIFICATION_ID,
                        TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}", TEST_SPECIFICATION_1.getId(),
                        EMPTY_STAKEHOLDER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_NEW_STAKEHOLDER, EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New stake")
                        .param("notes", "stake notes")
                        .param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_1.getId(), 400)));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_2.getId()))
                        .param("name", "New stake")
                        .param("notes", "spec notes")
                        .param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "spec notes")
                        .param("description", "Description"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "name"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New spec")
                        .param("notes", "spec notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_STAKEHOLDER, "description", "NotBlank"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("name", is(TEST_STAKEHOLDER_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("description", is(TEST_STAKEHOLDER_1.getDescription()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("notes", is(TEST_STAKEHOLDER_1.getNotes()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes here")
                        .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS,
                        TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())));
    }

    @Test
    void testProcessUpdateFormError() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_2.getId()))
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "description"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "description"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId())
                        .param("specification", String.valueOf(EMPTY_SPECIFICATION_ID))
                        .param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "notes")
                        .param("description", "descr"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID)
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "notes")
                        .param("description", "descr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "name"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("id", String.valueOf(TEST_STAKEHOLDER_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_3.getId())
                        .param("id", String.valueOf(TEST_STAKEHOLDER_3.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION_2.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("id", String.valueOf(TEST_STAKEHOLDER_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_2.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, EMPTY_SPECIFICATION_ID, TEST_STAKEHOLDER_1.getId())
                        .param("id", String.valueOf(TEST_STAKEHOLDER_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), EMPTY_STAKEHOLDER_ID)
                        .param("id", String.valueOf(EMPTY_STAKEHOLDER_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("id", String.valueOf(EMPTY_STAKEHOLDER_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_2.getId())
                        .param("id", String.valueOf(TEST_STAKEHOLDER_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_DETAILS, TEST_SPECIFICATION_1.getId(), TEST_STAKEHOLDER_2.getId())));
    }
}
