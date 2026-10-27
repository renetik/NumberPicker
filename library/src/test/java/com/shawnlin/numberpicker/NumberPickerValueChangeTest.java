package com.shawnlin.numberpicker;

import static com.shawnlin.numberpicker.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(ParameterizedRobolectricTestRunner.class)
public class NumberPickerValueChangeTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> geometries() {
        return Arrays.asList(new Object[][]{
                {new WheelGeometry("spacing larger than text", 400, 16, 24, 64, 36)},
                {new WheelGeometry("spacing smaller than text", 180, 20, 24, 29, 19)},
        });
    }

    private static final int InitialValue = 50;

    private final WheelGeometry geometry;

    public NumberPickerValueChangeTest(WheelGeometry geometry) {
        this.geometry = geometry;
    }

    @Test
    public void releaseInsideBandChangesOnce() {
        NumberPickerDriver wheel = createWheel(NumberPicker.VERTICAL, NumberPicker.ASCENDING);
        wheel.dragAndRelease(geometry.bandReleaseOffset);
        assertValueChanges(wheel, InitialValue - 1);
    }

    @Test
    public void releaseAtHalfItemKeepsValue() {
        NumberPickerDriver wheel = createWheel(NumberPicker.VERTICAL, NumberPicker.ASCENDING);
        wheel.dragAndRelease(geometry.itemSize / 2);
        assertValueChanges(wheel);
        assertEquals(InitialValue, wheel.picker.getValue());
    }

    @Test
    public void releaseJustPastHalfItemChangesOnce() {
        NumberPickerDriver wheel = createWheel(NumberPicker.VERTICAL, NumberPicker.ASCENDING);
        wheel.dragAndRelease(geometry.itemSize / 2 + 1);
        assertValueChanges(wheel, InitialValue - 1);
    }

    @Test
    public void flingStepsOneItemAtATime() {
        NumberPickerDriver wheel = createWheel(NumberPicker.VERTICAL, NumberPicker.ASCENDING);
        wheel.fling();
        List<Integer> values = wheel.values();
        assertTrue("fling crossed " + values, values.size() >= 3);
        for (int i = 0; i < values.size(); i++)
            assertEquals("values " + values, Math.floorMod(InitialValue - 1 - i, NumberPickerDriver.ValueCount),
                    (int) values.get(i));
        assertEquals("value changes after idle", values, wheel.valuesBeforeIdle());
        assertEquals(SCROLL_STATE_IDLE, wheel.lastScrollState());
    }

    @Test
    public void descendingReleaseInsideBandChangesOnce() {
        NumberPickerDriver wheel = createWheel(NumberPicker.VERTICAL, NumberPicker.DESCENDING);
        wheel.dragAndRelease(geometry.bandReleaseOffset);
        assertValueChanges(wheel, InitialValue + 1);
    }

    @Test
    public void horizontalReleaseInsideBandChangesOnce() {
        NumberPickerDriver wheel = createWheel(NumberPicker.HORIZONTAL, NumberPicker.ASCENDING);
        wheel.dragAndRelease(geometry.bandReleaseOffset);
        assertValueChanges(wheel, InitialValue - 1);
    }

    @Test
    public void tapOnNextItemChangesOnce() {
        NumberPickerDriver wheel = createWheel(NumberPicker.VERTICAL, NumberPicker.ASCENDING);
        wheel.tap(geometry.length / 2 + geometry.itemSize);
        assertValueChanges(wheel, InitialValue + 1);
    }

    private NumberPickerDriver createWheel(int orientation, int order) {
        return new NumberPickerDriver(geometry, orientation, order, InitialValue);
    }

    private static void assertValueChanges(NumberPickerDriver wheel, Integer... expectedValues) {
        List<Integer> expected = Arrays.asList(expectedValues);
        assertEquals(expected, wheel.values());
        assertEquals("value changes after idle", wheel.values(), wheel.valuesBeforeIdle());
        assertEquals(SCROLL_STATE_IDLE, wheel.lastScrollState());
    }
}
