package com.soam.service.soamenum;

import com.soam.model.priority.PriorityType;
import com.soam.model.soamenum.SoamEnum;
import com.soam.model.soamenum.SoamEnumRepository;
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
class SoamEnumServiceTest {
    private final static PriorityType TEST_PRIORITY_1 = new PriorityType();
    private final static PriorityType TEST_PRIORITY_2 = new PriorityType();
    private static final int EMPTY_SOAM_ENUM_ID = 99;

    static {
        TEST_PRIORITY_1.setId(1);
        TEST_PRIORITY_1.setName("Test Priority 1");
        TEST_PRIORITY_1.setSequence(10);
        TEST_PRIORITY_2.setId(2);
        TEST_PRIORITY_2.setName("Test Priority 2");
        TEST_PRIORITY_2.setSequence(20);
    }

    @Mock
    private SoamEnumRepository soamEnumRepository;

    @InjectMocks
    private SoamEnumService soamEnumService;

    @Test
    void getByIdSuccessTest() {
        given(soamEnumRepository.findById(TEST_PRIORITY_1.getId())).willReturn(Optional.of(TEST_PRIORITY_1));

        assertNotNull(soamEnumService.getById(TEST_PRIORITY_1.getId()));
    }

    @Test
    void getByIdErrorTest() {
        given(soamEnumRepository.findById(EMPTY_SOAM_ENUM_ID)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> soamEnumService.getById(EMPTY_SOAM_ENUM_ID));
    }

    @Test
    void findByEnumIdAndNameTest() {
        given(soamEnumRepository.findByEnumIdAndName(PriorityType.class.getSimpleName(), TEST_PRIORITY_1.getName())).willReturn(Optional.of(TEST_PRIORITY_1));

        Optional<SoamEnum> maybeSoamEnum = soamEnumService.findBySoamEnumIdAndName(PriorityType.class, TEST_PRIORITY_1.getName());
        assertTrue(maybeSoamEnum.isPresent());
        assertEquals(TEST_PRIORITY_1.getId(), maybeSoamEnum.get().getId());
    }

    @Test
    void findByEnumIdAndSequenceTest() {
        given(soamEnumRepository.findByEnumIdAndSequence(PriorityType.class.getSimpleName(), TEST_PRIORITY_1.getSequence())).willReturn(Optional.of(TEST_PRIORITY_1));

        Optional<SoamEnum> maybeSoamEnum = soamEnumService.findBySoamEnumIdAndSequence(PriorityType.class, TEST_PRIORITY_1.getSequence());
        assertTrue(maybeSoamEnum.isPresent());
        assertEquals(TEST_PRIORITY_1.getId(), maybeSoamEnum.get().getId());
    }

    @Test
    void findBySoamEnumIdTest() {
        given(soamEnumRepository.findByEnumId(eq(PriorityType.class.getSimpleName()), any())).willReturn(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2));

        assertEquals(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2), soamEnumService.findBySoamEnumId(PriorityType.class));
    }

    @Test
    void findAllTest() {
        given(soamEnumRepository.findAll(any())).willReturn(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2));

        assertEquals(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2), soamEnumService.findAll());
    }

    @Test
    void saveTest() {
        soamEnumService.save(new PriorityType());
        verify(soamEnumRepository, times(1)).save(any());
    }
}
