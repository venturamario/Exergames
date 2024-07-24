package com.example.exergames_beta.games.FlappyBird;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

public class Bird extends BaseObject {

    // VARIABLES AND CONSTANTS
    private ArrayList<Bitmap> arrBms = new ArrayList<>();

    public Bird() { }

    public void draw (Canvas canvas) {
        canvas.drawBitmap(this.getBm(), this.x, this.y, null);
    }

    public ArrayList<Bitmap> getArrBms() {
        return arrBms;
    }

    public void setArrBms(ArrayList<Bitmap> arrBms) {
        this.arrBms = arrBms;
        for (int i=0; i<arrBms.size(); i++) {
            this.arrBms.set(i, Bitmap.createScaledBitmap(this.arrBms.get(i), this.width, this.height, true));
        }
    }

    @Override
    public Bitmap getBm() {
        // Intenta coger el primer bitmap del array de bitmaps
        return this.getArrBms().get(0);
    }
}