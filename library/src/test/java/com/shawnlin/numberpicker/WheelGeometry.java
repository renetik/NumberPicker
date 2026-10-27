package com.shawnlin.numberpicker;

/**
 * A seven item wheel laid out along {@code length} pixels. {@code itemSize} is the text plus the
 * spacing the picker derives from that layout, and {@code bandReleaseOffset} lies past half an
 * item, where upstream 2.4.13 reports more than one value change for a release.
 */
final class WheelGeometry {

    static final int ItemCount = 7;
    static final int CrossLength = 200;

    final String name;
    final int length;
    final float textSize;
    final float selectedTextSize;
    final int itemSize;
    final int bandReleaseOffset;

    WheelGeometry(String name, int length, float textSize, float selectedTextSize,
                  int itemSize, int bandReleaseOffset) {
        this.name = name;
        this.length = length;
        this.textSize = textSize;
        this.selectedTextSize = selectedTextSize;
        this.itemSize = itemSize;
        this.bandReleaseOffset = bandReleaseOffset;
    }

    @Override
    public String toString() {
        return name;
    }
}
