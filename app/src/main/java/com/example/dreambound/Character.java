package com.example.dreambound;


import android.graphics.Canvas;

public class Character extends GameObject {
    //Stats class
    public static class Stats{
        int Attack = 10;
        int Defense = 10;
        int SpAttack = 10;
        int SpDefense = 10;
        int Health = 50;
        //stats stored in BIN for easy file I/O
        byte[] statsBIN = {(byte) Attack, (byte) Defense, (byte)SpAttack, (byte)SpDefense, (byte)Health };
    }
    //stats variable
    Stats stats;
    //Constructor
    Character(float x, float y, float width, float height) {
        super(x, y, width, height);

        stats = new Stats(); //default character Stats
    }


}



