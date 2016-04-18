package com.mchapagai.simplepaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

/**
 * Created by mchapagai on 4/14/16.
 * An Activity with Draw attributes to handle different events.
 */
public class PaintingView extends View implements OnTouchListener {

    private Canvas canvas;
    private Path path;
    private Paint paint;
    private float hX, vY;
    private static final float TOUCH_TOLERANCE = 4;
    private boolean isEraserActive = false; // set the eraser to false
    private ArrayList<Pair<Path, Paint>> paths = new ArrayList<>();

    // PaintingView class constructor to initialize values
    public PaintingView(Context context, AttributeSet attribute) {
        super(context, attribute);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundColor(Color.WHITE);
        this.setOnTouchListener(this);
        onCanvasInitialization();
    }

    private void onCanvasInitialization() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2);
        canvas = new Canvas();
        path = new Path();
        Paint newPaint = new Paint(paint);
        paths.add(new Pair<Path, Paint>(path, newPaint));
    }

    /*
    * Facilitate drawing on touch
    * MotionEvent parameter to the onTouch method will let us respond to the particular touch events
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
        }
        invalidate();
        return true;

    }

    @Override
    protected void onDraw(Canvas xcanvas) {
        for (Pair<Path, Paint> p : paths) {
            xcanvas.drawPath(p.first, p.second);
        }
    }

    private void touch_start(float x, float y) {
        if (isEraserActive) {
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(10);
            Paint newPaint = new Paint(paint); // Clones the paint object
            paths.add(new Pair<Path, Paint>(path, newPaint));
        } else {
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            Paint newPaint = new Paint(paint);
            paths.add(new Pair<Path, Paint>(path, newPaint));
        }

        path.reset();
        path.moveTo(x, y);
        hX = x;
        vY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - hX);
        float dy = Math.abs(y - vY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(hX, vY, (x + hX) / 2, (y + vY) / 2);
            hX = x;
            vY = y;
        }
    }

    /*
    * Commit the patch to the offscreen
    * Make sure we only draw once
    * Commit paint object
     */

    private void touch_up() {
        path.lineTo(hX, vY);
        canvas.drawPath(path, paint);
        path = new Path();
        Paint newPaint = new Paint(paint);
        paths.add(new Pair<Path, Paint>(path, newPaint));
    }


    public void activateEraser() {
        isEraserActive = true;
    }


    public void deactivateEraser() {
        isEraserActive = false;
    }

    public boolean isEraserActive() {
        return isEraserActive;
    }


    public void reset() {
        paths.clear();

        invalidate();
    }


}