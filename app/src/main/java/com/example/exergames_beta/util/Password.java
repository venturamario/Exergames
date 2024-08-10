package com.example.exergames_beta.util;
import java.util.Random;

public class Password {
    // VARIABLES
    public String token;
    public int seed;
    public int password_length;

    // CONSTANTES
    private static final boolean USE_SEPARATORS = true;
    private static final char[] charMinuscula = "abcdefghijklmnñopqrstuvwxyz".toCharArray();
    private static final char[] charMayuscula = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ".toCharArray();
    private static final char[] numbers = "0123456789".toCharArray();
    private static final char[] separators = ",.;:-_()[]'".toCharArray();

    // Constructores
    public Password() {

    }
    public Password (int s, String tk) {
        this.token = tk;
        this.seed = s;
    }
    public Password (int s) {
        this.seed = s;
    }

    // Getters
    public int getSeed() {
        return this.seed;
    }
    public String getToken() {
        return this.token;
    }

    // Setters
    public void setSeed(int s) {
        this.seed = s;
    }
    public void setToken(String tk) {
        this.token = tk;
    }
    public void setPasswordLength(int length) {
        this.password_length = length;
    }

    //Generate random numbers and iterate
    public static Password generatePassword(int seed, int length, boolean withSeparators) {
        String token = "";
        // Random number based on seed
        Random r = new Random(seed);

        //Random character generation since password.length = length
        for(int index=0; index<length; index++) {
            // characterChooser for choosing a character inside the array
            int characterChooser;
            // Choosing one of the declared arrays
            int arrayChooser;
            if (withSeparators) {
                arrayChooser = 1 + r.nextInt(4);
            } else {
                arrayChooser = 1 + r.nextInt(3);
            }

            switch(arrayChooser) {
                case 1:
                    // charMinuscula
                    characterChooser = r.nextInt(charMinuscula.length-1);
                    token+=charMinuscula[characterChooser];
                    break;
                case 2:
                    // charMayuscula
                    characterChooser = r.nextInt(charMayuscula.length-1);
                    token+=charMayuscula[characterChooser];
                    break;
                case 3:
                    // numbers
                    characterChooser = r.nextInt(numbers.length-1);
                    token+=numbers[characterChooser];
                    break;
                case 4:
                    // separators
                    characterChooser = r.nextInt(separators.length-1);
                    token+=separators[characterChooser];
                    break;
            }
        }
        // Creating new object password
        Password p = new Password(seed, token);
        // Returning password with its seed and token setted
        return p;
    }
}
