package com.soam.web.specificationobjective;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.service.specificationobjective.SpecificationObjectiveService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

@WebMvcTest(SpecificationObjectiveController.class)
@Import(SoamConfiguration.class)
class SpecificationObjectiveControllerTest {
    private static final Specification TEST_SPECIFICATION = new Specification();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE = new SpecificationObjective();

    private static final int EMPTY_SPECIFICATION_ID = 99;
    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 999;

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
    private SpecificationService specificationService;

    @MockBean
    private SpecificationObjectiveService specificationObjectiveService;

    @BeforeEach
    void setup() {
        given(specificationService.getById(TEST_SPECIFICATION.getId())).willReturn(TEST_SPECIFICATION);
        given(specificationService.getById(EMPTY_SPECIFICATION_ID)).willThrow(new EntityNotFoundException("Specification", EMPTY_SPECIFICATION_ID));

        given(specificationObjectiveService.getById(TEST_SPECIFICATION_OBJECTIVE.getId())).willReturn(TEST_SPECIFICATION_OBJECTIVE);
        given(specificationObjectiveService.getById(EMPTY_SPECIFICATION_OBJECTIVE_ID)).willThrow(new EntityNotFoundException("Specification Objective", EMPTY_SPECIFICATION_OBJECTIVE_ID));
    }
}
