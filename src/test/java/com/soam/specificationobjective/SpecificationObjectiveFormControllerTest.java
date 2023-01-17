package com.soam.specificationobjective;

import com.soam.Util;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.web.specificationobjective.SpecificationObjectiveFormController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
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

@WebMvcTest(SpecificationObjectiveFormController.class)
public class SpecificationObjectiveFormControllerTest {

    private static Specification TEST_SPECIFICATION = new Specification();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_2 = new SpecificationObjective();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_3 = new SpecificationObjective();

    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 999;
    
    private static String URL_VIEW_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/{specificationObjectiveId}";
    private static String URL_NEW_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/new";
    private static String URL_EDIT_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/{specificationObjectiveId}/edit";
    private static String URL_DELETE_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/{specificationObjectiveId}/delete";

    private static String VIEW_EDIT_SPECIFICATION_OBJECTIVE =  "specificationObjective/addUpdateSpecificationObjective";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        PriorityType highPriority = new PriorityType();
        lowPriority.setName("High");
        lowPriority.setId(3);
        lowPriority.setSequence(3);

        TEST_SPECIFICATION.setId(10);
        TEST_SPECIFICATION.setName("Test Specification");

        TEST_SPECIFICATION_OBJECTIVE_1.setId(100);
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification( TEST_SPECIFICATION );
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Spec 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_1.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_1.setPriority(lowPriority);

        TEST_SPECIFICATION_OBJECTIVE_2.setId(200);
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification( TEST_SPECIFICATION );
        TEST_SPECIFICATION_OBJECTIVE_2.setName("Test Spec 2");
        TEST_SPECIFICATION_OBJECTIVE_2.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_2.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_2.setPriority(highPriority);

        TEST_SPECIFICATION_OBJECTIVE_3.setId(300);
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification( TEST_SPECIFICATION );
        TEST_SPECIFICATION_OBJECTIVE_3.setName("Spec 3");
        TEST_SPECIFICATION_OBJECTIVE_3.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE_3.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE_3.setPriority(lowPriority);

        TEST_SPECIFICATION.setSpecificationObjectives( Lists.newArrayList(TEST_SPECIFICATION_OBJECTIVE_1, TEST_SPECIFICATION_OBJECTIVE_2, TEST_SPECIFICATION_OBJECTIVE_3));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;

    @MockBean
    private SpecificationObjectiveRepository specificationObjectives;

    @MockBean
    private ObjectiveTemplateRepository objectiveTemplates;

    @MockBean
    private PriorityRepository priorities;

    @Autowired
    private WebConversionService conversionService;

    @BeforeEach
    void setup() {
        given( this.specificationRepository.findById(TEST_SPECIFICATION.getId())).willReturn(Optional.of(TEST_SPECIFICATION));

        given( this.specificationObjectives.findBySpecificationAndNameIgnoreCase(TEST_SPECIFICATION, TEST_SPECIFICATION_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));
        given( this.specificationObjectives.findById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));

