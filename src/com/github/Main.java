package com.github;

public class Main {

    public static void main(String[] args) {
        TimeParser tp = new TimeParser();
        long[] time = tp.GetInput("Let's meet at 5pm tomorrow");
        System.out.println(time[0]);
        System.out.println(time[1]);
    }
}
