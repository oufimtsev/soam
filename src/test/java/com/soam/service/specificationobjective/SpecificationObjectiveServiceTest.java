package com.soam.service.specificationobjective;

import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.service.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpecificationObjectiveServiceTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final int EMPTY_SPECIFICATION_OBJECTIVE_ID = 9999;

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_1.setId(10);
        TEST_SPECIFICATION_1.setName("Test Specification 1");
        TEST_SPECIFICATION_1.setDescription("Test Specification 1 Description");
        TEST_SPECIFICATION_1.setPriority(lowPriority);

        TEST_SPECIFICATION_OBJECTIVE_1.setId(1000);
        TEST_SPECIFICATION_OBJECTIVE_1.setName("Test Specification Objective 1");
        TEST_SPECIFICATION_OBJECTIVE_1.setDescription("Test Specification Objective 1 Description");
        TEST_SPECIFICATION_OBJECTIVE_1.setPriority(lowPriority);
        TEST_SPECIFICATION_OBJECTIVE_1.setSpecification(TEST_SPECIFICATION_1);
        TEST_SPECIFICATION_1.setSpecificationObjectives(List.of(TEST_SPECIFICATION_OBJECTIVE_1));
    }

    @Mock
    private SpecificationObjectiveRepository specificationObjectiveRepository;

    @InjectMocks
    private SpecificationObjectiveService specificationObjectiveService;

    @Test
    void getByIdSuccessTest() {
        given(specificationObjectiveRepository.findById(TEST_SPECIFICATION_OBJECTIVE_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));

        assertNotNull(specificationObjectiveService.getById(TEST_SPECIFICATION_OBJECTIVE_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(specificationObjectiveRepository.findById(EMPTY_SPECIFICATION_OBJECTIVE_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specificationObjectiveService.getById(EMPTY_SPECIFICATION_OBJECTIVE_ID));
    }

    @Test
    void findBySpecificationAndNameTest() {
        given(specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(TEST_SPECIFICATION_1, TEST_SPECIFICATION_OBJECTIVE_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_OBJECTIVE_1));

        Optional<SpecificationObjective> maybeSpecificationObjective = specificationObjectiveService.findBySpecificationAndName(TEST_SPECIFICATION_1, TEST_SPECIFICATION_OBJECTIVE_1.getName());
        assertTrue(maybeSpecificationObjective.isPresent());
        assertEquals(TEST_SPECIFICATION_OBJECTIVE_1.getId(), maybeSpecificationObjective.get().getId());
    }

    @Test
    void saveTest() {
        specificationObjectiveService.save(new SpecificationObjective());
        verify(specificationObjectiveRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        specificationObjectiveService.delete(new SpecificationObjective());
        verify(specificationObjectiveRepository, times(1)).delete(any());
    }
}
