package com.shawnlin.numberpicker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/** Records each text drawn into it, in drawing order. */
final class DrawnTextCanvas extends Canvas {

    record DrawnText(String text, float textSize) {
    }

    final List<DrawnText> drawnTexts = new ArrayList<>();

    DrawnTextCanvas(int width, int height) {
        super(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
    }

    @Override
    public void drawText(String text, float x, float y, Paint paint) {
        drawnTexts.add(new DrawnText(text, paint.getTextSize()));
    }
}
