package com.github;

import static com.github.StringUtils.normalize;

public class Main {

    public static void main(String[] args) {
        String message = "Let's meet at 5pm tomorrow";
        message = normalize(message);
        String message2 = "Let's meet at 5pm tomorrow";
        TimeComponent timeComponent = new TimeComponent(message);

        TimeParser tp = new TimeParser();
        long[] time = tp.GetInput(message2);
        System.out.println(time[0]);
        System.out.println(time[1]);
        System.out.println("!!!" + timeComponent.timeExpressionString);
        System.out.println("!!!" + timeComponent.timeKeywords);
    }
}
