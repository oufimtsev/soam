package com.soam.service.specification;

import com.soam.config.SoamProperties;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.service.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class SpecificationTemplateServiceTest {
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_1 = new SpecificationTemplate();
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE_1 = new StakeholderTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static final TemplateLink TEST_TEMPLATE_LINK_1 = new TemplateLink();
    private static final int EMPTY_SPECIFICATION_TEMPLATE_ID = 99;

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_TEMPLATE_1.setId(15);
        TEST_SPECIFICATION_TEMPLATE_1.setName("Test Specification Template 1");
        TEST_SPECIFICATION_TEMPLATE_1.setDescription("Test Specification Template 1 Description");
        TEST_SPECIFICATION_TEMPLATE_1.setPriority(lowPriority);

        TEST_STAKEHOLDER_TEMPLATE_1.setId(105);
        TEST_STAKEHOLDER_TEMPLATE_1.setName("Test Stakeholder Template 1");
        TEST_STAKEHOLDER_TEMPLATE_1.setDescription("Test Stakeholder Template 1 Description");
        TEST_STAKEHOLDER_TEMPLATE_1.setPriority(lowPriority);

        TEST_OBJECTIVE_TEMPLATE_1.setId(1005);
        TEST_OBJECTIVE_TEMPLATE_1.setName("Test Objective Template 1");
        TEST_OBJECTIVE_TEMPLATE_1.setDescription("Test Objective Template 1 Description");
        TEST_OBJECTIVE_TEMPLATE_1.setPriority(lowPriority);

        TEST_TEMPLATE_LINK_1.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_1);
        TEST_TEMPLATE_LINK_1.setStakeholderTemplate(TEST_STAKEHOLDER_TEMPLATE_1);
        TEST_TEMPLATE_LINK_1.setObjectiveTemplate(TEST_OBJECTIVE_TEMPLATE_1);
        TEST_SPECIFICATION_TEMPLATE_1.setTemplateLinks(List.of(TEST_TEMPLATE_LINK_1));
        TEST_STAKEHOLDER_TEMPLATE_1.setTemplateLinks(List.of(TEST_TEMPLATE_LINK_1));
        TEST_OBJECTIVE_TEMPLATE_1.setTemplateLinks(List.of(TEST_TEMPLATE_LINK_1));
    }

    @Mock
    private SpecificationTemplateRepository specificationTemplateRepository;

    @Mock
    private TemplateLinkRepository templateLinkRepository;

    @Mock
    private SoamProperties soamProperties;

    @InjectMocks
    private SpecificationTemplateService specificationTemplateService;

    @Test
    void getByIdSuccessTest() {
        given(specificationTemplateRepository.findById(TEST_SPECIFICATION_TEMPLATE_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));

        assertNotNull(specificationTemplateService.getById(TEST_SPECIFICATION_TEMPLATE_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(specificationTemplateRepository.findById(EMPTY_SPECIFICATION_TEMPLATE_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specificationTemplateService.getById(EMPTY_SPECIFICATION_TEMPLATE_ID));
    }

    @Test
    void findByPrefixTest() {
        given(specificationTemplateRepository.findByNameStartsWithIgnoreCase(eq("Test"), any())).willReturn(List.of(TEST_SPECIFICATION_TEMPLATE_1));

        List<SpecificationTemplate> result = specificationTemplateService.findByPrefix("Test");
        assertEquals(List.of(TEST_SPECIFICATION_TEMPLATE_1), result);
    }

    @Test
    void findByNameTest() {
        given(specificationTemplateRepository.findByNameIgnoreCase(eq(TEST_SPECIFICATION_TEMPLATE_1.getName()))).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));

        Optional<SpecificationTemplate> maybeSpecificationTemplate = specificationTemplateService.findByName(TEST_SPECIFICATION_TEMPLATE_1.getName());
        assertTrue(maybeSpecificationTemplate.isPresent());
        assertEquals(TEST_SPECIFICATION_TEMPLATE_1.getId(), maybeSpecificationTemplate.get().getId());
    }

    @Test
    void findAllTest() {
        given(specificationTemplateRepository.findAll(any(Sort.class))).willReturn(List.of(TEST_SPECIFICATION_TEMPLATE_1));

        List<SpecificationTemplate> result = specificationTemplateService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void saveTest() {
        specificationTemplateService.save(new SpecificationTemplate());
        verify(specificationTemplateRepository, times(1)).save(any());
    }

    @Test
    void saveDeepCopyTest() {
        given(specificationTemplateRepository.save(any())).will(invocation -> {
            SpecificationTemplate specificationTemplate = invocation.getArgument(0);
            specificationTemplate.setId(95);
            return specificationTemplate;
        });

        SpecificationTemplate specificationTemplate = new SpecificationTemplate();
        specificationTemplate.setName("Test New Specification Template");

        specificationTemplate = specificationTemplateService.saveDeepCopy(TEST_SPECIFICATION_TEMPLATE_1, specificationTemplate);

        TemplateLink templateLink = specificationTemplate.getTemplateLinks().get(0);
        assertSame(TEST_STAKEHOLDER_TEMPLATE_1, templateLink.getStakeholderTemplate());
        assertSame(TEST_OBJECTIVE_TEMPLATE_1, templateLink.getObjectiveTemplate());

        verify(specificationTemplateRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        specificationTemplateService.delete(new SpecificationTemplate());
        verify(specificationTemplateRepository, times(1)).delete(any());
    }
}
