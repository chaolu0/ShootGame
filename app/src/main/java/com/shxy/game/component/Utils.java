package com.shxy.game.component;


import java.util.Random;

public class Utils {
    private static Random random = new Random();

    public static int rand(int range) {
        return Math.abs(random.nextInt() % range);
    }
}
