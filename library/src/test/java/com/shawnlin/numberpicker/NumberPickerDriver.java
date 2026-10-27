package com.shawnlin.numberpicker;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.shawnlin.numberpicker.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

import com.shawnlin.numberpicker.DrawnTextCanvas.DrawnText;

import java.util.ArrayList;
import java.util.List;

/**
 * Lays a picker out at a fixed size, drives it with touch events and advances the clock the
 * scrollers read, recording every value change and scroll state.
 */
final class NumberPickerDriver {

    static final int ValueCount = 100;

    private static final int FrameMillis = 16;
    private static final int SettleMillis = 5000;
    private static final int SlowMoveMillis = 50;
    private static final int FastMoveMillis = 5;
    private static final int FastMovePixels = 20;
    private static final int FastMoveCount = 6;
    private static final int DriverClockStepMillis = 600_000;

    private static int createdDriverCount;

    final NumberPicker picker;
    private final WheelGeometry geometry;
    private final List<Integer> scrollStates = new ArrayList<>();
    private final List<Integer> values = new ArrayList<>();
    private int valueCountAtFirstIdle = -1;
    private final boolean isHorizontal;
    private final int touchSlop;
    private final int width;
    private final int height;
    private final int center;
    private long downTime;

    NumberPickerDriver(WheelGeometry geometry, int orientation, int order, int value) {
        this(geometry, orientation, order, value, true);
    }

    NumberPickerDriver(WheelGeometry geometry, int orientation, int order, int value,
                       boolean wrap) {
        // Robolectric resets the clock for each test, but pooled VelocityTrackers keep the times of
        // earlier movements, which would then look newer than this driver's touches.
        advance(DriverClockStepMillis * ++createdDriverCount);
        Context context = RuntimeEnvironment.getApplication();
        this.geometry = geometry;
        isHorizontal = orientation == NumberPicker.HORIZONTAL;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        picker = new NumberPicker(context);
        picker.setOrientation(orientation);
        picker.setOrder(order);
        picker.setWheelItemCount(geometry.itemCount);
        picker.setTextSize(geometry.textSize);
        picker.setSelectedTextSize(geometry.selectedTextSize);
        picker.setWrapSelectorWheel(wrap);
        picker.setMinValue(0);
        picker.setMaxValue(ValueCount - 1);
        picker.setValue(value);
        picker.setOnValueChangedListener((view, oldValue, newValue) -> values.add(newValue));
        picker.setOnScrollListener((view, scrollState) -> {
            scrollStates.add(scrollState);
            if (scrollState == SCROLL_STATE_IDLE && valueCountAtFirstIdle < 0)
                valueCountAtFirstIdle = values.size();
        });
        new FrameLayout(context).addView(picker);
        width = isHorizontal ? geometry.length : WheelGeometry.CrossLength;
        height = isHorizontal ? WheelGeometry.CrossLength : geometry.length;
        picker.measure(makeMeasureSpec(width, EXACTLY), makeMeasureSpec(height, EXACTLY));
        picker.layout(0, 0, width, height);
        center = geometry.length / 2;
    }

    int lastScrollState() {
        return scrollStates.isEmpty() ? SCROLL_STATE_IDLE : scrollStates.get(scrollStates.size() - 1);
    }

    List<Integer> values() {
        return values;
    }

    /** Each item the wheel draws, in order along the wheel. */
    List<DrawnText> drawnTexts() {
        DrawnTextCanvas canvas = new DrawnTextCanvas(width, height);
        picker.onDraw(canvas);
        return canvas.drawnTexts;
    }

    String centreText() {
        return drawnTexts().get(geometry.middleItemIndex()).text();
    }

    /** Values reported before the first {@link NumberPicker.OnScrollListener#SCROLL_STATE_IDLE}. */
    List<Integer> valuesBeforeIdle() {
        return valueCountAtFirstIdle < 0 ? values : values.subList(0, valueCountAtFirstIdle);
    }

    /**
     * Drags slowly by {@code offset} pixels past the touch slop, holds still and releases, so the
     * release does not fling.
     */
    void dragAndRelease(int offset) {
        int position = center;
        touch(MotionEvent.ACTION_DOWN, position);
        position += touchSlop + 1;
        advance(SlowMoveMillis);
        touch(MotionEvent.ACTION_MOVE, position);
        for (int step = 0; step < offset; step++) {
            advance(SlowMoveMillis);
            touch(MotionEvent.ACTION_MOVE, ++position);
        }
        advance(SlowMoveMillis * 4);
        touch(MotionEvent.ACTION_UP, position);
        settle();
    }

    /** Flings at the full system fling velocity, so the wheel crosses several items. */
    void fling() {
        picker.setMaxFlingVelocityCoefficient(1);
        int position = center - FastMovePixels * FastMoveCount;
        touch(MotionEvent.ACTION_DOWN, position);
        for (int step = 0; step < FastMoveCount; step++) {
            advance(FastMoveMillis);
            position += FastMovePixels;
            touch(MotionEvent.ACTION_MOVE, position);
        }
        advance(FastMoveMillis);
        touch(MotionEvent.ACTION_UP, position + FastMovePixels);
        settle();
    }

    void tap(int position) {
        touch(MotionEvent.ACTION_DOWN, position);
        advance(FrameMillis);
        touch(MotionEvent.ACTION_UP, position);
        settle();
    }

    private void touch(int action, int position) {
        long now = SystemClock.uptimeMillis();
        if (action == MotionEvent.ACTION_DOWN) downTime = now;
        float x = isHorizontal ? position : WheelGeometry.CrossLength / 2f;
        float y = isHorizontal ? WheelGeometry.CrossLength / 2f : position;
        MotionEvent event = MotionEvent.obtain(downTime, now, action, x, y, 0);
        picker.dispatchTouchEvent(event);
        event.recycle();
    }

    private void settle() {
        for (int elapsed = 0; elapsed < SettleMillis; elapsed += FrameMillis) {
            advance(FrameMillis);
            picker.computeScroll();
        }
    }

    private static void advance(int millis) {
        ShadowLooper.idleMainLooper(millis, MILLISECONDS);
    }
}
