package com.example.exergames_beta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exergames_beta.login.Login;
import com.example.exergames_beta.notifications.DailyNotificationReceiver;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Evento asociado al boton de Comenzar
    public void onGotoGetStarted(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

}