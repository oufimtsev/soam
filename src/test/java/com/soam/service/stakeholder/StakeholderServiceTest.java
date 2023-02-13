package com.soam.service.stakeholder;

import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
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
class StakeholderServiceTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final int EMPTY_STAKEHOLDER_ID = 999;

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_1.setId(10);
        TEST_SPECIFICATION_1.setName("Test Specification 1");
        TEST_SPECIFICATION_1.setDescription("Test Specification 1 Description");
        TEST_SPECIFICATION_1.setPriority(lowPriority);

        TEST_STAKEHOLDER_1.setId(100);
        TEST_STAKEHOLDER_1.setName("Test Stakeholder 1");
        TEST_STAKEHOLDER_1.setDescription("Test Stakeholder 1 Description");
        TEST_STAKEHOLDER_1.setStakeholderObjectives(new TreeSet<>(new StakeholderObjectiveComparator()));
        TEST_STAKEHOLDER_1.setPriority(lowPriority);
        TEST_STAKEHOLDER_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_1.setStakeholders(List.of(TEST_STAKEHOLDER_1));
    }

    @Mock
    private StakeholderRepository stakeholderRepository;

    @InjectMocks
    private StakeholderService stakeholderService;

    @Test
    void getByIdSuccessTest() {
        given(stakeholderRepository.findById(TEST_STAKEHOLDER_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_1));

        assertNotNull(stakeholderService.getById(TEST_STAKEHOLDER_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(stakeholderRepository.findById(EMPTY_STAKEHOLDER_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stakeholderService.getById(EMPTY_STAKEHOLDER_ID));
    }

    @Test
    void findBySpecificationAndNameTest() {
        given(stakeholderRepository.findBySpecificationAndNameIgnoreCase(TEST_SPECIFICATION_1, TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));

        Optional<Stakeholder> maybeStakeholder = stakeholderService.findBySpecificationAndName(TEST_SPECIFICATION_1, TEST_STAKEHOLDER_1.getName());
        assertTrue(maybeStakeholder.isPresent());
        assertEquals(TEST_STAKEHOLDER_1.getId(), maybeStakeholder.get().getId());
    }

    @Test
    void saveTest() {
        stakeholderService.save(new Stakeholder());
        verify(stakeholderRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        stakeholderService.delete(new Stakeholder());
        verify(stakeholderRepository, times(1)).delete(any());
    }
}
