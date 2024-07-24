package com.example.exergames_beta.util;

import com.example.exergames_beta.User;

import java.io.Serializable;

public class SuperObject implements Serializable {

    // OBJETO QUE CONTIENE TODOS LOS OBJETOS / ENTIDADES DE LA APP
    // SE USARA PARA PODER PASAR SU INFORMACION DE UN INTENT A OTRO

    // Version de serializacion para evitar errores
    private static final long serialVersionUID = 1L;
    public User user;
    public User userAux;
    public Game game;
    public int numFollowers;
    String aux;

    public SuperObject() {

    }
    public SuperObject(User u, Game g) {
        this.game = g;
        this.user = u;
    }

    // Getters y setters
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public int getNumFollowers() {
        return numFollowers;
    }
    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }
    public User getUserAux() {
        return userAux;
    }
    public void setUserAux(User userAux) {
        this.userAux = userAux;
    }
    public String getAux() {
        return aux;
    }
    public void setAux(String aux) {
        this.aux = aux;
    }
}
