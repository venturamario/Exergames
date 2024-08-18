package com.example.exergames_beta.realTime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.camera.core.CameraSelector;

public class GraphicOverlay extends View {
    private Paint paint;
    private float x;
    private float y;
    private final int CIRCLE_RADIUS = 10;

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
    public void updateCoordinates(float Xnose, float Ynose, int Wprev, int Hprev, int Wcamera, int Hcamera, int rotation) {
        // Calcular la escala de la cámara a la vista previa
        float scaleX = Wprev / (float) Wcamera;
        float scaleY = Hprev / (float) Hcamera;

        // Usar la menor escala para mantener la relación de aspecto
        float scale = Math.min(scaleX, scaleY);

        // Adaptar las coordenadas de la nariz a la vista previa
        float adjustedX = Xnose * scale;
        float adjustedY = Ynose * scale;

        // Considerar padding (offsets) si los hay para alinear la imagen
        float offsetX = (Wprev - (Wcamera * scale)) / 2;
        float offsetY = (Hprev - (Hcamera * scale)) / 2;

        adjustedX += offsetX;
        adjustedY += offsetY;

        // Guardar las coordenadas ajustadas para el redibujo
        this.x = adjustedX;
        this.y = adjustedY;

        // Redibujar la vista
        invalidate();
    }


    //Dibujar circulo en coordenadas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, CIRCLE_RADIUS, paint);
    }
}

