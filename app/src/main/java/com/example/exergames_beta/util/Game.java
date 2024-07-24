package com.example.exergames_beta.util;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable  {

    // Version de serializacion para evitar errores
    private static final long serialVersionUID = 1L;
    public String name, description;                            // Nombre y descripcion del juego
    public int difficulty, id;                                  // Dificultad del juego
    public ArrayList<CervicalCondition> cervicalConditions;     // Condiciones cervicales asociadas

    public Game() {
        cervicalConditions = new ArrayList<>();
    }
    public Game(String n, String d, int dif) {
        this.name = n;
        this.description = d;
        this.difficulty = dif;
        this.cervicalConditions = new ArrayList<>();
    }
    public Game(int id, String n, String d, int dif) {
        this.id = id;
        this.name = n;
        this.description = d;
        this.difficulty = dif;
        this.cervicalConditions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public ArrayList<CervicalCondition> getCervicalConditions() {
        return cervicalConditions;
    }
    public void setCervicalConditions(ArrayList<CervicalCondition> cervicalConditions) {
        this.cervicalConditions = cervicalConditions;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCervicalConditionsString() {
        String conditions = "";
        for (CervicalCondition cervCondition : cervicalConditions) {
            conditions += cervCondition.getName() + " ";
        }
        return conditions;
    }
}
