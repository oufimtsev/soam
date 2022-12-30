package com.soam.objective;

import com.soam.Util;
import com.soam.model.objective.Objective;
import com.soam.model.objective.ObjectiveRepository;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.web.objective.ObjectiveFormController;
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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ObjectiveFormController.class)
public class ObjectiveFormControllerTest {

    private static Specification TEST_SPECIFICATION = new Specification();
    private static Stakeholder TEST_STAKEHOLDER = new Stakeholder();
    private static Objective TEST_OBJECTIVE_1 = new Objective();
    private static Objective TEST_OBJECTIVE_2 = new Objective();
    private static Objective TEST_OBJECTIVE_3 = new Objective();

    private static final int EMPTY_OBJECTIVE_ID = 999;
    
    private static String URL_NEW_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/objective/new";
    private static String URL_EDIT_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/objective/{objectiveId}/edit";
    private static String URL_DELETE_OBJECTIVE = "/specification/{specificationId}/stakeholder/{stakeholderId}/objective/{objectiveId}/delete";
    
    private static String VIEW_EDIT_OBJECTIVE =  "objective/addUpdateObjective";



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

        TEST_STAKEHOLDER.setId(1);
        TEST_STAKEHOLDER.setName("Test Stakeholder");
        TEST_STAKEHOLDER.setSpecification(TEST_SPECIFICATION);


        TEST_OBJECTIVE_1.setId(100);
        TEST_OBJECTIVE_1.setStakeholder( TEST_STAKEHOLDER );
        TEST_OBJECTIVE_1.setName("Test Spec 1");
        TEST_OBJECTIVE_1.setDescription("desc");
        TEST_OBJECTIVE_1.setNotes("notes");
        TEST_OBJECTIVE_1.setPriority(lowPriority);




        TEST_OBJECTIVE_2.setId(200);
        TEST_OBJECTIVE_2.setStakeholder( TEST_STAKEHOLDER );
        TEST_OBJECTIVE_2.setName("Test Spec 2");
        TEST_OBJECTIVE_2.setDescription("desc");
        TEST_OBJECTIVE_2.setNotes("notes");
        TEST_OBJECTIVE_2.setPriority(highPriority);
        TEST_OBJECTIVE_2.setStakeholder( TEST_STAKEHOLDER );

        TEST_OBJECTIVE_3.setId(300);
        TEST_OBJECTIVE_3.setStakeholder( TEST_STAKEHOLDER );
        TEST_OBJECTIVE_3.setName("Spec 3");
        TEST_OBJECTIVE_3.setDescription("desc");
        TEST_OBJECTIVE_3.setNotes("notes");
        TEST_OBJECTIVE_3.setPriority(lowPriority);

        TEST_STAKEHOLDER.setObjectives( Lists.newArrayList(TEST_OBJECTIVE_1, TEST_OBJECTIVE_2, TEST_OBJECTIVE_3));
        TEST_SPECIFICATION.setStakeholders( Lists.newArrayList( TEST_STAKEHOLDER ));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;
    @MockBean
    private StakeholderRepository stakeholders;

    @MockBean
    private ObjectiveRepository objectives;

    @MockBean
    private ObjectiveTemplateRepository objectiveTemplates;

    @MockBean
    private PriorityRepository priorities;
    

    @BeforeEach
    void setup() {
        given( this.specificationRepository.findById(TEST_SPECIFICATION.getId())).willReturn(Optional.of( TEST_SPECIFICATION));
        given( this.stakeholders.findById(TEST_STAKEHOLDER.getId())).willReturn(Optional.of( TEST_STAKEHOLDER));

        given( this.objectives.findByName(TEST_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_OBJECTIVE_1));
        given( this.objectives.findByNameIgnoreCase(TEST_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_OBJECTIVE_1));
        given( this.objectives.findById(TEST_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_OBJECTIVE_1));

        given( this.objectives.findByName(TEST_OBJECTIVE_2.getName())).willReturn(Optional.of(TEST_OBJECTIVE_2));
        given( this.objectives.findByNameIgnoreCase(TEST_OBJECTIVE_2.getName())).willReturn(Optional.of(TEST_OBJECTIVE_2));
        given( this.objectives.findById(TEST_OBJECTIVE_2.getId())).willReturn(Optional.of(TEST_OBJECTIVE_2));

        given( this.objectives.findByName(TEST_OBJECTIVE_3.getName())).willReturn(Optional.of(TEST_OBJECTIVE_3));
        given( this.objectives.findByNameIgnoreCase(TEST_OBJECTIVE_3.getName())).willReturn(Optional.of(TEST_OBJECTIVE_3));
        given( this.objectives.findById(TEST_OBJECTIVE_3.getId())).willReturn(Optional.of(TEST_OBJECTIVE_3));


        given( this.objectives.findById(EMPTY_OBJECTIVE_ID)).willReturn(Optional.empty());
        

        given( this.objectives.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Lists.newArrayList(TEST_OBJECTIVE_2, TEST_OBJECTIVE_2)));