        given( this.specificationObjectives.findBySpecificationAndNameIgnoreCase(TEST_SPECIFICATION, TEST_SPECIFICATION_OBJECTIVE_2.getName())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_2));
        given( this.specificationObjectives.findById(TEST_SPECIFICATION_OBJECTIVE_2.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_2));

        given( this.specificationObjectives.findBySpecificationAndNameIgnoreCase(TEST_SPECIFICATION, TEST_SPECIFICATION_OBJECTIVE_3.getName())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_3));
        given( this.specificationObjectives.findById(TEST_SPECIFICATION_OBJECTIVE_3.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_3));

        given( this.specificationObjectives.findById(EMPTY_SPECIFICATION_OBJECTIVE_ID)).willReturn(Optional.empty());

        conversionService.addConverter(String.class, Specification.class, source -> specificationRepository.findById(Integer.parseInt(source)).orElse(null));
    }

    @Test
    void testViewSpecificationObjectiveDetails() throws Exception {
        mockMvc.perform(get(URL_VIEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(),  TEST_SPECIFICATION_OBJECTIVE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("specificationObjective"))
                .andExpect(model().attribute("specificationObjective", hasProperty("name", is(TEST_SPECIFICATION_OBJECTIVE_1.getName()))))
                .andExpect(view().name("specificationObjective/specificationObjectiveDetails"));

        mockMvc.perform(get(URL_VIEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(),
                        EMPTY_SPECIFICATION_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s", TEST_SPECIFICATION.getId())));
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists("specificationObjective"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("objectiveTemplates"))
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));

        mockMvc.perform(get(URL_NEW_SPECIFICATION_OBJECTIVE, 42))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId())
                        .param("specification", String.valueOf(TEST_SPECIFICATION.getId()))
                        .param("name", "New Test Specification Objective")
                        .param("notes", "Specification Objective notes")
                        .param("description", "Description"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId()).param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName())
                        .param("notes", "Specification Objective notes").param("description", "Description"))
                        .andExpect(model().attributeHasErrors("specificationObjective"))
                          .andExpect(model().attributeHasFieldErrors("specificationObjective", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));

        mockMvc.perform(post(URL_NEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId()).param("name", "New Test Specification Objective")
                        .param("notes", "Specification Objective notes").param("description", ""))
                .andExpect(model().attributeHasErrors("specificationObjective"))
                .andExpect(model().attributeHasFieldErrorCode("specificationObjective", "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));
    }

    @Test
    void testInitUpdateSpecificationObjectiveForm() throws Exception {
        Mockito.when(this.specificationObjectives.findById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).thenReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())).andExpect(status().isOk())
                .andExpect(model().attributeExists("specificationObjective"))
                .andExpect(model().attribute("specificationObjective", hasProperty("name", is(TEST_SPECIFICATION_OBJECTIVE_1.getName()))))
                .andExpect(model().attribute("specificationObjective", hasProperty("description", is(TEST_SPECIFICATION_OBJECTIVE_1.getDescription()))))
                .andExpect(model().attribute("specificationObjective", hasProperty("notes", is(TEST_SPECIFICATION_OBJECTIVE_1.getNotes()))))
                .andExpect(model().attribute("specificationObjective", hasProperty("priority", is(TEST_SPECIFICATION_OBJECTIVE_1.getPriority()))))
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));

        mockMvc.perform(get(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), EMPTY_SPECIFICATION_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s",TEST_SPECIFICATION.getId())));
    }

    @Test
    void testProcessUpdateSpecificationObjectiveFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("name", "New Test Specification Objective")
                        .param("notes", "notes here")
                        .param("description", "description there")
                        )
                    .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/specification/%s/specificationObjective/%s",
                        TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())));
    }

    @Test
    void testProcessUpdateSpecificationObjectiveFormHasErrors() throws Exception {
        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("name", "New Test Specification Objective")
                        .param("notes", "")
                        .param("description", "descr")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specificationObjective"))
                .andExpect(model().attributeHasFieldErrors("specificationObjective", "notes"))
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName() )
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specificationObjective"))
                .andExpect(model().attributeHasFieldErrors("specificationObjective", "description"))
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));

        mockMvc.perform(post(URL_EDIT_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), EMPTY_SPECIFICATION_OBJECTIVE_ID)
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName() )
                        .param("notes", "notes")
                        .param("description", "descr")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specificationObjective"))
                .andExpect(model().attributeHasFieldErrors("specificationObjective", "name"))
                .andExpect(view().name(VIEW_EDIT_SPECIFICATION_OBJECTIVE));
    }

    @Test
    void testProcessDeleteSpecificationObjectiveSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_1.getId())
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s", TEST_SPECIFICATION.getId())));

        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), TEST_SPECIFICATION_OBJECTIVE_3.getId())
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_3.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name(String.format("redirect:/specification/%s", TEST_SPECIFICATION.getId())));
    }

    @Test
    void testProcessDeleteSpecificationObjectiveError() throws Exception {
        mockMvc.perform(post(URL_DELETE_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(), EMPTY_SPECIFICATION_OBJECTIVE_ID)
                        .param("name", TEST_SPECIFICATION_OBJECTIVE_1.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(String.format("redirect:/specification/%s", TEST_SPECIFICATION.getId())));
    }
}
