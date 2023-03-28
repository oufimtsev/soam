package com.soam.web.specification;

import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.service.specification.SpecificationService;
import com.soam.service.stakeholder.StakeholderService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.TreeSet;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecificationTreeController.class)
class SpecificationTreeControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final StakeholderObjective TEST_STAKEHOLDER_OBJECTIVE_1 = new StakeholderObjective();

    static {
        TEST_SPECIFICATION_1.setId(10);
        TEST_SPECIFICATION_1.setName("Test Specification 1");
        TEST_SPECIFICATION_1.setDescription("Test Specification Description 1");
        TEST_SPECIFICATION_1.setNotes("Test Specification Notes 1");

        TEST_SPECIFICATION_OBJECTIVE_1.setId(100);
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Specification Objective 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("Test Specification Objective Description 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setNotes("Test Specification Objective Notes 1");

        TEST_STAKEHOLDER_1.setId(1000);
        TEST_STAKEHOLDER_1.setName("Test Stakeholder 1");
        TEST_STAKEHOLDER_1.setDescription("Test Stakeholder Description 1");
        TEST_STAKEHOLDER_1.setNotes("Test Stakeholder Notes 1");

        TEST_STAKEHOLDER_OBJECTIVE_1.setId(10000);
        TEST_STAKEHOLDER_OBJECTIVE_1.setSpecificationObjective(TEST_SPECIFICATION_OBJECTIVE_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setStakeholder(TEST_STAKEHOLDER_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setNotes("Test Stakeholder Objective Notes 1");

        TEST_SPECIFICATION_1.setSpecificationObjectives(List.of(TEST_SPECIFICATION_OBJECTIVE_1));
        TEST_SPECIFICATION_1.setStakeholders(List.of(TEST_STAKEHOLDER_1));
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_STAKEHOLDER_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_OBJECTIVE_1.setStakeholderObjectives(List.of(TEST_STAKEHOLDER_OBJECTIVE_1));
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()) {{ add(TEST_STAKEHOLDER_OBJECTIVE_1); }});
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationService specificationService;

    @MockBean
    private StakeholderService stakeholderService;

    @BeforeEach
    void setup() {
        given(specificationService.findAll()).willReturn(List.of(TEST_SPECIFICATION_1));
        given(specificationService.getById(TEST_SPECIFICATION_1.getId())).willReturn(TEST_SPECIFICATION_1);
        given(stakeholderService.getById(TEST_STAKEHOLDER_1.getId())).willReturn(TEST_STAKEHOLDER_1);
    }

    @Test
    void testGetSpecifications() throws Exception {
        mockMvc.perform(get("/tree/specification"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("specification")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_SPECIFICATION_1.getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_SPECIFICATION_1.getName())));
    }

    @Test
    void testGetSpecificationObjectives() throws Exception {
        mockMvc.perform(get("/tree/specification/{specificationId}/specificationObjective", TEST_SPECIFICATION_1.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("specificationObjective")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getId()))))
                .andExpect(jsonPath("$[0].specificationId", Matchers.equalTo(String.valueOf(TEST_SPECIFICATION_OBJECTIVE_1.getSpecification().getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_SPECIFICATION_OBJECTIVE_1.getName())));
    }

    @Test
    void testGetStakeholders() throws Exception {
        mockMvc.perform(get("/tree/specification/{specificationId}/stakeholder", TEST_SPECIFICATION_1.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("stakeholder")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_STAKEHOLDER_1.getId()))))
                .andExpect(jsonPath("$[0].specificationId", Matchers.equalTo(String.valueOf(TEST_STAKEHOLDER_1.getSpecification().getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_STAKEHOLDER_1.getName())));
    }

    @Test
    void testGetStakeholderObjectives() throws Exception {
        mockMvc.perform(get("/tree/stakeholder/{stakeholderId}/stakeholderObjective", TEST_STAKEHOLDER_1.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()", Matchers.equalTo(1)))
                .andExpect(jsonPath("$[0].type", Matchers.equalTo("stakeholderObjective")))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(String.valueOf(TEST_STAKEHOLDER_OBJECTIVE_1.getId()))))
                .andExpect(jsonPath("$[0].specificationId", Matchers.equalTo(String.valueOf(TEST_STAKEHOLDER_1.getSpecification().getId()))))
                .andExpect(jsonPath("$[0].stakeholderId", Matchers.equalTo(String.valueOf(TEST_STAKEHOLDER_1.getId()))))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(TEST_STAKEHOLDER_OBJECTIVE_1.getSpecificationObjective().getName())));
    }
}
