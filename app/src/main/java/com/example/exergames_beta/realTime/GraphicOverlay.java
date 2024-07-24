package com.example.exergames_beta.realTime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphicOverlay extends View {
    private Paint paint;
    private float x;
    private float y;

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }
/*
    public void updateCoordinates(float x, float y) {
        // Ajustar las coordenadas para que estén dentro del rango de la vista
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        this.x = Math.max(0, Math.min(viewWidth, -x)); // Asegurar que x esté dentro del rango
        this.y = Math.max(0, Math.min(viewHeight, -y)); // Asegurar que y esté dentro del rango
        invalidate(); // Redibujar la vista
    }
*/
    public void updateCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
        invalidate(); // Redibujar la vista
    }

    //Dibujar circulo en coordenadas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, 10, paint);
    }
}

