package com.soam.service.stakeholder;

import com.soam.config.SoamProperties;
import com.soam.model.priority.PriorityType;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
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
class StakeholderTemplateServiceTest {
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE_1 = new StakeholderTemplate();
    private static final int EMPTY_STAKEHOLDER_TEMPLATE_ID = 999;

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_STAKEHOLDER_TEMPLATE_1.setId(105);
        TEST_STAKEHOLDER_TEMPLATE_1.setName("Test Stakeholder Template 1");
        TEST_STAKEHOLDER_TEMPLATE_1.setDescription("Test Stakeholder Template 1 Description");
        TEST_STAKEHOLDER_TEMPLATE_1.setPriority(lowPriority);
    }

    @Mock
    private StakeholderTemplateRepository stakeholderTemplateRepository;

    @Mock
    private SoamProperties soamProperties;

    @InjectMocks
    private StakeholderTemplateService stakeholderTemplateService;

    @Test
    void getByIdSuccessTest() {
        given(stakeholderTemplateRepository.findById(TEST_STAKEHOLDER_TEMPLATE_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_TEMPLATE_1));

        assertNotNull(stakeholderTemplateService.getById(TEST_STAKEHOLDER_TEMPLATE_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(stakeholderTemplateRepository.findById(EMPTY_STAKEHOLDER_TEMPLATE_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stakeholderTemplateService.getById(EMPTY_STAKEHOLDER_TEMPLATE_ID));
    }

    @Test
    void findByPrefixTest() {
        given(stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(eq("Test"), any())).willReturn(List.of(TEST_STAKEHOLDER_TEMPLATE_1));

        List<StakeholderTemplate> result = stakeholderTemplateService.findByPrefix("Test");
        assertEquals(List.of(TEST_STAKEHOLDER_TEMPLATE_1), result);
    }

    @Test
    void findByNameTest() {
        given(stakeholderTemplateRepository.findByNameIgnoreCase(eq(TEST_STAKEHOLDER_TEMPLATE_1.getName()))).willReturn(Optional.of(TEST_STAKEHOLDER_TEMPLATE_1));

        Optional<StakeholderTemplate> maybeSpecificationTemplate = stakeholderTemplateService.findByName(TEST_STAKEHOLDER_TEMPLATE_1.getName());
        assertTrue(maybeSpecificationTemplate.isPresent());
        assertEquals(TEST_STAKEHOLDER_TEMPLATE_1.getId(), maybeSpecificationTemplate.get().getId());
    }

    @Test
    void findAllTest() {
        given(stakeholderTemplateRepository.findAll(any(Sort.class))).willReturn(List.of(TEST_STAKEHOLDER_TEMPLATE_1));

        List<StakeholderTemplate> result = stakeholderTemplateService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void saveTest() {
        stakeholderTemplateRepository.save(new StakeholderTemplate());
        verify(stakeholderTemplateRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        stakeholderTemplateService.delete(new StakeholderTemplate());
        verify(stakeholderTemplateRepository, times(1)).delete(any());
    }
}
