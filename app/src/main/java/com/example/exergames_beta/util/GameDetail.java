package com.example.exergames_beta.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exergames_beta.R;
import com.example.exergames_beta.User;
import com.example.exergames_beta.games.BricksBreaker.BricksBreaker;
import com.example.exergames_beta.games.FlappyBird.FlappyBird;
import com.example.exergames_beta.games.Pacman.Pacman;
import com.example.exergames_beta.games.Snake.SnakeMain;

import java.util.ArrayList;

public class GameDetail extends AppCompatActivity {

    // VARS AND CONSTANTS
    TextView exName, exDescription, exDifficulty, exProbCerv;
    ImageView gameImage;
    SuperObject superObject;
    User user;
    Game exergame;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamedetail);

        // Obtener TextViews del layout
        exName = findViewById(R.id.valueName);
        exDescription = findViewById(R.id.descriptionValue);
        exDifficulty = findViewById(R.id.difficultyValue);
        exProbCerv = findViewById(R.id.relatedPCValue);
        gameImage = findViewById(R.id.imageView4);

        // Obtener super objeto de intent anterior
        Intent intent = getIntent();
        superObject = (SuperObject) intent.getSerializableExtra("superObject");

        // Obtener exergame, usuario del super objeto
        user = superObject.getUser();
        exergame = superObject.getGame();

        // Cargar imagen del juego
        loadExergameImage(exergame);

        exName.setText(exergame.getName());
        exDescription.setText(exergame.getDescription());
        String text = exergame.getDifficulty() + " Estrellas";
        exDifficulty.setText(text);
        String cervConditions = exergame.getCervicalConditionsString();
        exProbCerv.setText(cervConditions);
    }

    // Jugar al juego seleccionado
    public void onPlayToExergame(View view) {
        String name = this.exergame.getName();
        if (name.equals("Pacman")) {
            Log.d("INFO EXERGAME","SE VA A JUGAR A PACMAN");
            Intent intent = new Intent(this, Pacman.class);
            intent.putExtra("superObject", superObject);
            startActivity(intent);

        } else if (name.equals("Bricks Breaker")) {
            Log.d("INFO EXERGAME","SE VA A JUGAR A BRICKS BREAKER");
            Intent intent = new Intent(this, BricksBreaker.class);
            intent.putExtra("superObject", superObject);
            startActivity(intent);

        } else if (name.equals("Flappy Bird")) {
            Log.d("INFO EXERGAME","SE VA A JUGAR A FLAPPY BIRD");
            Intent intent = new Intent(this, FlappyBird.class);
            intent.putExtra("superObject", superObject);
            startActivity(intent);

        } else if (name.equals("Snake Game")) {
            Log.d("INFO EXERGAME","SE VA A JUGAR AL JUEGO DE SNAKE");
            Intent intent = new Intent(this, SnakeMain.class);
            startActivity(intent);
        } else {
            Log.d("INFO EXERGAME","NO SE HA DETECTADO NINGÚN JUEGO PARA JUGAR");
        }
    }

    private void loadExergameImage(Game game) {
        // cargar imagenes con borde negro almacenadas en drawable
        if (!(game ==null)) {
            String name = game.getName();
            if (name.equals("Pacman")) {
                gameImage.setImageResource(R.drawable.pacmanborder);
            } else if (name.equals("Bricks Breaker")) {
                gameImage.setImageResource(R.drawable.bricksbreakerborder);
            } else if (name.equals("Flappy Bird")) {
                gameImage.setImageResource(R.drawable.flappybirdborder);
            } else if (name.equals("Snake Game")) {
                gameImage.setImageResource(R.drawable.snakeborder);
            }
        }
    }

    private void onGotoMyStats(View view) {

    }
}
