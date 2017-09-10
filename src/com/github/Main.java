package com.github;

import java.util.Scanner;

import static com.github.StringUtils.normalize;

public class Main {

    public static void main(String[] args) {
        String message = "Let's meet at 1pm tomorrow";
        message = normalize(message);
        String message2 = "Let's meet at 1pm tomorrow";
        TimeComponent timeComponent = new TimeComponent();

        TimeParser tp = new TimeParser();
        long[] time = tp.GetInput(message2);
        System.out.println(time[0]);
        System.out.println(time[1]);

    }
}
