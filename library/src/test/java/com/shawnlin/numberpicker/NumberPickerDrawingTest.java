package com.shawnlin.numberpicker;

import static com.shawnlin.numberpicker.NumberPicker.ASCENDING;
import static com.shawnlin.numberpicker.NumberPicker.DESCENDING;
import static com.shawnlin.numberpicker.NumberPicker.HORIZONTAL;
import static com.shawnlin.numberpicker.NumberPicker.VERTICAL;
import static com.shawnlin.numberpicker.NumberPickerDriver.ValueCount;
import static org.junit.Assert.assertEquals;

import com.shawnlin.numberpicker.DrawnTextCanvas.DrawnText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class NumberPickerDrawingTest {

    private static final int Value = 50;

    @Test
    public void descendingDrawsValueAtCentre() {
        for (int itemCount = 4; itemCount <= 7; itemCount++)
            for (int orientation : new int[]{VERTICAL, HORIZONTAL})
                assertDrawsAroundValue(itemCount, orientation, DESCENDING, Value, true);
    }

    @Test
    public void ascendingDrawsValueAtCentre() {
        for (int itemCount : new int[]{4, 6})
            assertDrawsAroundValue(itemCount, VERTICAL, ASCENDING, Value, true);
    }

    @Test
    public void descendingWithoutWrapDrawsEndValuesAtCentre() {
        assertDrawsAroundValue(6, VERTICAL, DESCENDING, 0, false);
        assertDrawsAroundValue(6, VERTICAL, DESCENDING, ValueCount - 1, false);
    }

    private static void assertDrawsAroundValue(int itemCount, int orientation, int order,
                                               int value, boolean wrap) {
        WheelGeometry geometry = new WheelGeometry(itemCount + " items", itemCount, 400, 16, 24);
        NumberPickerDriver wheel = new NumberPickerDriver(geometry, orientation, order, value, wrap);
        String wheelName = geometry + (orientation == VERTICAL ? " vertical" : " horizontal")
                + (order == ASCENDING ? " ascending" : " descending") + " at " + value;
        assertEquals(wheelName, expectedDrawnTexts(geometry, order, value, wrap),
                wheel.drawnTexts());
    }

    /** Items after the centre step one value in the wheel's order, items before it step back. */
    private static List<DrawnText> expectedDrawnTexts(WheelGeometry geometry, int order, int value,
                                                      boolean wrap) {
        int step = order == ASCENDING ? 1 : -1;
        List<DrawnText> texts = new ArrayList<>();
        for (int position = 0; position < geometry.itemCount; position++) {
            int offset = position - geometry.middleItemIndex();
            int itemValue = value + step * offset;
            String text = wrap ? String.valueOf(Math.floorMod(itemValue, ValueCount))
                    : itemValue < 0 || itemValue >= ValueCount ? "" : String.valueOf(itemValue);
            float textSize = offset == 0 ? geometry.selectedTextSize : geometry.textSize;
            texts.add(new DrawnText(text, textSize));
        }
        return texts;
    }
}
