package com.example.exergames_beta.connection;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnection {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    Connection conexion;
    String url = "jdbc:postgresql://10.0.2.2:5432/exergames";
    String user = "postgres";
    String password = "root";

    // Funcion para conectar a postgres
    public Connection conexionBD() {

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
                conexion = null;
                try {
                    conexion = DriverManager.getConnection(url,user,password);
                    Log.i("INFO","CONNECTION: "+conexion.toString());
                } catch (Exception e) {
                    Log.e("BD CONNECTION ERROR",e.toString());
                    e.printStackTrace();
                    e.getMessage();
                }
        return conexion;
    }

    // Cerrar conexion
    public void cerrarConexion(Connection con) throws Exception {
        con.close();
    }
}
