package com.example.exergames_beta.games.Snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.exergames_beta.R;

import java.util.LinkedList;

public class SnakeView extends View {

    // Colores
    int snakeColor = ContextCompat.getColor(this.getContext(), R.color.snake);

    // NECESARIOS
    private GestureDetector gestos;
    private Direccion direccion;
    private LinkedList<Punto> lista;
    private Handler manejador = new Handler(Looper.getMainLooper());;
    private Runnable tiempo;
    private int columna, fila;              // Columna y fila donde se encuentra la cabeza de la vibora
    private int colfruta, filfruta;         // Columna y fila donde se encuentra la fruta
    private boolean activo = true;          // Representa si el juego esta activo
    private int crecimiento = 0;            // Indica la cantidad de cuadraditos que debe crecer la vibora
    private int ladoCuadradito;
    private int cuadrosAncho = 20;          // Cuadros de ancho del tablero
    private int cuadrosAlto;                // cuadrosAlto se calcula según la altura del dispositivo

    // PROPIOS
    private OnAppleEatenListener onAppleEatenListener;  // Listener de comer manzana
    private int pointsPerApple = 100;                   // Puntos por comerse una manzana
    private Bitmap appleBitmap;                         // Imagen de la manzana
    private Bitmap grassBitmap;                         // Imagen de la hierba

    public enum Direccion {
        IZQUIERDA, DERECHA, SUBE, BAJA
    };

    class Punto {
        int x, y;

        public Punto(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gestos = new GestureDetector(this.getContext(),new EscuchaGestos());

        // Cargar las imágenes
        appleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        grassBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grass2);

        tiempo=new Runnable() {
            @Override
            public void run() {
                switch (direccion) {
                    case DERECHA:
                        columna++;
                        break;
                    case IZQUIERDA:
                        columna--;
                        break;
                    case SUBE:
                        fila--;
                        break;
                    case BAJA:
                        fila++;
                        break;
                }
                sePisa();
                lista.addFirst(new Punto(columna, fila));

                if (verificarComeFruta() == false && crecimiento == 0) {
                    lista.remove(lista.size() - 1);
                } else {
                    // Si creciento es mayor a cero es que debemos hacer crecer la vibora
                    if (crecimiento > 0)
                        crecimiento--;
                }
                verificarFueraTablero();
                invalidate();
                if (activo)
                    manejador.postDelayed(this,100);
            }
        };

        iniciar();

    }

    public void setOnAppleEatenListener(OnAppleEatenListener listener) {
        this.onAppleEatenListener = listener;
    }

    public void setDireccion(String nuevaDireccion) {
        switch (nuevaDireccion) {
            case "DERECHA":
                // Evitar movimientos opuestos que siempre conducen a la muerte de la serpiente
                // <--- --->
                if (direccion!=Direccion.IZQUIERDA) {
                    direccion = Direccion.DERECHA;
                }
                break;
            case "IZQUIERDA":
                // ---> <---
                if (direccion!=Direccion.DERECHA) {
                    direccion = Direccion.IZQUIERDA;
                }
                break;
            case "SUBE":
                if (direccion!=Direccion.BAJA) {
                    direccion = Direccion.SUBE;
                }
                break;
            case "BAJA":
                if (direccion!=Direccion.SUBE) {
                    direccion = Direccion.BAJA;
                }
                break;
        }

        // Llamar a invalidate() para forzar el redibujado
        //invalidate();
    }

    public void iniciar() {
        lista = new LinkedList<Punto>();
        direccion = Direccion.DERECHA;
        crecimiento = 0;
        activo = true;
        lista.add(new Punto(4, 5));
        lista.add(new Punto(3, 5));
        lista.add(new Punto(2, 5));
        lista.add(new Punto(1, 5));
        // indicamos la ubicacion de la cabeza de la vibora
        columna = 4;
        fila = 5;
        generarCoordenadaFruta();
        manejador.removeCallbacksAndMessages(null);
        manejador.postDelayed(tiempo,100);
    }


    // Serpiente se choca consigo misma
    private void sePisa() {
        for (Punto p : lista) {
            if (p.x == columna && p.y == fila) {
                activo = false;
            }
        }
    }

    private boolean verificarComeFruta() {
        if (columna == colfruta && fila == filfruta) {
            generarCoordenadaFruta();
            crecimiento = 1;
            if (onAppleEatenListener != null) {
                onAppleEatenListener.onAppleEaten(pointsPerApple);
            }
            return true;
        } else
            return false;
    }

    private void generarCoordenadaFruta() {
        // generamos la coordenada de la fruta
        colfruta = 3+(int) (Math.random() * (cuadrosAncho-4));
        filfruta = 4+(int) (Math.random() * (cuadrosAlto-4));

    }

    // controlamos si estamos fuera de la region del tablero
    private void verificarFueraTablero() {
        if (columna <= 0 || columna >= cuadrosAncho || fila <= 0 || fila >= cuadrosAlto) {
            activo = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Obtener las dimensiones sugeridas por el sistema
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // Calcular el tamaño del lado del cuadrado
        ladoCuadradito = width / cuadrosAncho;
        cuadrosAlto = height / ladoCuadradito;

        appleBitmap = Bitmap.createScaledBitmap(appleBitmap, ladoCuadradito, ladoCuadradito, false);
        grassBitmap = Bitmap.createScaledBitmap(grassBitmap, ladoCuadradito, ladoCuadradito, false);

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return true;
    }

    class EscuchaGestos extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float ancho=Math.abs(e2.getX()- e1.getX());
            float alto=Math.abs(e2.getY()-e1.getY());
            if (ancho>alto) {
                if (e2.getX() > e1.getX()) {
                    direccion = Direccion.DERECHA;
                } else {
                    direccion = Direccion.IZQUIERDA;
                }
            }
            else {
                if (e2.getY() > e1.getY()) {
                    direccion = Direccion.BAJA;
                } else {
                    direccion = Direccion.SUBE;
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            iniciar();
            if (onAppleEatenListener != null) {
                onAppleEatenListener.onAppleEaten(0);
            }
            return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint pincel1 = new Paint();
        pincel1.setColor(Color.WHITE);

        // Dibujar tablero
        for (int c = 0; c <= cuadrosAncho; c++) {
            for (int f = 0; f <= cuadrosAlto; f++) {
                canvas.drawBitmap(grassBitmap, c * ladoCuadradito, f * ladoCuadradito, null);
            }
        }

        // Dibujar serpiente
        pincel1.setColor(snakeColor);
        for (Punto punto : lista) {
            canvas.drawRect(punto.x * ladoCuadradito, punto.y * ladoCuadradito,
                    punto.x * ladoCuadradito + ladoCuadradito -3,  punto.y * ladoCuadradito+ladoCuadradito-3,pincel1);
        }
        // Dibujar manzana
        canvas.drawBitmap(appleBitmap, colfruta * ladoCuadradito, filfruta * ladoCuadradito, null);
    }


}
