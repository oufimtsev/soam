package com.soam.service.stakeholderobjective;

import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.service.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StakeholderObjectiveServiceTest {
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final StakeholderObjective TEST_STAKEHOLDER_OBJECTIVE_1 = new StakeholderObjective();
    private static final int EMPTY_STAKEHOLDER_OBJECTIVE_ID = 99999;

    static {
        TEST_SPECIFICATION_OBJECTIVE_1.setId(1000);
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Specification Objective 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("Test Specification Objective 1 Description");

        TEST_STAKEHOLDER_OBJECTIVE_1.setId(10_000);
        TEST_STAKEHOLDER_OBJECTIVE_1.setStakeholder(TEST_STAKEHOLDER_1);
        TEST_STAKEHOLDER_OBJECTIVE_1.setSpecificationObjective(TEST_SPECIFICATION_OBJECTIVE_1);
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()) {{ add(TEST_STAKEHOLDER_OBJECTIVE_1); }});

        TEST_SPECIFICATION_OBJECTIVE_1.setStakeholderObjectives(List.of(TEST_STAKEHOLDER_OBJECTIVE_1));
    }

    @Mock
    private StakeholderObjectiveRepository stakeholderObjectiveRepository;

    @InjectMocks
    private StakeholderObjectiveService stakeholderObjectiveService;

    @Test
    void getByIdSuccessTest() {
        given(stakeholderObjectiveRepository.findById(TEST_STAKEHOLDER_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_OBJECTIVE_1));

        assertNotNull(stakeholderObjectiveService.getById(TEST_STAKEHOLDER_OBJECTIVE_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(stakeholderObjectiveRepository.findById(EMPTY_STAKEHOLDER_OBJECTIVE_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stakeholderObjectiveService.getById(EMPTY_STAKEHOLDER_OBJECTIVE_ID));
    }

    @Test
    void existsByStakeholderAndSpecificationObjectiveTest() {
        given(stakeholderObjectiveRepository.existsByStakeholderAndSpecificationObjective(TEST_STAKEHOLDER_1, TEST_SPECIFICATION_OBJECTIVE_1)).willReturn(true);

        assertTrue(stakeholderObjectiveService.existsForStakeholderAndSpecificationObjective(TEST_STAKEHOLDER_1, TEST_SPECIFICATION_OBJECTIVE_1));
    }

    @Test
    void saveTest() {
        stakeholderObjectiveService.save(new StakeholderObjective());
        verify(stakeholderObjectiveRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        stakeholderObjectiveService.delete(new StakeholderObjective());
        verify(stakeholderObjectiveRepository, times(1)).delete(any());
    }
}
