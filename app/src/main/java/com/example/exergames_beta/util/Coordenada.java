package com.example.exergames_beta.util;

public class Coordenada {

    // VARIABLES Y ATRIBUTOS
    public float X;
    public float Y;

    // METHODS
    public Coordenada() {

    }
    public Coordenada(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    // Getters
    public float getX() {
        return this.X;
    }
    public float getY() {
        return this.Y;
    }

    // Setters
    public void setX(float Xcoord) {
        this.X = Xcoord;
    }
    public void setY(float Ycoord) {
        this.Y = Ycoord;
    }

    // toString para debugging
    @Override
    public String toString() {
        return "("+this.X+", "+this.Y+")";
    }

    // ---------------------------------------------------------------
    //      CALCULAR DISTANCIA ENTRE COORDENADAS
    // ---------------------------------------------------------------

    // Distancia entre dos coordenadas en valor absoluto
    public float getAbsoluteDistance(Coordenada b) {
        // d = sqrt((a.getX()- b.getX())^2 + (a.getY()-b.getY())^2)
        float deltaX = this.getX() - b.getX();
        float deltaY = this.getY() - b.getY();
        float distancia =  (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        return distancia;
    }
}
