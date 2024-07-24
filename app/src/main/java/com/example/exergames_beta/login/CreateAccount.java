package com.example.exergames_beta.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exergames_beta.connection.DatabaseConnection;
import com.example.exergames_beta.util.Password;
import com.example.exergames_beta.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class CreateAccount extends AppCompatActivity {

    // VARIABLES Y CONSTANTES
    private final int PASSWORD_LENGTH = 8;
    EditText editText_Name;                                 // Nombre
    EditText editText_Surname;                              // Apellido
    EditText editText_Username;                             // Username
    EditText editText_Password1;                            // Password
    EditText editText_Password2;                            // Password confirmation
    String name,lastname,username,password1,password2;      // Strings con los valores introducidos
    int infoStatus = 0;                                     // Status de la info
    TextView textView_generatedPassword;                    // Password generated
    DatabaseConnection connection;                          // Conexion a base de datos

    // ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);

        // Instanciar los edit text y text view
        editText_Name = findViewById(R.id.name);               // Nombre
        editText_Surname = findViewById(R.id.surname);         // Apellido
        editText_Username = findViewById(R.id.username);       // Username
        editText_Password1 = findViewById(R.id.password);      // Password
        editText_Password2 = findViewById(R.id.password2);     // Password confirmation
        textView_generatedPassword = findViewById(R.id.generatedPassword);

        connection = new DatabaseConnection();
    }

    // ON CLICKS
    public void onCreateAccount(View view) {
        // Obtener texto de los editText
        name = editText_Name.getText().toString();              // Nombre introducido
        lastname = editText_Surname.getText().toString();       // Apellido introducido
        username = editText_Username.getText().toString();      // Username introducido
        password1 = editText_Password1.getText().toString();    // Password 1 introducido
        password2 = editText_Password2.getText().toString();    // Password 2 introducido

        // Comprobar información introducida
        if (infoOK(name,lastname,username,password1,password2)) {

            try {
                Connection con = connection.conexionBD();
                String callFunction = "SELECT public.create_user(?,?,?,?);";
                PreparedStatement pStatement = con.prepareStatement(callFunction);

                pStatement.setString(1, username);
                pStatement.setString(2, name);
                pStatement.setString(3, lastname);
                pStatement.setString(4, password1);

                ResultSet rs = pStatement.executeQuery();

                if (rs.next()) {
                    Boolean inserted = rs.getBoolean(1);
                    if (inserted) {
                        Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Ha ocurrido un error al crear una cuenta", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("CREATE ACCOUNT EXCEPTION",e.toString());
                e.printStackTrace();
                e.getMessage();
            }
        } else {
            showToastError(infoStatus);
        }
    }

    public void onCreatePassword(View view) {
        // Se crea un número aleatorio que actuará como semilla
        Random r = new Random();
        int seed = r.nextInt();

        // Se crea un objeto password y se llama al metodo generatePassword de la clase Password
        Password p = new Password();
        Password password = p.generatePassword(seed, PASSWORD_LENGTH, true);

        // Se obtiene el token creado y se asigna al atributo contraseña
        password1 = password.getToken();
        password2 = password1;

        // Se establece esa contraseña en el EditText
        String s = "La contraseña generada es "+password1;
        textView_generatedPassword.setText(s);
        textView_generatedPassword.setVisibility(View.VISIBLE);
        editText_Password1.setText(password1);
        editText_Password2.setText(password2);
        Log.d("INFO PASSWORD",s);
    }

    // Aux
    private boolean infoOK(String name, String surname, String username, String password1, String password2) {
        /*
        CÓDIGOS DE ERROR:
        infoStatus = -1 ---> Algún campo está vacío
        infoStatus = -2 ---> Contraseñas no coincidentes en el EditText
        infoStatus = -3 ---> Ya existe alguna cuenta con ese username
        infoStatus = -4 ---> Alguno de los campos es demasiado largo y se violaria una CONSTRAINT en BD
         */

        // Comprobar si algún campo está en blanco
        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            infoStatus = -1;
            return false;
        }

        // Comprobar contraseñas coincidentes
        if (!(password1.equals(password2))) {
            // Contraseñas no coincidentes
            infoStatus = -2;
            return false;
        }

        // Comprobar si ya existe alguna cuenta con ese correo
        if (usernameAlreadyExists(username, password1)) {
            infoStatus = -3;
            return false;
        }

        // Comprobar longitud de los campos
        if (username.length()>30 || name.length()>30 || surname.length()>30 || password1.length()>25 || password2.length()>25) {
            infoStatus = -4;
            return false;
        }

        // Toda la info es correcta
        return true;
    }

    public boolean usernameAlreadyExists(String u, String p) {
        boolean exists = false;
        try {
            Connection con = connection.conexionBD();
            String callFunction = "SELECT * FROM findUsername(?);";
            PreparedStatement pStatement = con.prepareStatement(callFunction);

            pStatement.setString(1, u);

            ResultSet rs = pStatement.executeQuery();

            if (rs.next()) {
                String uname = rs.getString("username");
                if(uname.equals(u)) {
                    exists = true;
                }
            }
        } catch (Exception e) {
            Log.e("CREATE ACCOUNT EXCEPTION",e.toString());
            e.printStackTrace();
            e.getMessage();

        } finally {
            return exists;
        }
    }

    private void showToastError(int status) {
        String nombreCampo = "";
        // Mostrar errores en función del valor de infoStatus
        switch (status) {
            case -1:
                // Descubrir qué campo esta vacío
                nombreCampo = getNullEditTextName(name,lastname,username,password1);
                Toast.makeText(this, "El campo '"+nombreCampo+"' está vacío", Toast.LENGTH_LONG).show();
                Log.e("ERR", "El campo '"+nombreCampo+"' está vacío");
                break;
            case -2:
                Toast.makeText(this, "Las contraseñas introducidas no coinciden", Toast.LENGTH_LONG).show();
                Log.e("ERR", "Las contraseñas introducidas no coinciden");
                break;
            case -3:
                Toast.makeText(this, "Ya existe una cuenta con el username que has introducido", Toast.LENGTH_LONG).show();
                Log.e("ERR", "Ya existe una cuenta con el username que has introducido");
                break;
            case -4:
                nombreCampo = getLongEditTextName(name,lastname,username,password1);
                Toast.makeText(this, "El campo "+nombreCampo+" es demasiado largo", Toast.LENGTH_LONG).show();
                Log.e("ERR", "Alguno de los campos introducidos es demasiado largo");
                break;
        }
    }

    private String getNullEditTextName(String name, String surname, String username, String password1) {
        if (name.isEmpty()) {
            return "Nombre";
        }
        if (surname.isEmpty()) {
            return "Apellido";
        }
        if (username.isEmpty()) {
            return "Nombre de Usuario";
        }
        if (password1.isEmpty()) {
            return "Contraseña (1)";
        }
        return "Contraseña (2)";
    }

    private String getLongEditTextName(String name, String surname, String username, String password1) {
        if (name.length()>30) {
            return "Nombre";
        }
        if (surname.length()>30) {
            return "Apellido";
        }
        if (username.length()>30) {
            return "Nombre de Usuario";
        }
        if (password1.length()>25) {
            return "Contraseña (1)";
        }
        return "Contraseña (2)";
    }
}
