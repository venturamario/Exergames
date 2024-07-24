package com.example.exergames_beta;

import android.util.Log;
import java.io.Serializable;

public class User implements Serializable {
    // CLASE USUARIO QUE REPRESENTA A UN USUARIO DE LA APP

    // Version de serializacion para evitar errores
    private static final long serialVersionUID = 1L;
    String name,surname,username,password;
    Float xp;
    Integer level;

    // MÉTODOS
    public User() {}
    public User(String n, String s, String us, String p) {
        name = n;
        surname = s;
        username = us;
        password = p;
        xp = (float) 0;
        level = 0;
    }

    public User(String name, String lastname, String username, String password, int level, float xp) {
        this.name = name;
        this.surname = lastname;
        this.username = username;
        this.password = password;
        this.level = level;
        this.xp = xp;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Float getXp() {
        return xp;
    }

    public void setXp(Float xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    // Imprimir info del usuario
    public String toString() {
        String s = "";
        s += "NOMBRE: "+this.name+", APELLIDO: "+this.surname+", NOMBRE DE USUARIO: "+this.username;
        s += ", NIVEL: "+this.level+", PUNTOS DE XP: "+this.xp;
        Log.i("User toString()", s);
        return s;
    }
}
