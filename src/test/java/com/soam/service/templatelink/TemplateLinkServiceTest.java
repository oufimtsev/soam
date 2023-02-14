package com.soam.service.templatelink;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.model.templatelink.TemplateLinkRepository;
import com.soam.service.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TemplateLinkServiceTest {
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_1 = new SpecificationTemplate();
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE_1 = new StakeholderTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static final TemplateLink TEST_TEMPLATE_LINK_1 = new TemplateLink();
    private static final int EMPTY_TEMPLATE_LINK_ID = 99999;

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

        TEST_TEMPLATE_LINK_1.setId(10005);
        TEST_TEMPLATE_LINK_1.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_1);
        TEST_TEMPLATE_LINK_1.setStakeholderTemplate(TEST_STAKEHOLDER_TEMPLATE_1);
        TEST_TEMPLATE_LINK_1.setObjectiveTemplate(TEST_OBJECTIVE_TEMPLATE_1);
        TEST_SPECIFICATION_TEMPLATE_1.setTemplateLinks(List.of(TEST_TEMPLATE_LINK_1));
        TEST_STAKEHOLDER_TEMPLATE_1.setTemplateLinks(List.of(TEST_TEMPLATE_LINK_1));
        TEST_OBJECTIVE_TEMPLATE_1.setTemplateLinks(List.of(TEST_TEMPLATE_LINK_1));
    }

    @Mock
    private TemplateLinkRepository templateLinkRepository;

    @InjectMocks
    private TemplateLinkService templateLinkService;

    @Test
    void getByIdSuccessTest() {
        given(templateLinkRepository.findById(TEST_TEMPLATE_LINK_1.getId())).willReturn(Optional.of(TEST_TEMPLATE_LINK_1));

        assertNotNull(templateLinkService.getById(TEST_TEMPLATE_LINK_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(templateLinkRepository.findById(EMPTY_TEMPLATE_LINK_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> templateLinkService.getById(EMPTY_TEMPLATE_LINK_ID));
    }

    @Test
    void findBySpecificationTemplateAndStakeholderTemplateTest() {
        given(templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplate(eq(TEST_SPECIFICATION_TEMPLATE_1), eq(TEST_STAKEHOLDER_TEMPLATE_1), any())).willReturn(List.of(TEST_TEMPLATE_LINK_1));

        TemplateLink templateLink = templateLinkService.findBySpecificationTemplateAndStakeholderTemplate(TEST_SPECIFICATION_TEMPLATE_1, TEST_STAKEHOLDER_TEMPLATE_1).iterator().next();
        assertSame(TEST_TEMPLATE_LINK_1, templateLink);
    }

    @Test
    void findBySpecificationTemplateTest() {
        given(templateLinkRepository.findBySpecificationTemplate(eq(TEST_SPECIFICATION_TEMPLATE_1), any())).willReturn(List.of(TEST_TEMPLATE_LINK_1));

        TemplateLink templateLink = templateLinkService.findBySpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_1).iterator().next();
        assertSame(TEST_TEMPLATE_LINK_1, templateLink);
    }

    @Test
    void findByStakeholderTemplateTest() {
        given(templateLinkRepository.findByStakeholderTemplate(eq(TEST_STAKEHOLDER_TEMPLATE_1), any())).willReturn(List.of(TEST_TEMPLATE_LINK_1));

        TemplateLink templateLink = templateLinkService.findByStakeholderTemplate(TEST_STAKEHOLDER_TEMPLATE_1).iterator().next();
        assertSame(TEST_TEMPLATE_LINK_1, templateLink);
    }

    @Test
    void findAllTest() {
        given(templateLinkRepository.findAll(any())).willReturn(List.of(TEST_TEMPLATE_LINK_1));

        TemplateLink templateLink = templateLinkService.findAll().iterator().next();
        assertSame(TEST_TEMPLATE_LINK_1, templateLink);
    }

    @Test
    void findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplateTest() {
        given(templateLinkRepository.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(TEST_SPECIFICATION_TEMPLATE_1, TEST_STAKEHOLDER_TEMPLATE_1, TEST_OBJECTIVE_TEMPLATE_1)).willReturn(Optional.of(TEST_TEMPLATE_LINK_1));

        Optional<TemplateLink> maybeTemplateLink = templateLinkService.findBySpecificationTemplateAndStakeholderTemplateAndObjectiveTemplate(TEST_SPECIFICATION_TEMPLATE_1, TEST_STAKEHOLDER_TEMPLATE_1, TEST_OBJECTIVE_TEMPLATE_1);
        assertTrue(maybeTemplateLink.isPresent());
        assertEquals(TEST_TEMPLATE_LINK_1.getId(), maybeTemplateLink.get().getId());
    }

    @Test
    void saveTest() {
        templateLinkService.save(new TemplateLink());
        verify(templateLinkRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        templateLinkService.delete(new TemplateLink());
        verify(templateLinkRepository, times(1)).delete(any());
    }
}
