package com.shawnlin.numberpicker;

/** A wheel of {@code itemCount} items laid out along {@code length} pixels. */
final class WheelGeometry {

    static final int CrossLength = 200;

    final String name;
    final int itemCount;
    final int length;
    final float textSize;
    final float selectedTextSize;

    WheelGeometry(String name, int itemCount, int length, float textSize, float selectedTextSize) {
        this.name = name;
        this.itemCount = itemCount;
        this.length = length;
        this.textSize = textSize;
        this.selectedTextSize = selectedTextSize;
    }

    int middleItemIndex() {
        return itemCount / 2;
    }

    @Override
    public String toString() {
        return name;
    }
}
