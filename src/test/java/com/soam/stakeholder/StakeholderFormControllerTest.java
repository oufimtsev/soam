package com.soam.stakeholder;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.web.ModelConstants;
import com.soam.web.ViewConstants;
import com.soam.web.stakeholder.StakeholderFormController;
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

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StakeholderFormController.class)
public class StakeholderFormControllerTest {
    private static Specification TEST_SPECIFICATION = new Specification();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE = new SpecificationObjective();
    private static Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static Stakeholder TEST_STAKEHOLDER_2 = new Stakeholder();
    private static Stakeholder TEST_STAKEHOLDER_3 = new Stakeholder();
    private static StakeholderObjective TEST_STAKEHOLDER_2_OBJECTIVE = new StakeholderObjective();

    private static final int EMPTY_STAKEHOLDER_ID = 999;
    
    private static String URL_NEW_STAKEHOLDER = "/specification/{specificationId}/stakeholder/new";
    private static String URL_EDIT_STAKEHOLDER = "/specification/{specificationId}/stakeholder/{stakeholderId}/edit";
    private static String URL_DELETE_STAKEHOLDER = "/specification/{specificationId}/stakeholder/{stakeholderId}/delete";
    
    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        PriorityType highPriority = new PriorityType();
        lowPriority.setName("High");
        lowPriority.setId(3);
        lowPriority.setSequence(3);

        TEST_SPECIFICATION.setId(1);
        TEST_SPECIFICATION.setName("Test Specification");

        TEST_SPECIFICATION_OBJECTIVE.setId(10);
        TEST_SPECIFICATION_OBJECTIVE.setName("Test Specification Objective");
        TEST_SPECIFICATION_OBJECTIVE.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE.setPriority(lowPriority);
        TEST_SPECIFICATION_OBJECTIVE.setSpecification(TEST_SPECIFICATION);

