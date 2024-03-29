package com.soam.web.specificationobjective;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.service.EntityNotFoundException;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specificationobjective.SpecificationObjectiveService;
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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationObjectiveFormController.class)
@Import(SoamConfiguration.class)
class SpecificationObjectiveFormControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Specification TEST_SPECIFICATION_2 = new Specification();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_2 = new SpecificationObjective();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_3 = new SpecificationObjective();

    private static final int EMPTY_SPECIFICATION_ID = 99;
    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 999;

    private static final String URL_NEW_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/new";
    private static final String URL_EDIT_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/{specificationObjectiveId}/edit";
    private static final String URL_DELETE_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/{specificationObjectiveId}/delete";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        PriorityType highPriority = new PriorityType();
        lowPriority.setName("High");
        lowPriority.setId(3);
        lowPriority.setSequence(3);

        TEST_SPECIFICATION_1.setId(10);
        TEST_SPECIFICATION_1.setName("Test Specification 1");

        TEST_SPECIFICATION_2.setId(20);
        TEST_SPECIFICATION_2.setName("Test Specification 2");

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
        TEST_SPECIFICATION_OBJECTIVE_2.setPriority(highPriority);

        TEST_SPECIFICATION_OBJECTIVE_3.setId(300);
        TEST_SPECIFICATION_OBJECTIVE_3.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_OBJECTIVE_3.setName("Spec 3");
        TEST_SPECIFICATION_OBJECTIVE_3.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_3.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_3.setPriority(lowPriority);

        TEST_SPECIFICATION_1.setSpecificationObjectives(Lists.newArrayList(TEST_SPECIFICATION_OBJECTIVE_1, TEST_SPECIFICATION_OBJECTIVE_2, TEST_SPECIFICATION_OBJECTIVE_3));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationService specificationService;

    @MockBean
    private SpecificationObjectiveService specificationObjectiveService;

    @MockBean
    private ObjectiveTemplateService objectiveTemplateService;

    @MockBean
    private PriorityService priorityService;

    @Autowired
    private WebConversionService conversionService;

    @BeforeEach
    void setup() {
        given(specificationService.getById(TEST_SPECIFICATION_1.getId())).willReturn(TEST_SPECIFICATION_1);
        given(specificationService.getById(TEST_SPECIFICATION_2.getId())).willReturn(TEST_SPECIFICATION_2);
        given(specificationService.getById(EMPTY_SPECIFICATION_ID)).willThrow(new EntityNotFoundException("Specification", EMPTY_SPECIFICATION_ID));

        given(specificationObjectiveService.getById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(TEST_SPECIFICATION_OBJECTIVE_1);
        given(specificationObjectiveService.getById(EMPTY_SPECIFICATION_OBJECTIVE_ID)).willThrow(new EntityNotFoundException("Specification Objective", EMPTY_SPECIFICATION_OBJECTIVE_ID));
        given(specificationObjectiveService.findBySpecificationAndName(TEST_SPECIFICATION_1, TEST_SPECIFICATION_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));

        given(specificationObjectiveService.getById(TEST_SPECIFICATION_OBJECTIVE_3.getId())).willReturn(TEST_SPECIFICATION_OBJECTIVE_3);

        given(specificationObjectiveService.save(any())).will(invocation -> {
            SpecificationObjective specificationObjective = invocation.getArgument(0);
            if (specificationObjective.getId() == null) {
                specificationObjective.setId(400);
            }
            return specificationObjective;
        });

        conversionService.addConverter(String.class, Specification.class, source -> specificationService.getById(Integer.parseInt(source)));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_OBJECTIVE_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_NEW_SPECIFICATION_OBJECTIVE, EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New Test Specification Objective")
                        .param("notes", "Specification Objective notes")
                        .param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS, TEST_SPECIFICATION_1.getId(), 400)));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_2.getId()))
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName())
                        .param("notes", "Specification Objective notes")
                        .param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName())
                        .param("notes", "Specification Objective notes")
                        .param("description", "Description"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New Test Specification Objective")
                        .param("notes", "Specification Objective notes")
                        .param("description", ""))
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, hasProperty("name", is(TEST_SPECIFICATION_OBJECTIVE_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, hasProperty("description", is(TEST_SPECIFICATION_OBJECTIVE_1.getDescription()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, hasProperty("notes", is(TEST_SPECIFICATION_OBJECTIVE_1.getNotes()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, hasProperty("priority", is(TEST_SPECIFICATION_OBJECTIVE_1.getPriority()))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_SPECIFICATION_OBJECTIVE_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_SPECIFICATION_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", "New Test Specification Objective")
                        .param("notes", "notes here")
                        .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS,
                        TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())));
    }

    @Test
    void testProcessUpdateFormError() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_2.getId()))
                        .param("name", "New Test Specification Objective")
                        .param("notes", "notes here")
                        .param("description", "descr"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName())
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("specification", String.valueOf(EMPTY_SPECIFICATION_ID))
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName())
                        .param("notes", "notes")
                        .param("description", "descr"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_SPECIFICATION_OBJECTIVE_ID)
                        .param("specification", String.valueOf(TEST_SPECIFICATION_1.getId()))
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName())
                        .param("notes", "notes")
                        .param("description", "descr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_3.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_3.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION_1.getId())));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_2.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION_2.getId())));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, EMPTY_SPECIFICATION_ID, TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), EMPTY_SPECIFICATION_OBJECTIVE_ID)
                        .param("id", String.valueOf(EMPTY_SPECIFICATION_OBJECTIVE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION_1.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("id", String.valueOf(EMPTY_SPECIFICATION_OBJECTIVE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION_1.getId())));
    }
}
