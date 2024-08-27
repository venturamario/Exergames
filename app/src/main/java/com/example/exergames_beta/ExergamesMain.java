package com.example.exergames_beta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.example.exergames_beta.connection.DatabaseConnection;
import com.example.exergames_beta.games.Snake.SnakeMain;
import com.example.exergames_beta.login.Login;
import com.example.exergames_beta.notifications.DailyNotificationReceiver;
import com.example.exergames_beta.realTime.FaceDetectionAnalyzer;
import com.example.exergames_beta.util.CervicalCondition;
import com.example.exergames_beta.util.Game;
import com.example.exergames_beta.util.GameDetail;
import com.example.exergames_beta.util.SuperObject;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExergamesMain extends AppCompatActivity {

    // VARIABLES Y CONSTANTS
    public final String playButtonText = "Jugar";
    String gameName = "bricksbreaker";
    ImageCarousel carousel;
    List<CarouselItem> list;
    TextView textViewName;
    EditText searchBarUsers;
    User user, userAux;
    ImageButton searchUsersBtn;
    Game game;
    DatabaseConnection connection;
    SuperObject superobject;

    // ON CREATE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exergamesmain);

        // Programar la notificación diaria
        scheduleDailyNotification(this);

        // Instanciar super objeto
        superobject = new SuperObject();

        // Conexion con bd
        connection = new DatabaseConnection();

        // Obtener usuario de intent anterior
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        user.toString();
        userAux = new User();

        // Actualizar textView
        textViewName = findViewById(R.id.textViewUserName);
        if (user != null) {
            textViewName.setText("Bienvenido,\n"+user.name+" "+user.surname);
            superobject.setUser(user);
        }

        searchBarUsers = findViewById(R.id.searchBar);
        searchUsersBtn = findViewById(R.id.searchUsers);

        // Instanciar objetos
        carousel = findViewById(R.id.carousel);
        list = new ArrayList<CarouselItem>();

        // Iniciar el slider
        initCarousel(carousel, list);

        // Gestion de permisos de acceso a camara
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ android.Manifest.permission.CAMERA}, 0);
            }
        }
    }
    public void scheduleDailyNotification(Context context) {
        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Configurar el horario para la notificación diaria a las 12:00am
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Si la hora ya ha pasado, se programa para el siguiente día
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Programar la alarma para repetirse diariamente
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }


    // ON CLICK
    public void onTryTracker(View view) {
        Intent intent = new Intent(this, FaceDetectionAnalyzer.class);
        startActivity(intent);
        //finish();
    }

    public void onLogOut(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void onGoToProfile(View view) {
        // Obtener número de seguidores
        Connection con;
        PreparedStatement pStatement;
        ResultSet rs;
        try {
            con = connection.conexionBD();
            String callFunction = "SELECT get_followers_count(?);";
            pStatement = con.prepareStatement(callFunction);

            pStatement.setString(1, this.user.getUsername());
            rs = pStatement.executeQuery();

            if (rs.next()) {
                int numFollowers = rs.getInt(1);
                superobject.setNumFollowers(numFollowers);
                superobject.setUserAux(user);
                superobject.setUser(user);
            }

            // Cerrar conexiones
            rs.close();
            pStatement.close();
            con.close();

            Intent intent = new Intent(this, ProfileDetail.class);
            intent.putExtra("superobject", superobject);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("ExergamesMain exception at <onGoToProfile>",e.toString());
            e.printStackTrace();
            e.getMessage();
        }
    }

    public void onSearchUser(View view) {
        if (searchBarUsers.getText().length() == 0) {
            Toast.makeText(this, "Introduce algún texto para iniciar la búsuqeda", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, SearchResults.class);
            superobject.setAux(searchBarUsers.getText().toString());
            superobject.setUser(user);
            intent.putExtra("superobject", superobject);
            startActivity(intent);
        }
    }

    public void getExergameInfoFromBD(String name) {
        Connection con;
        PreparedStatement pStatement;
        ResultSet rs;
        Game g = new Game();

        // Obtener info del juego en base a su nombre
        try {
//            int gameId = getGameIdByName(name);
//            con = connection.conexionBD();
//            String callFunction = "SELECT * FROM get_game_details(?);";
//            pStatement = con.prepareStatement(callFunction);
//
//            pStatement.setInt(1, gameId);
//            rs = pStatement.executeQuery();
//
//
//            if (rs.next()) {
//
//                int idGame = rs.getInt(1);
//                String gameName = rs.getString(2);
//                String gameDesc = rs.getString(3);
//                String gameInstructions = rs.getString(4);
//                float gameDifficulty = rs.getFloat(5);
//                int idCerv = rs.getInt(6);
//
//                g = new Game();
//                g.setId(idGame);
//                g.setName(gameName);
//                g.setDescription(gameDesc);
//                g.setInstructions(gameInstructions);
//                g.setDifficulty((int) gameDifficulty);
//                g.setCervicalConditions(getCervicalProblemsById(idCerv));
//
//            }
//
//            superobject.setGame(g);
//            superobject.setUserAux(null);
//            superobject.setUser(user);
//
//            // Cerrar conexiones
//            rs.close();
//            pStatement.close();
//            con.close();

            Intent intent = new Intent(this, GameDetail.class);
            //Game g = new Game();
            g.setName("Snake Game");
            superobject.setGame(g);
            superobject.setUser(user);
            intent.putExtra("superobject", superobject);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("ExergamesMain exception at <getExergameInfoFromBD>",e.toString());
            e.printStackTrace();
            e.getMessage();
        }

        // Ir a la vista de detalle del juego
        this.gotoGameDetail(superobject);
    }

    // Aux
    private ArrayList<CervicalCondition> getCervicalProblemsById(int idCerv) {
        ArrayList<CervicalCondition> cervicalConditions = new ArrayList<>();
        switch (idCerv) {
            case 1:
                cervicalConditions.add(new CervicalCondition("Limitaciones de movimiento horizontal"));
                return cervicalConditions;
            case 2:
                cervicalConditions.add(new CervicalCondition("Limitaciones de movimiento vertical"));
                return cervicalConditions;
            default:
                cervicalConditions.add(new CervicalCondition("Limitaciones de movimiento horizontal y vertical"));
                return cervicalConditions;
        }
    }

    private int getGameIdByName(String gameName) {
        if (gameName.equals("bricksbreaker")) {
            return 1;
        } else if (gameName.equals("pacman")) {
            return 2;
        } else if (gameName.equals("flappybird")) {
            return 3;
        } else {
            return 4;
        }
    }

    public void OnRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(ExergamesMain.class.getSimpleName(), "Grant result for "+permissions[0]+ " is "+grantResults[0]);
    }

    public void gotoGameDetail(SuperObject superObject) {
        Intent intent = new Intent(this, GameDetail.class);
        intent.putExtra("superObject", superObject);
        startActivity(intent);
        //finish();
    }

    public void initCarousel(ImageCarousel carousel, List<CarouselItem> list) {
        // Tipo de slider
        carousel.setImageScaleType(ImageView.ScaleType.FIT_CENTER);
        carousel.setCarouselType(CarouselType.SHOWCASE);
        carousel.setScaleOnScroll(true);
        carousel.setScalingFactor(.15f);    // 0 to 1; 1 means 100
        carousel.setShowCaption(false);

        // Autoscroll
        carousel.setAutoWidthFixing(true);
        carousel.setAutoPlay(false);        // True para habilitar
        carousel.setAutoPlayDelay(4000);

        // Poner imagenes en el slider
        list.add(new CarouselItem(R.drawable.bricksbreakerborder, "bricksbreaker"));
        list.add(new CarouselItem(R.drawable.flappybirdborder, "flappybird"));
        list.add(new CarouselItem(R.drawable.pacmanborder,"pacman"));
        list.add(new CarouselItem(R.drawable.snakeborder,"snake"));

        // Click listener
        carousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                gameName = carouselItem.getCaption();
                getExergameInfoFromBD(gameName);
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });

        // Poner lista al slider
        carousel.addData(list);
    }
}