package com.example.exergames_beta;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.exergames_beta.connection.DatabaseConnection;
import com.example.exergames_beta.util.SuperObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SearchResults extends AppCompatActivity {

    DatabaseConnection connection;
    private User user, selectedUser;
    private SuperObject superObject;
    private int numResultados = 0;
    private String searchedText;
    LinearLayout resultsLayout;
    private TextView searchedTextTV;
    private TextView numResultsTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchresults);

        connection = new DatabaseConnection();
        superObject = new SuperObject();

        // Obtener info de intent anterior
        Intent intent = getIntent();
        superObject = (SuperObject) intent.getSerializableExtra("superobject");
        searchedText = superObject.getAux();
        user = superObject.getUser();
        user.toString();

        // Obtener elementos de xml por su id
        searchedTextTV = findViewById(R.id.searchedText);
        numResultsTV = findViewById(R.id.numResultsTitle);
        resultsLayout = findViewById(R.id.resultsLayout);

        // Actualizar los elementos xml
        searchedTextTV.setText("Resultados para '"+searchedText+"'");

        // Buscar resultados
        searchUsers(searchedText);
        if (numResultados == 0) {
            resultsLayout.setVisibility(View.GONE);
            numResultsTV.setText("No se han encontrado usuarios");
        } else if (numResultados == 1) {
            numResultsTV.setText(numResultados+" Coincidencia");
        } else {
            numResultsTV.setText(numResultados+" Coincidencias");
        }
    }

    // on Clicks
    public void onClickExit(View view) {
        this.finish();
    }

    // Aux
    private void searchUsers(String search) {
        // Buscar usuarios que coincidan con la busqueda
        // Se buscan coincidencias en nombre, apellido o nombre de usuario
        int numR = 0;
        try {
            Connection con = connection.conexionBD();
            String callFunction = "SELECT * FROM search_user(?);";
            PreparedStatement pStatement = con.prepareStatement(callFunction);

            pStatement.setString(1, search);

            ResultSet rs = pStatement.executeQuery();

            // Crear elemento XML con el username
            while(rs.next()) {
                String uname = rs.getString("username");
                if (!uname.equals(user.getUsername())) {
                    final Button button = new Button(this);
                    button.setText(uname.toLowerCase());
                    button.setTextColor(ContextCompat.getColorStateList(this, R.color.white));
                    button.setTextSize(14);
                    button.setWidth(80);
                    button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue_UIB_3));

                    // Aplicar fondo con bordes redondeados
                    button.setBackgroundResource(R.drawable.roundedbutton);

                    // Añadir drawable a la izquierda del texto
                    Drawable leftDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_person_outline_white);
                    button.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

                    // Añadir listener al botón
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onUserButtonClick(uname);
                        }
                    });

                    // Crear LayoutParams para el botón con márgenes
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // o LinearLayout.LayoutParams.WRAP_CONTENT dependiendo de tus necesidades
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 16, 0, 16); // Márgenes (left, top, right, bottom)

                    // Asignar los LayoutParams al botón
                    button.setLayoutParams(params);

                    // Añadir boton a la vista
                    resultsLayout.addView(button);
                }
                numR++;
            }

        } catch (Exception e) {
            Log.e("Search Results exception at <searchUsers>",e.toString());
            e.printStackTrace();
            e.getMessage();

        } finally {
            numResultados = numR;
        }
    }

    public void onUserButtonClick(String username) {
        selectedUser = new User();

        // Obtener número de seguidores del user seleccionado
        Connection con;
        PreparedStatement pStatement;
        ResultSet rs;
        try {
            // Obtener numero de seguidores
            con = connection.conexionBD();
            String callFunction = "SELECT get_followers_count(?);";
            pStatement = con.prepareStatement(callFunction);
            pStatement.setString(1, username);
            rs = pStatement.executeQuery();

            if (rs.next()) {
                int numFollowers = rs.getInt(1);
                superObject.setNumFollowers(numFollowers);
            }

            // Obtener info detallada del usuario
            con = connection.conexionBD();
            callFunction = "SELECT * FROM findUsername(?);";
            pStatement = con.prepareStatement(callFunction);
            pStatement.setString(1, username);
            rs = pStatement.executeQuery();

            if (rs.next()) {
                String uname = rs.getString("username");
                String name = rs.getString("name");
                String surname = rs.getString("lastname");
                String psswd = rs.getString("password");
                int level = rs.getInt("level");
                float xp = rs.getFloat("xp");

                selectedUser = new User(name,surname,uname,psswd,level,xp);
            }
            selectedUser.toString();
            user.toString();
            superObject.setUserAux(selectedUser);
            superObject.setUser(user);

            // Cerrar conexiones
            rs.close();
            pStatement.close();
            con.close();

            Intent intent = new Intent(this, ProfileDetail.class);
            intent.putExtra("superobject", superObject);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("EXCEPTION IN onGoToProfile (Exergames Main): ",e.toString());
            e.printStackTrace();
            e.getMessage();
        }
    }
}
