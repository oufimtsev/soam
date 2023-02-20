package com.soam.service.specification;

import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specificationobjective.SpecificationObjective;
import com.soam.model.specificationobjective.SpecificationObjectiveRepository;
import com.soam.model.stakeholder.Stakeholder;
import com.soam.model.stakeholder.StakeholderRepository;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholderobjective.StakeholderObjective;
import com.soam.model.stakeholderobjective.StakeholderObjectiveComparator;
import com.soam.model.stakeholderobjective.StakeholderObjectiveRepository;
import com.soam.model.templatelink.TemplateLink;
import com.soam.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpecificationServiceTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
    private static final Stakeholder TEST_STAKEHOLDER_1 = new Stakeholder();
    private static final SpecificationObjective TEST_SPECIFICATION_OBJECTIVE_1 = new SpecificationObjective();
    private static final StakeholderObjective TEST_STAKEHOLDER_OBJECTIVE_1 = new StakeholderObjective();
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_1 = new SpecificationTemplate();
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE_1 = new StakeholderTemplate();
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();
    private static final int EMPTY_SPECIFICATION_ID = 99;

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

        TEST_SPECIFICATION_TEMPLATE_1.setId(15);
        TEST_SPECIFICATION_TEMPLATE_1.setName("Test Specification Template 1");
        TEST_SPECIFICATION_TEMPLATE_1.setDescription("Test Specification Template Description 1");

        TEST_STAKEHOLDER_TEMPLATE_1.setId(105);
        TEST_STAKEHOLDER_TEMPLATE_1.setName("Test Stakeholder Template 1");
        TEST_STAKEHOLDER_TEMPLATE_1.setDescription("Test Stakeholder Template Description 1");

        TEST_OBJECTIVE_TEMPLATE_1.setId(1005);
        TEST_OBJECTIVE_TEMPLATE_1.setName("Test Objective Template 1");
        TEST_OBJECTIVE_TEMPLATE_1.setDescription("Test Objective Template Description 1");

        TemplateLink templateLink = new TemplateLink();
        templateLink.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_1);
        templateLink.setStakeholderTemplate(TEST_STAKEHOLDER_TEMPLATE_1);
        templateLink.setObjectiveTemplate(TEST_OBJECTIVE_TEMPLATE_1);
        TEST_SPECIFICATION_TEMPLATE_1.setTemplateLinks(List.of(templateLink));
    }

    @Mock
    private SpecificationRepository specificationRepository;

    @Mock
    private StakeholderRepository stakeholderRepository;

    @Mock
    private SpecificationObjectiveRepository specificationObjectiveRepository;

    @Mock
    private StakeholderObjectiveRepository stakeholderObjectiveRepository;

    @InjectMocks
    private SpecificationService specificationService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(specificationService, "pageSize", 1);
    }

    @Test
    void getByIdSuccessTest() {
        given(specificationRepository.findById(TEST_SPECIFICATION_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_1));

        assertNotNull(specificationService.getById(TEST_SPECIFICATION_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(specificationRepository.findById(EMPTY_SPECIFICATION_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specificationService.getById(EMPTY_SPECIFICATION_ID));
    }

    @Test
    void findByPrefixTest() {
        given(specificationRepository.findByNameStartsWithIgnoreCase(eq("Test"), any())).willReturn(new PageImpl<>(List.of(TEST_SPECIFICATION_1)));

        Page<Specification> result = specificationService.findByPrefix("Test", 0);
        assertEquals(1, result.getTotalElements());
        assertEquals(TEST_SPECIFICATION_1.getId(), result.iterator().next().getId());
    }

    @Test
    void findByNameTest() {
        given(specificationRepository.findByNameIgnoreCase(eq(TEST_SPECIFICATION_1.getName()))).willReturn(Optional.of(TEST_SPECIFICATION_1));

        Optional<Specification> maybeSpecification = specificationService.findByName(TEST_SPECIFICATION_1.getName());
        assertTrue(maybeSpecification.isPresent());
        assertEquals(TEST_SPECIFICATION_1.getId(), maybeSpecification.get().getId());
    }

    @Test
    void findAllTest() {
        given(specificationRepository.findAll(any())).willReturn(List.of(TEST_SPECIFICATION_1));

        List<Specification> result = specificationService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void saveTest() {
        specificationService.save(new Specification());
        verify(specificationRepository, times(1)).save(any());
    }

    @Test
    void saveDeepCopyTest() {
        given(specificationObjectiveRepository.findBySpecificationAndNameIgnoreCase(any(), eq(TEST_SPECIFICATION_OBJECTIVE_1.getName())))
                .will(invocation -> Optional.of(invocation.<Specification>getArgument(0).getSpecificationObjectives().get(0)));
        given(specificationRepository.save(any())).will(invocation -> {
            Specification specification = invocation.getArgument(0);
            specification.setId(90);
            return specification;
        });

        Specification specification = new Specification();

        specification = specificationService.saveDeepCopy(TEST_SPECIFICATION_1, specification);
        Stakeholder stakeholder = specification.getStakeholders().get(0);
        SpecificationObjective specificationObjective = specification.getSpecificationObjectives().get(0);
        assertEquals(TEST_STAKEHOLDER_1.getName(), stakeholder.getName());
        assertEquals(TEST_SPECIFICATION_OBJECTIVE_1.getName(), specificationObjective.getName());
        assertSame(stakeholder, stakeholder.getStakeholderObjectives().first().getStakeholder());
        assertSame(specificationObjective, stakeholder.getStakeholderObjectives().first().getSpecificationObjective());

        verify(specificationRepository, times(1)).save(any());
    }

    @Test
    void saveFromTemplateTest() {
        Specification specification = new Specification();

        specification = specificationService.saveFromTemplate(TEST_SPECIFICATION_TEMPLATE_1, specification);
        Stakeholder stakeholder = specification.getStakeholders().get(0);
        assertEquals(TEST_STAKEHOLDER_TEMPLATE_1.getName(), stakeholder.getName());
        SpecificationObjective specificationObjective = specification.getSpecificationObjectives().get(0);
        assertEquals(TEST_OBJECTIVE_TEMPLATE_1.getName(), specificationObjective.getName());
        assertEquals(TEST_OBJECTIVE_TEMPLATE_1.getName(), stakeholder.getStakeholderObjectives().first().getSpecificationObjective().getName());

        verify(specificationRepository, times(1)).save(any());
    }

    @Test
    void deleteTest() {
        specificationService.delete(new Specification());
        verify(specificationRepository, times(1)).delete(any());
    }
}
