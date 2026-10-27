package com.shawnlin.numberpicker;

/**
 * A seven item wheel laid out along {@code length} pixels. {@code itemSize} is the text plus the
 * spacing the picker derives from that layout. {@code pastHalfReleaseOffset} lies past half an
 * item, where upstream 2.4.13 misreports a release: the value flipping when the spacing exceeds
 * the text, and a change after idle when it does not.
 */
final class WheelGeometry {

    static final int ItemCount = 7;
    static final int CrossLength = 200;

    final String name;
    final int length;
    final float textSize;
    final float selectedTextSize;
    final int itemSize;
    final int pastHalfReleaseOffset;

    WheelGeometry(String name, int length, float textSize, float selectedTextSize,
                  int itemSize, int pastHalfReleaseOffset) {
        this.name = name;
        this.length = length;
        this.textSize = textSize;
        this.selectedTextSize = selectedTextSize;
        this.itemSize = itemSize;
        this.pastHalfReleaseOffset = pastHalfReleaseOffset;
    }

    @Override
    public String toString() {
        return name;
    }
}