        given( this.objectives.findByNameStartsWithIgnoreCase(eq("Spec"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Lists.newArrayList(TEST_OBJECTIVE_3)));

    }

    @Test
    void testViewSpecificationDetails() throws Exception {
        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}/objective/{objectiveId}",
                        TEST_SPECIFICATION.getId(),  TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("objective"))
                .andExpect(model().attribute("objective", hasProperty("name", is(TEST_OBJECTIVE_1.getName()))))
                .andExpect(view().name("objective/objectiveDetails"));

        mockMvc.perform(get("/specification/{specificationId}/stakeholder/{stakeholderId}/objective/{objectiveId}", TEST_SPECIFICATION.getId(),
                        TEST_STAKEHOLDER.getId(), EMPTY_OBJECTIVE_ID ))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s",TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists("objective"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("objectiveTemplates"))
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));

        mockMvc.perform(get(URL_NEW_OBJECTIVE, TEST_SPECIFICATION.getId(), 42))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId()).param("name", "New stake")
                        .param("notes", "stake notes").param("description", "Description"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId()).param("name", TEST_OBJECTIVE_1.getName())
                        .param("notes", "spec notes").param("description", "Description"))
                        .andExpect(model().attributeHasErrors("objective"))
                          .andExpect(model().attributeHasFieldErrors("objective", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));

        mockMvc.perform(post(URL_NEW_OBJECTIVE, TEST_SPECIFICATION.getId() , TEST_STAKEHOLDER.getId()).param("name", "New spec")
                        .param("notes", "spec notes").param("description", ""))
                .andExpect(model().attributeHasErrors("objective"))
                .andExpect(model().attributeHasFieldErrorCode("objective", "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));
    }
    @Test
    void testInitUpdateObjectiveForm() throws Exception {
        Mockito.when(this.objectives.findById(TEST_OBJECTIVE_1.getId())).thenReturn(Optional.of(TEST_OBJECTIVE_1));

        mockMvc.perform(get(URL_EDIT_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists("objective"))
                .andExpect(model().attribute("objective", hasProperty("name", is(TEST_OBJECTIVE_1.getName()))))
                .andExpect(model().attribute("objective", hasProperty("description", is(TEST_OBJECTIVE_1.getDescription()))))
                .andExpect(model().attribute("objective", hasProperty("notes", is(TEST_OBJECTIVE_1.getNotes()))))
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));

        mockMvc.perform(get(URL_EDIT_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), EMPTY_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s",TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));


    }

    @Test
    void testProcessUpdateObjectiveFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId())
                        .param("name", "New Test Objective")
                        .param("notes", "notes here")
                        .param("description", "description there")
                        )
                    .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s/objective/%s",
                        TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId())));
        
    }

    @Test
    void testProcessUpdateObjectiveFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId())
                        .param("name", "New Test Objective")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("objective"))
                .andExpect(model().attributeHasFieldErrors("objective", "description"))
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));

        mockMvc.perform(post(URL_EDIT_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId())
                        .param("name", TEST_OBJECTIVE_1.getName() )
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("objective"))
                .andExpect(model().attributeHasFieldErrors("objective", "description"))
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));

        mockMvc.perform(post(URL_EDIT_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), EMPTY_OBJECTIVE_ID)
                        .param("name", TEST_OBJECTIVE_1.getName() )
                        .param("notes", "notes")
                        .param("description", "descr")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("objective"))
                .andExpect(model().attributeHasFieldErrors("objective", "name"))
                .andExpect(view().name(VIEW_EDIT_OBJECTIVE));
    }

    @Test
    void testProcessDeleteObjectiveSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_1.getId())
                        .param("name",TEST_OBJECTIVE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s",TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));

        mockMvc.perform(post(URL_DELETE_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), TEST_OBJECTIVE_3.getId())
                        .param("name",TEST_OBJECTIVE_3.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s",TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));

    }

    @Test
    void testProcessDeleteObjectiveError() throws Exception {
        mockMvc.perform(post(URL_DELETE_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId(), EMPTY_OBJECTIVE_ID)
                        .param("name",TEST_OBJECTIVE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect( flash().attributeExists(Util.DANGER))
                .andExpect(view().name(String.format("redirect:/specification/%s/stakeholder/%s", TEST_SPECIFICATION.getId(), TEST_STAKEHOLDER.getId())));

    }
}
