package com.soam.specificationobjective;

import com.soam.Util;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import com.soam.web.specificationobjective.SpecificationObjectiveController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationObjectiveController.class)
class SpecificationObjectiveControllerTest {
    private static Specification TEST_SPECIFICATION = new Specification();
    private static SpecificationObjective TEST_SPECIFICATION_OBJECTIVE = new SpecificationObjective();

    private static final int EMPTY_SPECIFICATION_ID = 99;
    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 999;

    private static String URL_VIEW_SPECIFICATION_OBJECTIVE_LIST = "/specification/{specificationId}/specificationObjective/list";
    private static String URL_VIEW_SPECIFICATION_OBJECTIVE = "/specification/{specificationId}/specificationObjective/{specificationObjectiveId}";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION.setId(10);
        TEST_SPECIFICATION.setName("Test Specification");

        TEST_SPECIFICATION_OBJECTIVE.setId(100);
        TEST_SPECIFICATION_OBJECTIVE.setSpecification(TEST_SPECIFICATION);
        TEST_SPECIFICATION_OBJECTIVE.setName("Test Spec 1");
        TEST_SPECIFICATION_OBJECTIVE.setDescription("desc");
        TEST_SPECIFICATION_OBJECTIVE.setNotes("notes");
        TEST_SPECIFICATION_OBJECTIVE.setPriority(lowPriority);

        TEST_SPECIFICATION.setSpecificationObjectives(Lists.newArrayList(TEST_SPECIFICATION_OBJECTIVE));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;

    @MockBean
    private SpecificationObjectiveRepository specificationObjectiveRepository;

    @BeforeEach
    void setup() {
        given(this.specificationRepository.findById(TEST_SPECIFICATION.getId())).willReturn(Optional.of(TEST_SPECIFICATION));

        given(this.specificationObjectiveRepository.findAll()).willReturn(Lists.newArrayList(TEST_SPECIFICATION_OBJECTIVE));
        given(this.specificationObjectiveRepository.findById(TEST_SPECIFICATION_OBJECTIVE.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE));
    }

    @Test
    void testListAllSpecificationObjectives() throws Exception {
        mockMvc.perform(get(URL_VIEW_SPECIFICATION_OBJECTIVE_LIST, TEST_SPECIFICATION.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_OBJECTIVES))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_LIST));
    }

    @Test
    void testViewSpecificationObjectiveDetails() throws Exception {
        mockMvc.perform(get(URL_VIEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(),  TEST_SPECIFICATION_OBJECTIVE.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_OBJECTIVE, hasProperty("name", is(TEST_SPECIFICATION_OBJECTIVE.getName()))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_OBJECTIVE_DETAILS));

        mockMvc.perform(get(URL_VIEW_SPECIFICATION_OBJECTIVE, EMPTY_SPECIFICATION_ID,
                        TEST_SPECIFICATION_OBJECTIVE.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_LIST));

        mockMvc.perform(get(URL_VIEW_SPECIFICATION_OBJECTIVE, TEST_SPECIFICATION.getId(),
                        EMPTY_SPECIFICATION_OBJECTIVE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION.getId())));
    }
}
