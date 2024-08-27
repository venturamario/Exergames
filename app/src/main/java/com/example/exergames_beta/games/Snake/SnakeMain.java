package com.example.exergames_beta.games.Snake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.exergames_beta.R;
import com.example.exergames_beta.User;
import com.example.exergames_beta.realTime.FaceDetectionAnalyzer;
import com.example.exergames_beta.realTime.OnFaceMovementListener;
import com.example.exergames_beta.util.Game;
import com.example.exergames_beta.util.SuperObject;
import com.google.mlkit.vision.face.FaceDetector;


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
    User u; Game g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snakemain);

        Intent intent = getIntent();
        SuperObject superObject = (SuperObject) intent.getSerializableExtra("superObject");

        if (superObject != null) {
            g = superObject.getGame();
            u = superObject.getUser();
        }

        snakeView = findViewById(R.id.snakeView);
        snakeView.setUserID(u);
        snakeView.setGameID(g);

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
