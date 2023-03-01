package com.soam.web.admin;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeleteSpecificationController.class)
@Import(SoamConfiguration.class)
class DeleteSpecificationControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final StakeholderObjective TEST_STAKEHOLDER_OBJECTIVE_1 = new StakeholderObjective();
    private static final int EMPTY_SPECIFICATION_ID = 999;

    private static final String URL_VIEW_SPECIFICATION_LIST = "/admin/deleteSpecification/list";
    private static final String URL_DELETE_CASCADE_SPECIFICATION = "/admin/deleteSpecification/{specificationId}/delete";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_1.setId(100);
        TEST_SPECIFICATION_1.setName("Test Specification 1");
        TEST_SPECIFICATION_1.setDescription("Test Specification 1 Description");
        TEST_SPECIFICATION_1.setNotes("Test Specification 1 Notes");
        TEST_SPECIFICATION_1.setPriority(lowPriority);

        TEST_STAKEHOLDER_1.setId(100);
        TEST_STAKEHOLDER_1.setName("Test Stakeholder 1");
        TEST_STAKEHOLDER_1.setDescription("Test Stakeholder 1 Description");
        TEST_STAKEHOLDER_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
        TEST_SPECIFICATION_1.setStakeholders(List.of(TEST_STAKEHOLDER_1));

        TEST_SPECIFICATION_OBJECTIVE_1.setId(1000);
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Specification Objective 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("Test Specification Objective 1 Description");
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_1.setSpecificationObjectives(List.of(TEST_SPECIFICATION_OBJECTIVE_1));

        TEST_STAKEHOLDER_OBJECTIVE_1.setId(10_000);
        TEST_STAKEHOLDER_OBJECTIVE_1.setStakeholder(TEST_STAKEHOLDER_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setSpecificationObjective(TEST_SPECIFICATION_OBJECTIVE_1);
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()) {{ add(TEST_STAKEHOLDER_OBJECTIVE_1); }});
        TEST_SPECIFICATION_OBJECTIVE_1.setStakeholderObjectives(List.of(TEST_STAKEHOLDER_OBJECTIVE_1));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationService specificationService;

    @BeforeEach
    void setup() {
        given(specificationService.getById(TEST_SPECIFICATION_1.getId())).willReturn(TEST_SPECIFICATION_1);
        given(specificationService.getById(EMPTY_SPECIFICATION_ID)).willThrow(new EntityNotFoundException("Specification", EMPTY_SPECIFICATION_ID));
    }

    @Test
    void testListAll() throws Exception {
        Page<Specification> specifications = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1));
        given(specificationService.findByPrefix(eq(""), anyInt())).willReturn(specifications);
        mockMvc.perform(get(URL_VIEW_SPECIFICATION_LIST).param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_PAGINATED))
                .andExpect(view().name(ViewConstants.VIEW_ADMIN_DELETE_SPECIFICATION));
    }

    @Test
    void testProcessDeleteCascadeSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_CASCADE_SPECIFICATION, TEST_SPECIFICATION_1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUCCESS))
                .andExpect(view().name(RedirectConstants.REDIRECT_ADMIN_DELETE_SPECIFICATION));
    }

    @Test
    void testProcessDeleteCascadeError() throws Exception {
        mockMvc.perform(post(URL_DELETE_CASCADE_SPECIFICATION, EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_ADMIN_DELETE_SPECIFICATION));
    }
}
