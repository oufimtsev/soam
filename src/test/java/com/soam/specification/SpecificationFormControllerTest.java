package com.soam.specification;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import com.soam.web.specification.SpecificationFormController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.TreeSet;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationFormController.class)
class SpecificationFormControllerTest {
    private static Specification TEST_SPECIFICATION_1 = new Specification();
    private static Specification TEST_SPECIFICATION_2 = new Specification();
    private static Specification TEST_SPECIFICATION_3 = new Specification();
    private static SpecificationTemplate TEST_SPECIFICATION_TEMPLATE = new SpecificationTemplate();

    private static final int EMPTY_SPECIFICATION_ID = 999;
    
    private static String URL_NEW_SPECIFICATION = "/specification/new";
    private static String URL_EDIT_SPECIFICATION = "/specification/{specificationId}/edit";
    private static String URL_DELETE_SPECIFICATION = "/specification/{specificationId}/delete";
    
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
        TEST_SPECIFICATION_2.setStakeholders(Lists.newArrayList(testStakeholder));
        SpecificationObjective testSpecificationObjective = new SpecificationObjective();
        TEST_SPECIFICATION_2.setSpecificationObjectives(Lists.newArrayList(testSpecificationObjective));
        testStakeholder.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));

        TEST_SPECIFICATION_3.setId(300);
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
    private SpecificationTemplateRepository specificationTemplateRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setup() {
        given( this.specificationRepository.findByName(TEST_SPECIFICATION_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_1));
        given( this.specificationRepository.findByNameIgnoreCase(TEST_SPECIFICATION_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_1));
        given( this.specificationRepository.findById(TEST_SPECIFICATION_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_1));

        given( this.specificationRepository.findByName(TEST_SPECIFICATION_2.getName())).willReturn(Optional.of(TEST_SPECIFICATION_2));
        given( this.specificationRepository.findByNameIgnoreCase(TEST_SPECIFICATION_2.getName())).willReturn(Optional.of(TEST_SPECIFICATION_2));
        given( this.specificationRepository.findById(TEST_SPECIFICATION_2.getId())).willReturn(Optional.of(TEST_SPECIFICATION_2));

        given( this.specificationRepository.findByName(TEST_SPECIFICATION_3.getName())).willReturn(Optional.of(TEST_SPECIFICATION_3));
        given( this.specificationRepository.findByNameIgnoreCase(TEST_SPECIFICATION_3.getName())).willReturn(Optional.of(TEST_SPECIFICATION_3));
        given( this.specificationRepository.findById(TEST_SPECIFICATION_3.getId())).willReturn(Optional.of(TEST_SPECIFICATION_3));

        given( this.specificationRepository.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_2, TEST_SPECIFICATION_2)));

        given( this.specificationRepository.findByNameStartsWithIgnoreCase(eq("Spec"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_3)));

        given( specificationTemplateRepository.findById(TEST_SPECIFICATION_TEMPLATE.getId()) ).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE));

        given(specificationRepository.save(any())).will(invocation -> {
            Specification specification = invocation.getArgument(0);
            specification.setId(400);
            return specification;
        });
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
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, 400)));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "srcSpecification")
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, 400)));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "templateDeepCopy")
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_TEMPLATE.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, 400)));
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
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
                        .param("collectionType", "srcSpecification")
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_NEW_SPECIFICATION).param("name", "New spec 3")
                        .param("notes", "spec notes").param("description", "desc")
                        .param("collectionType", "templateDeepCopy")
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testInitUpdateSpecificationForm() throws Exception {
        Mockito.when(this.specificationRepository.findById(TEST_SPECIFICATION_1.getId())).thenReturn(Optional.of(TEST_SPECIFICATION_1));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("name", is(TEST_SPECIFICATION_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("description", is(TEST_SPECIFICATION_1.getDescription()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("notes", is(TEST_SPECIFICATION_1.getNotes()))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION, EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_FIND_SPECIFICATION));
    }

    @Test
    void testProcessUpdateSpecificationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes here")
                        .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));
    }

    @Test
    void testProcessUpdateSpecificationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name", TEST_SPECIFICATION_1.getName() )
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION, EMPTY_SPECIFICATION_ID)
                        .param("name", TEST_SPECIFICATION_1.getName() )
                        .param("notes", "notes")
                        .param("description", "descr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteSpecificationSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_1.getId())
                        .param("name",TEST_SPECIFICATION_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_3.getId())
                        .param("name",TEST_SPECIFICATION_3.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));
    }

    @Test
    void testProcessDeleteSpecificationError() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION, EMPTY_SPECIFICATION_ID)
                        .param("name",TEST_SPECIFICATION_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION, TEST_SPECIFICATION_2.getId())
                        .param("name", TEST_SPECIFICATION_2.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_2.getId())));
    }
}