        TEST_STAKEHOLDER_1.setId(100);
        TEST_STAKEHOLDER_1.setSpecification( TEST_SPECIFICATION );
        TEST_STAKEHOLDER_1.setName("Test Spec 1");
        TEST_STAKEHOLDER_1.setDescription("desc");
        TEST_STAKEHOLDER_1.setNotes("notes");
        TEST_STAKEHOLDER_1.setPriority(lowPriority);
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));

        TEST_STAKEHOLDER_2.setId(200);
        TEST_STAKEHOLDER_2.setSpecification( TEST_SPECIFICATION );
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
        TEST_STAKEHOLDER_3.setSpecification( TEST_SPECIFICATION );
        TEST_STAKEHOLDER_3.setName("Spec 3");
        TEST_STAKEHOLDER_3.setDescription("desc");
        TEST_STAKEHOLDER_3.setNotes("notes");
        TEST_STAKEHOLDER_3.setPriority(lowPriority);

        TEST_SPECIFICATION.setStakeholders( Lists.newArrayList( TEST_STAKEHOLDER_1, TEST_STAKEHOLDER_1,  TEST_STAKEHOLDER_1 ));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;

    @MockBean
    private StakeholderRepository stakeholderRepository;

    @MockBean
    private StakeholderTemplateRepository stakeholderTemplateRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setup() {
        given( this.specificationRepository.findById(TEST_SPECIFICATION.getId())).willReturn(Optional.of( TEST_SPECIFICATION));

        given( this.stakeholderRepository.findByName(TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderRepository.findByNameIgnoreCase(TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderRepository.findById(TEST_STAKEHOLDER_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_1));

        given( this.stakeholderRepository.findByName(TEST_STAKEHOLDER_2.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_2));
        given( this.stakeholderRepository.findByNameIgnoreCase(TEST_STAKEHOLDER_2.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_2));
        given( this.stakeholderRepository.findById(TEST_STAKEHOLDER_2.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_2));

        given( this.stakeholderRepository.findByName(TEST_STAKEHOLDER_3.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_3));
        given( this.stakeholderRepository.findByNameIgnoreCase(TEST_STAKEHOLDER_3.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_3));
        given( this.stakeholderRepository.findById(TEST_STAKEHOLDER_3.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_3));


        given( this.stakeholderRepository.findById(EMPTY_STAKEHOLDER_ID)).willReturn(Optional.empty());
        

        given( this.stakeholderRepository.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Lists.newArrayList(TEST_STAKEHOLDER_2, TEST_STAKEHOLDER_2)));

        given( this.stakeholderRepository.findByNameStartsWithIgnoreCase(eq("Spec"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Lists.newArrayList(TEST_STAKEHOLDER_3)));

        given(stakeholderRepository.save(any())).will(invocation -> {
            Stakeholder stakeholder = invocation.getArgument(0);
            stakeholder.setId(400);
            return stakeholder;
        });
    }

    @Test
    void testViewSpecificationDetails() throws Exception {
        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}",
                        TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("name", is(TEST_STAKEHOLDER_1.getName()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_DETAILS));

        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}", TEST_SPECIFICATION.getId(),
                        EMPTY_STAKEHOLDER_ID ))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s",TEST_SPECIFICATION.getId())));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_NEW_STAKEHOLDER, 42))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/list"));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION.getId()).param("name", "New stake")
                        .param("notes", "stake notes").param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s", TEST_SPECIFICATION.getId(), 400)));
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION.getId()).param("name", TEST_STAKEHOLDER_1.getName())
                        .param("notes", "spec notes").param("description", "Description"))
                        .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "name"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_STAKEHOLDER, TEST_SPECIFICATION.getId()).param("name", "New spec")
                        .param("notes", "spec notes").param("description", ""))
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_STAKEHOLDER, "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));
    }
    @Test
    void testInitUpdateStakeholderForm() throws Exception {
        Mockito.when(this.stakeholderRepository.findById(TEST_STAKEHOLDER_1.getId())).thenReturn(Optional.of(TEST_STAKEHOLDER_1));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("name", is(TEST_STAKEHOLDER_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("description", is(TEST_STAKEHOLDER_1.getDescription()))))
                .andExpect(model().attribute(ModelConstants.ATTR_STAKEHOLDER, hasProperty("notes", is(TEST_STAKEHOLDER_1.getNotes()))))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION.getId(), EMPTY_STAKEHOLDER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s",TEST_SPECIFICATION.getId())));
    }

    @Test
    void testProcessUpdateStakeholderFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes here")
                        .param("description", "description there")
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s",
                        TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId())));
    }

    @Test
    void testProcessUpdateStakeholderFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("name", "New Test Stakeholder")
                        .param("notes", "notes")
                        .param("description", "")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "description"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("name", TEST_STAKEHOLDER_1.getName() )
                        .param("notes", "notes")
                        .param("description", "")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "description"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_STAKEHOLDER, TEST_SPECIFICATION.getId(), EMPTY_STAKEHOLDER_ID)
                        .param("name", TEST_STAKEHOLDER_1.getName() )
                        .param("notes", "notes")
                        .param("description", "descr")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER, "name"))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteStakeholderSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_1.getId())
                        .param("name",TEST_STAKEHOLDER_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s",TEST_SPECIFICATION.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_3.getId())
                        .param("name",TEST_STAKEHOLDER_3.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s",TEST_SPECIFICATION.getId())));
    }

    @Test
    void testProcessDeleteStakeholderError() throws Exception {
        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION.getId(), EMPTY_STAKEHOLDER_ID)
                        .param("name",TEST_STAKEHOLDER_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect( flash().attributeExists(Util.DANGER))
                .andExpect(view().name(String.format("redirect:/specification/%s", TEST_SPECIFICATION.getId())));

        mockMvc.perform(post(URL_DELETE_STAKEHOLDER, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_2.getId())
                        .param("name",TEST_STAKEHOLDER_2.getName()))
                .andExpect( flash().attributeExists(Util.SUB_FLASH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s", TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER_2.getId())));
    }
}
