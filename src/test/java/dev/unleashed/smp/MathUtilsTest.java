package dev.unleashed.smp.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

class MathUtilsTest {

    @Test
    void randomIntWithinBounds() {
        for (int i = 0; i < 1000; i++) {
            int v = MathUtils.randomInt(5, 10);
            assertTrue(v >= 5 && v <= 10, "value out of bounds: " + v);
        }
    }

    @Test
    void chanceRespectsZeroAndOne() {
        assertFalse(MathUtils.chance(0.0));
        assertTrue(MathUtils.chance(1.0));
    }

    @Test
    void weightedPickReturnsValidIndex() {
        double[] weights = {1, 0, 0};
        int idx = MathUtils.weightedPick(weights);
        assertEquals(0, idx);
        double[] allZero = {0, 0, 0};
        assertEquals(-1, MathUtils.weightedPick(allZero));
    }

    @Test
    void clampBounds() {
        assertEquals(5, MathUtils.clamp(2, 5, 10));
        assertEquals(10, MathUtils.clamp(20, 5, 10));
        assertEquals(7, MathUtils.clamp(7, 5, 10));
    }

    @Test
    void randomIsNonNull() {
        assertNotNull(MathUtils.random());
    }
}
