package com.example.exergames_beta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.exergames_beta.connection.DatabaseConnection;
import com.example.exergames_beta.util.SuperObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.Objects;

public class ProfileDetail extends AppCompatActivity {

    // VARIABLES AND CONSTRAINTS
    private User user, userAux, sessionUser;
    private EditText nameET,lastnameET,usernameEt,passwordET;
    private TextView usernameTV;
    private TextView followersTV;
    private TextView levelTV;
    private TextView currrentXP;
    private TextView remainingXP;
    private TextView passwordTitle;
    private TextView titlePersonalInfo;
    private ProgressBar progressBar;
    private SuperObject superObject;
    private Button followButton;
    private Button exitButton2;
    private ConstraintLayout cLayout;
    private int numFollowers;
    private int infoStatus = 0;
    private boolean following;
    DatabaseConnection connection;

    // ON CREATE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profiledetail);

        User showInfoUser;
        connection = new DatabaseConnection();
        superObject = new SuperObject();

        // Obtener info de intent anterior
        Intent intent = getIntent();
        superObject = (SuperObject) intent.getSerializableExtra("superobject");
        user = superObject.getUser();
        userAux = superObject.getUserAux();
        user.toString();
        sessionUser = new User();

        // Obtener edit texts y text views
        nameET = findViewById(R.id.name);
        lastnameET = findViewById(R.id.lastname);
        usernameEt = findViewById(R.id.username);
        passwordET = findViewById(R.id.passwordEditText);
        passwordTitle = findViewById(R.id.passwordTitle);
        titlePersonalInfo = findViewById(R.id.datosPersonalesTitle);

        exitButton2 = findViewById(R.id.buttonExit2);
        cLayout = findViewById(R.id.constraintLayout4);

        // Boton de seguir
        followButton = findViewById(R.id.followUserButton);

        // Mirar si user sigue a userAux
        following = checkIfFollowing(user, userAux);

