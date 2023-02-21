package com.soam.service.priority;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PriorityServiceTest {
    private final static PriorityType TEST_PRIORITY_1 = new PriorityType();
    private final static PriorityType TEST_PRIORITY_2 = new PriorityType();

    static {
        TEST_PRIORITY_1.setId(1);
        TEST_PRIORITY_1.setName("Test Priority 1");
        TEST_PRIORITY_1.setSequence(10);
        TEST_PRIORITY_2.setId(2);
        TEST_PRIORITY_2.setName("Test Priority 2");
        TEST_PRIORITY_2.setSequence(20);
    }

    @Mock
    private PriorityRepository priorityRepository;

    @InjectMocks
    private PriorityService priorityService;

    @Test
    void findAllTest() {
        given(priorityRepository.findAllByOrderBySequence()).willReturn(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2));

        assertEquals(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2), priorityService.findAll());
    }
}
