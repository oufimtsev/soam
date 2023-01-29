package com.soam.priority;

import com.soam.model.priority.PriorityType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;

class PriorityTest {
    private PriorityType getPriorityLow() {
        PriorityType low = new PriorityType();
        low.setId(1);
        low.setName("Low");
        low.setSequence(1);
        return low;
    }

    private PriorityType getPriorityMedium() {
        PriorityType medium = new PriorityType();
        medium.setId(2);
        medium.setName("Medium");
        medium.setSequence(2);
        return medium;
    }

    private PriorityType getPriorityHigh() {
        PriorityType high = new PriorityType();
        high.setId(3);
        high.setName("High");
        high.setSequence(3);
        return high;
    }

    @Test
    void testPriorityType() {
        PriorityType low = getPriorityLow();
        PriorityType medium = getPriorityMedium();
        PriorityType high = getPriorityHigh();
        ArrayList<PriorityType> unsorted = Lists.newArrayList(high, low, medium);
        Optional<PriorityType> first = unsorted.stream().sorted().findFirst();

        assertEquals("Check your priorities", first.get(), low);
    }
}
