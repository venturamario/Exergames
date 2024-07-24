package com.example.exergames_beta.util;

import java.io.Serializable;

public class CervicalCondition implements Serializable {

    // Version de serializacion para evitar errores
    private static final long serialVersionUID = 1L;
    String name;

    public CervicalCondition(){}
    public CervicalCondition(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
