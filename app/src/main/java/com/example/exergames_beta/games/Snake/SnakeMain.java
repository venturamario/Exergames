package com.example.exergames_beta.games.Snake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.exergames_beta.R;
import com.example.exergames_beta.realTime.FaceDetectionAnalyzer;
import com.example.exergames_beta.realTime.OnFaceMovementListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.FaceDetector;

import java.util.concurrent.ExecutionException;

public class SnakeMain extends AppCompatActivity implements OnAppleEatenListener, OnFaceMovementListener {

    // CONSTANTS
    private FaceDetector faceDetector;              // Detector de rostros
    private final float MIN_MOVEMENT_X = 20.0f;     // Sensibilidad de movimientos en X
    private final float MIN_MOVEMENT_Y = 20.0f;     // Sensibilidad de movimientos en Y

    // VARS
    private int score;
    TextView pointsTV, movementTV;
    SnakeView snakeView;
    private FaceDetectionAnalyzer faceDetectionAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snakemain);

        snakeView = findViewById(R.id.snakeView);
        snakeView.setOnAppleEatenListener(this);

        faceDetectionAnalyzer = new FaceDetectionAnalyzer(this);
        faceDetectionAnalyzer.setOnFaceMovementListener(this);

        score = 0;
        pointsTV = findViewById(R.id.pointsTV);
        pointsTV.setText("Score: "+score);

        movementTV = findViewById(R.id.movementTV);
        movementTV.setText("Ningún movimiento detectado");
    }

    // Evento asociado a que la serpiente coma la manzana
    public void onAppleEaten(int points) {
        if (points == 0) {
            score = 0;
        }
        score += points;
        pointsTV.setText("Score: "+score);
    }

    // Se cambia de direccion
    public void changeDirection(String direction) {
        if (snakeView != null) {
            snakeView.setDireccion(direction);
        }
    }

    // Cambiar de direccion en base al listener
    public void onFaceMovement(String horizontalMovement, String verticalMovement) {
        if (horizontalMovement.equals("Izquierda")) {
            movementTV.setText("Movimiento: Izquierda");
            snakeView.setDireccion("IZQUIERDA");
        } else if (horizontalMovement.equals("Derecha")) {
            movementTV.setText("Movimiento: Derecha");
            snakeView.setDireccion("DERECHA");
        } else if (verticalMovement.equals("Arriba")) {
            movementTV.setText("Movimiento: Arriba");
            snakeView.setDireccion("SUBE");
        } else if (verticalMovement.equals("Abajo")) {
            movementTV.setText("Movimiento: Abajo");
            snakeView.setDireccion("BAJA");
        }
    }
}
