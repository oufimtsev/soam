package com.soam.web.specification;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.TreeSet;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationFormController.class)
@Import(SoamConfiguration.class)
class SpecificationFormControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Specification TEST_SPECIFICATION_2 = new Specification();
    private static final Specification TEST_SPECIFICATION_2_COPY = new Specification();
    private static final Specification TEST_SPECIFICATION_3 = new Specification();
    private static final Specification TEST_SPECIFICATION_4_FROM_TEMPLATE = new Specification();
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE = new SpecificationTemplate();

    private static final int EMPTY_SPECIFICATION_ID = 999;
    private static final int EMPTY_SPECIFICATION_TEMPLATE_ID = 9999;

    private static final String URL_NEW_SPECIFICATION = "/specification/new";
    private static final String URL_EDIT_SPECIFICATION = "/specification/{specificationId}/edit";
    private static final String URL_DELETE_SPECIFICATION = "/specification/{specificationId}/delete";
    
    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        PriorityType highPriority = new PriorityType();
        lowPriority.setName("High");
        lowPriority.setId(3);
        lowPriority.setSequence(3);

        TEST_SPECIFICATION_1.setId(100);
        TEST_SPECIFICATION_1.setName("Test Spec 1");
        TEST_SPECIFICATION_1.setDescription("desc");
        TEST_SPECIFICATION_1.setNotes("notes");
        TEST_SPECIFICATION_1.setPriority(lowPriority);
        TEST_SPECIFICATION_1.setStakeholders(new ArrayList<>());

        TEST_SPECIFICATION_2.setId(200);
        TEST_SPECIFICATION_2.setName("Test Spec 2");
        TEST_SPECIFICATION_2.setDescription("desc");
        TEST_SPECIFICATION_2.setNotes("notes");
        TEST_SPECIFICATION_2.setPriority(highPriority);

        Stakeholder testStakeholder = new Stakeholder();
        testStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
        TEST_SPECIFICATION_2.setStakeholders(Lists.newArrayList(testStakeholder));
        SpecificationObjective testSpecificationObjective = new SpecificationObjective();
        TEST_SPECIFICATION_2.setSpecificationObjectives(Lists.newArrayList(testSpecificationObjective));
        testStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));

        TEST_SPECIFICATION_2_COPY.setId(300);
        TEST_SPECIFICATION_2_COPY.setName("Test Spec 2 Copy");
        TEST_SPECIFICATION_2_COPY.setDescription("desc");
        TEST_SPECIFICATION_2_COPY.setNotes("notes");
        TEST_SPECIFICATION_2_COPY.setPriority(highPriority);

        Stakeholder testStakeholderCopy = new Stakeholder();
        testStakeholderCopy.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
        TEST_SPECIFICATION_2_COPY.setStakeholders(Lists.newArrayList(testStakeholderCopy));
        SpecificationObjective testSpecificationObjectiveCopy = new SpecificationObjective();
        TEST_SPECIFICATION_2_COPY.setSpecificationObjectives(Lists.newArrayList(testSpecificationObjectiveCopy));
        testStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));

        TEST_SPECIFICATION_3.setId(400);
        TEST_SPECIFICATION_3.setName("Spec 3");
        TEST_SPECIFICATION_3.setDescription("desc");
        TEST_SPECIFICATION_3.setNotes("notes");
        TEST_SPECIFICATION_3.setPriority(lowPriority);

        TEST_SPECIFICATION_TEMPLATE.setId(1000);
        TEST_SPECIFICATION_TEMPLATE.setName("Test Specification Template");
        TEST_SPECIFICATION_TEMPLATE.setDescription("desc");
        TEST_SPECIFICATION_TEMPLATE.setNotes("Test Specification Template");
        TEST_SPECIFICATION_TEMPLATE.setPriority(lowPriority);
        TEST_SPECIFICATION_TEMPLATE.setTemplateLinks(new LinkedList<>());

        TEST_SPECIFICATION_4_FROM_TEMPLATE.setId(500);
        TEST_SPECIFICATION_4_FROM_TEMPLATE.setName("Test Specification Template");
        TEST_SPECIFICATION_4_FROM_TEMPLATE.setDescription("desc");
        TEST_SPECIFICATION_4_FROM_TEMPLATE.setNotes("Test Specification Template");
        TEST_SPECIFICATION_4_FROM_TEMPLATE.setPriority(lowPriority);

        Stakeholder testStakeholderFromTemplate = new Stakeholder();
        testStakeholderFromTemplate.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
        TEST_SPECIFICATION_4_FROM_TEMPLATE.setStakeholders(Lists.newArrayList(testStakeholderFromTemplate));
        SpecificationObjective testSpecificationObjectiveFromTemplate = new SpecificationObjective();
        TEST_SPECIFICATION_4_FROM_TEMPLATE.setSpecificationObjectives(Lists.newArrayList(testSpecificationObjectiveFromTemplate));
        testStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationService specificationService;

    @MockBean
    private SpecificationTemplateService specificationTemplateService;

    @MockBean
    private PriorityService priorityService;

    @BeforeEach
    void setup() {
        given(specificationTemplateService.getById(TEST_SPECIFICATION_TEMPLATE.getId())).willReturn(TEST_SPECIFICATION_TEMPLATE);
        given(specificationTemplateService.getById(EMPTY_SPECIFICATION_TEMPLATE_ID)).willThrow(new EntityNotFoundException("Specification Template", EMPTY_SPECIFICATION_TEMPLATE_ID));

        given(specificationService.getById(TEST_SPECIFICATION_1.getId())).willReturn(TEST_SPECIFICATION_1);
        given(specificationService.getById(TEST_SPECIFICATION_2.getId())).willReturn(TEST_SPECIFICATION_2);
        given(specificationService.getById(TEST_SPECIFICATION_3.getId())).willReturn(TEST_SPECIFICATION_3);
        given(specificationService.getById(EMPTY_SPECIFICATION_ID)).willThrow(new EntityNotFoundException("Specification", EMPTY_SPECIFICATION_ID));

        given(specificationService.findByName(TEST_SPECIFICATION_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_1));

        given(specificationService.save(any())).will(invocation -> {
            Specification specification = invocation.getArgument(0);
            if (specification.getId() == null) {
                specification.setId(500);
            }
            return specification;
        });

        given(specificationService.saveDeepCopy(eq(TEST_SPECIFICATION_2), any())).will(invocation -> TEST_SPECIFICATION_2_COPY);

        given(specificationService.saveFromTemplate(eq(TEST_SPECIFICATION_TEMPLATE), any())).will(invocation -> TEST_SPECIFICATION_4_FROM_TEMPLATE);
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_SPECIFICATION))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, 500)));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", SpecificationFormController.CREATE_MODE_COPY_SPECIFICATION)
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUCCESS))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_2_COPY.getId())));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "Test Specification Template")
                        .param("notes", "Test Specification Template").param("description", "desc")
                        .param("collectionType", SpecificationFormController.CREATE_MODE_FROM_TEMPLATE)
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_TEMPLATE.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUCCESS))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_4_FROM_TEMPLATE.getId())));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", TEST_SPECIFICATION_1.getName())
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec 3")
                        .param("notes", "spec notes").param("description", "")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_SPECIFICATION, "description", "NotBlank"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec 3")
                        .param("notes", "spec notes").param("description", "desc")
                        .param("collectionType", SpecificationFormController.CREATE_MODE_COPY_SPECIFICATION)
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec 3")
                        .param("notes", "spec notes").param("description", "desc")
                        .param("collectionType", SpecificationFormController.CREATE_MODE_FROM_TEMPLATE)
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_TEMPLATE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("name", is(TEST_SPECIFICATION_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("description", is(TEST_SPECIFICATION_1.getDescription()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("notes", is(TEST_SPECIFICATION_1.getNotes()))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION, EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes here")
                        .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));
    }

    @Test
    void testProcessUpdateFormError() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name", TEST_SPECIFICATION_1.getName())
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION, EMPTY_SPECIFICATION_ID)
                        .param("name", TEST_SPECIFICATION_1.getName())
                        .param("notes", "notes")
                        .param("description", "descr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_3.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_3.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION, EMPTY_SPECIFICATION_ID)
                        .param("id", String.valueOf(EMPTY_SPECIFICATION_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("id", String.valueOf(EMPTY_SPECIFICATION_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_2.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_2.getId())));
    }
}
