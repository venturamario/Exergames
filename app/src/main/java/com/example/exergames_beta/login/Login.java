package com.example.exergames_beta.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exergames_beta.ExergamesMain;
import com.example.exergames_beta.R;
import com.example.exergames_beta.User;
import com.example.exergames_beta.connection.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class Login extends AppCompatActivity {

    // VARIABLES Y CONSTANTES
    EditText editText_Username;         // Username ET
    EditText editText_Password;         // Password Et
    String username, password;          // Strings que contienen username y password
    DatabaseConnection connection;      // Objeto de conexión a base de datos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editText_Username = findViewById(R.id.username);
        editText_Password = findViewById(R.id.password);
        connection = new DatabaseConnection();
    }

    // Evento asociado al boton de inicio de sesión
    public void onGotoCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    // Evento asociado al boton de inicio de sesión
    public void onLogIn(View view) {
         User u = new User("Mario","Ventura","mario.ventura","1234",999,999);
         gotoExergames(u);


//        username = editText_Username.getText().toString();
//        password = editText_Password.getText().toString();
//
//        try {
//            Connection con = connection.conexionBD();
//            String callFunction = "SELECT * FROM login(?,?);";
//            PreparedStatement pStatement = con.prepareStatement(callFunction);
//
//            pStatement.setString(1, username);
//            pStatement.setString(2, password);
//
//            ResultSet rs = pStatement.executeQuery();
//
//            if (rs.next()) {
//                String uname = rs.getString("username");
//                String name = rs.getString("name");
//                String surname = rs.getString("lastname");
//                String psswd = rs.getString("password");
//                int level = rs.getInt("level");
//                float xp = rs.getFloat("xp");
//
//                if (psswd.equals(password)) {
//                    User user = new User(name, surname, uname, psswd, level, xp);
//                    user.toString();
//                    gotoExergames(user);
//                } else {
//                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(this, "El nombre de usuario o contraseña no existen", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Log.e("LOG IN EXCEPTION",e.toString());
//            e.printStackTrace();
//            e.getMessage();
//        }

    }

    // Going to exergames main page
    public void gotoExergames(User user) {
        Intent intent = new Intent(this, ExergamesMain.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}