        // Establecer diferencias entre perfil propio y otros perfiles
        if (user.getUsername().equals(userAux.getUsername())) {
            // Perfil propio
            showInfoUser = user;

            cLayout.setVisibility(View.VISIBLE);
            exitButton2.setVisibility(View.GONE);

            titlePersonalInfo.setText("Tus datos personales");

            followButton.setVisibility(View.GONE);  // Oculto y sin ocupar espacio
            //followButton.setVisibility(View.INVISIBLE); // Lo oculta pero sigue ocupando espacio

            // Edit text editables
            nameET.setText(user.getName());
            nameET.setFocusable(true);
            nameET.setFocusableInTouchMode(true);
            lastnameET.setText(user.getSurname());
            lastnameET.setFocusable(true);
            lastnameET.setFocusableInTouchMode(true);
            usernameEt.setText(user.getUsername());
            usernameEt.setFocusable(true);
            usernameEt.setFocusableInTouchMode(true);
            passwordET.setText(user.getPassword());
            passwordET.setFocusable(true);
            passwordET.setFocusableInTouchMode(true);

        } else {
            showInfoUser = userAux;

            cLayout.setVisibility(View.GONE);
            exitButton2.setVisibility(View.VISIBLE);

            titlePersonalInfo.setText("Datos de "+showInfoUser.getName());

            followButton.setVisibility(View.VISIBLE);
            // Edit text no editables
            nameET.setFocusable(false);
            nameET.setFocusableInTouchMode(false);
            lastnameET.setFocusable(false);
            lastnameET.setFocusableInTouchMode(false);
            usernameEt.setFocusable(false);
            usernameEt.setFocusableInTouchMode(false);

            passwordTitle.setVisibility(View.GONE);
            passwordET.setVisibility(View.GONE);

            // Campo de contraseña oculto


            // Comprobar si se sigue a ese usuario
            if (following) {
                followButton.setText("Siguiendo");
                followButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blueFollowed));
            } else {
                followButton.setText("Seguir");
                followButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.greenFollowers));
            }

        }

        TextView nameAndLastnameTV = findViewById(R.id.NameAndLastnameTextView);
        usernameTV = findViewById(R.id.usernameTextView);
        followersTV = findViewById(R.id.followersTextView);
        levelTV = findViewById(R.id.levelValue);
        currrentXP = findViewById(R.id.currentXPValue);
        remainingXP = findViewById(R.id.remainingXPValue);

        progressBar = findViewById(R.id.progressBarXP);

        // Cargar info del usuario en las views
        try {
            if (!showInfoUser.getUsername().isEmpty()) {
                // Nombre y apellidos
                String s = showInfoUser.getName()+" "+showInfoUser.getSurname();
                nameAndLastnameTV.setText(s);

                // Nombre de usuario
                usernameTV.setText(showInfoUser.getUsername());

                // Followers
                numFollowers = superObject.getNumFollowers();
                if (numFollowers == 1) {
                    s = numFollowers+" Seguidor";
                } else {
                    s = numFollowers+" Seguidores";
                }
                followersTV.setText(s);

                // Nivel y xp
                levelTV.setText(String.valueOf(showInfoUser.getLevel()));
                currrentXP.setText(String.valueOf(showInfoUser.getXp()));
                float rmXP = progressBar.getMax()-showInfoUser.getXp();
                remainingXP.setText(String.valueOf(rmXP));

                // Progress bar
                int progress = (int) showInfoUser.getXp().longValue();
                progressBar.setProgress(progress);

                // Edit Text
                nameET.setText(showInfoUser.getName());
                lastnameET.setText(showInfoUser.getSurname());
                usernameEt.setText(showInfoUser.getUsername());
                passwordET.setText(showInfoUser.getPassword());
            }
        } catch (NullPointerException npe) {
            Log.e("NULL POINTER EXCEPTION", "SE HA PRODUCIDO UN ERROR POR APUNTAR A UNA REFERENCIA NULA");
            npe.printStackTrace();
            npe.getMessage();
        } catch (Exception e) {
            Log.e("UNCAUGHT EXCEPTION", Objects.requireNonNull(e.getLocalizedMessage()));
            e.printStackTrace();
            e.getMessage();
        }
    }

    // ON CLICKS
    public void onClickExit(View view) {
        this.finish();
    }

    public void onClickSave(View view) {
        // Validar info introducida
        String name, lastname, username, password;
        name = nameET.getText().toString();
        lastname = lastnameET.getText().toString();
        username = usernameEt.getText().toString();
        password = passwordET.getText().toString();

        // Usuario temporal con la info de la sesion
        sessionUser = new User(name, lastname, username, password);

        if (infoOK(name,lastname,username,password)) {
            // Info validada, mensaje de confirmacion
            String title = "Advertencia";
            String message = "¿Estás seguro de que deseas actualizar la información de tu perfil?";
            showConfirmationDialog(title,message);
        } else {
            showToastError(infoStatus);
        }
    }

    public void gotoExergames(User user) {
        Intent intent = new Intent(this, ExergamesMain.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    public void onFollowUnfollowUser(View view) {
        String callFunction = "";

        if (following) {
            // User quiere dejar de seguir a userAux
            followButton.setText("Seguir");
            followButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.greenFollowers));
            Toast.makeText(this, "Has dejado de seguir a "+userAux.getUsername(), Toast.LENGTH_LONG).show();

            // Actualizar numero de followers
            numFollowers--;
            String s = "";
            superObject.setNumFollowers(numFollowers);
            if (numFollowers == 1) {
                s = numFollowers+" Seguidor";
            } else {
                s = numFollowers+" Seguidores";
            }
            followersTV.setText(s);

            // Actualizar following
            following = false;

            // Preparar llamada
            callFunction += "SELECT unfollow_user(?,?);";
        } else {
            // User quiere seguir a userAux
            followButton.setText("Siguiendo");
            followButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blueFollowed));
            Toast.makeText(this, "Ahora sigues a "+userAux.getUsername(), Toast.LENGTH_LONG).show();

            // Actualizar numero de followers
            numFollowers++;
            String s = "";
            superObject.setNumFollowers(numFollowers);
            if (numFollowers == 1) {
                s = numFollowers+" Seguidor";
            } else {
                s = numFollowers+" Seguidores";
            }
            followersTV.setText(s);

            // Actualizar following
            following = true;

            // Preparar llamada
            callFunction += "SELECT follow_user(?,?);";
        }
        followUnfollowUser(user.getUsername(), userAux.getUsername(), callFunction);
    }

    // Aux methods
    private void updateUser() {
        boolean updateSuccessful  = false;
        try {
            Connection con = connection.conexionBD();
            String callFunction = "SELECT update_user_profile(?,?,?,?,?,?);";
            PreparedStatement pStatement = con.prepareStatement(callFunction);

            pStatement.setString(1, sessionUser.getUsername());
            pStatement.setString(2, sessionUser.getName());
            pStatement.setString(3, sessionUser.getSurname());
            pStatement.setString(4, sessionUser.getPassword());
            pStatement.setInt(5, user.getXp().intValue());
            pStatement.setInt(6, user.getLevel());

            ResultSet rs = pStatement.executeQuery();

            if (rs.next()) {
                updateSuccessful  = rs.getBoolean(1);
                if (updateSuccessful) {
                    Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Ha ocurrido un error al actualizar el perfil", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            Log.e("Profile detail exception at <updateUser>",e.toString());
            e.printStackTrace();
            e.getMessage();
        } finally {
            // Actualizar usuario
            if (updateSuccessful) {
                this.user.setName(sessionUser.getName());
                this.user.setSurname(sessionUser.getSurname());
                this.user.setUsername(sessionUser.getUsername());
                this.user.setPassword(sessionUser.getPassword());

                // Volver a la vista anterior
                gotoExergames(user);
            }
        }
    }

    private void followUnfollowUser(String username1, String username2, String callFunction) {
        try {
            Connection con = connection.conexionBD();
            PreparedStatement pStatement = con.prepareStatement(callFunction);
            pStatement.setString(1, username1);
            pStatement.setString(2, username2);
            pStatement.executeQuery();
            pStatement.close();
            con.close();

        } catch (Exception e) {
            Log.e("Profile detail exception at <followUnfollowUser>",e.toString());
            e.printStackTrace();
            e.getMessage();
        }
    }

    private void showConfirmationDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción para el botón "Sí"
                updateUser();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción para el botón "No"
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean infoOK(String name, String surname, String username, String password) {
        /*
        CÓDIGOS DE ERROR:
        infoStatus = -1 ---> Contraseñas no coincidentes en el EditText
        infoStatus = -2 ---> Algún campo está vacío
        infoStatus = -3 ---> Ya existe alguna cuenta con ese username
        infoStatus = -4 ---> Alguno de los campos es demasiado largo y se violaria una CONSTRAINT en BD
         */

        // Comprobar si algún campo está en blanco
        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            infoStatus = -2;
            return false;
        }

        // Comprobar si ya existe alguna cuenta con ese username
        if (!username.equals(user.getUsername())) {
            if (usernameAlreadyExists(username, password)) {
                infoStatus = -3;
                return false;
            }
        }

        // Comprobar longitud de los campos
        if (username.length()>30 || name.length()>30 || surname.length()>30 || password.length()>25) {
            infoStatus = -4;
            return false;
        }

        // Toda la info es correcta
        return true;
    }

    private boolean usernameAlreadyExists(String u, String p) {
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
            Log.e("Profile detail exception at <usernameAlreadyExists>",e.toString());
            e.printStackTrace();
            e.getMessage();

        } finally {
            return exists;
        }
    }

    private void showToastError(int status) {
        String name = sessionUser.getName();
        String lastname = sessionUser.getSurname();
        String username = sessionUser.getUsername();
        String password = sessionUser.getPassword();
        String nombreCampo = "";

        // Mostrar errores en función del valor de infoStatus
        switch (status) {
            case -1:
                Toast.makeText(this, "Las contraseñas introducidas no coinciden", Toast.LENGTH_LONG).show();
                Log.e("ERR", "Las contraseñas introducidas no coinciden");
                break;
            case -2:
                // Descubrir qué campo esta vacío
                nombreCampo = getNullEditTextName(name,lastname,username);
                Toast.makeText(this, "El campo '"+nombreCampo+"' está vacío", Toast.LENGTH_LONG).show();
                Log.e("ERR", "El campo '"+nombreCampo+"' está vacío");
                break;
            case -3:
                Toast.makeText(this, "Ya existe una cuenta con el username que has introducido", Toast.LENGTH_LONG).show();
                Log.e("ERR", "Ya existe una cuenta con el username que has introducido");
                break;
            case -4:
                nombreCampo = getLongEditTextName(name,lastname,username,password);
                Toast.makeText(this, "El campo "+nombreCampo+" es demasiado largo", Toast.LENGTH_LONG).show();
                Log.e("ERR", "Alguno de los campos introducidos es demasiado largo");
                break;
        }
    }

    private String getNullEditTextName(String name, String surname, String username) {
        if (name.isEmpty()) {
            return "Nombre";
        }
        if (surname.isEmpty()) {
            return "Apellido";
        }
        if (username.isEmpty()) {
            return "Nombre de usuario";
        }
        return "Contraseña";
    }

    private boolean checkIfFollowing(User user, User userAux) {
        boolean follows = false;
        // Comprobar si user sigue a userAux
        try {
            Connection con = connection.conexionBD();
            String callFunction = "SELECT * FROM is_following(?,?);";
            PreparedStatement pStatement = con.prepareStatement(callFunction);

            pStatement.setString(1, user.getUsername());
            pStatement.setString(2, userAux.getUsername());

            ResultSet rs = pStatement.executeQuery();

            if (rs.next()) {
                follows  = rs.getBoolean(1);
            }
        } catch (Exception e) {
            Log.e("Profile detail exception at <checkIfFollowing>",e.toString());
            e.printStackTrace();
            e.getMessage();
        } finally {
            return follows;
        }
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