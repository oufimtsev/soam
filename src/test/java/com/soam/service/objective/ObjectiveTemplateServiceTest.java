package com.soam.service.objective;

import com.soam.config.SoamProperties;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.objective.ObjectiveTemplateRepository;
import com.soam.model.priority.PriorityType;
import com.soam.service.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ObjectiveTemplateServiceTest {
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static final int EMPTY_OBJECTIVE_TEMPLATE_ID = 9999;

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_OBJECTIVE_TEMPLATE_1.setId(1005);
        TEST_OBJECTIVE_TEMPLATE_1.setName("Test Objective Template 1");
        TEST_OBJECTIVE_TEMPLATE_1.setDescription("Test Objective Template 1 Description");
        TEST_OBJECTIVE_TEMPLATE_1.setPriority(lowPriority);
    }

    @Mock
    private ObjectiveTemplateRepository objectiveTemplateRepository;

    @Mock
    private SoamProperties soamProperties;

    @InjectMocks
    private ObjectiveTemplateService objectiveTemplateService;

    @Test
    void getByIdSuccessTest() {
        given(objectiveTemplateRepository.findById(TEST_OBJECTIVE_TEMPLATE_1.getId())).willReturn(Optional.of(TEST_OBJECTIVE_TEMPLATE_1));

        assertNotNull(objectiveTemplateService.getById(TEST_OBJECTIVE_TEMPLATE_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(objectiveTemplateRepository.findById(EMPTY_OBJECTIVE_TEMPLATE_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> objectiveTemplateService.getById(EMPTY_OBJECTIVE_TEMPLATE_ID));
    }

    @Test
    void findByPrefixTest() {
        given(objectiveTemplateRepository.findByNameStartsWithIgnoreCase(eq("Test"), any())).willReturn(List.of(TEST_OBJECTIVE_TEMPLATE_1));

        List<ObjectiveTemplate> result = objectiveTemplateService.findByPrefix("Test");
        assertEquals(List.of(TEST_OBJECTIVE_TEMPLATE_1), result);
    }

    @Test
    void findByNameTest() {
        given(objectiveTemplateRepository.findByNameIgnoreCase(eq(TEST_OBJECTIVE_TEMPLATE_1.getName()))).willReturn(Optional.of(TEST_OBJECTIVE_TEMPLATE_1));

        Optional<ObjectiveTemplate> maybeSpecificationTemplate = objectiveTemplateService.findByName(TEST_OBJECTIVE_TEMPLATE_1.getName());
        assertTrue(maybeSpecificationTemplate.isPresent());
        assertEquals(TEST_OBJECTIVE_TEMPLATE_1.getId(), maybeSpecificationTemplate.get().getId());
    }

    @Test
    void findAllTest() {
        given(objectiveTemplateRepository.findAll(any(Sort.class))).willReturn(List.of(TEST_OBJECTIVE_TEMPLATE_1));

        List<ObjectiveTemplate> result = objectiveTemplateService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void saveTest() {
        objectiveTemplateService.save(new ObjectiveTemplate());
        verify(objectiveTemplateRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        objectiveTemplateRepository.delete(new ObjectiveTemplate());
        verify(objectiveTemplateRepository, times(1)).delete(any());
    }
}
