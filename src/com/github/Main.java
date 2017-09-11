package com.github;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import static com.github.StringUtils.normalize;

public class Main {

    public static void main(String[] args) {
        String message2 = "Meet tomorrow?";
        TimeComponent timeComponent = new TimeComponent();

        TimeParser tp = new TimeParser();
        long[] time = tp.GetInput(message2);

        Date date0 = new Date(time[0]); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date0);
        System.out.println(formattedDate);

        if (time[1] == -1){
            System.out.println("-1");
        }
        else {
            Date date1 = new Date(time[1]); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
            String formattedDate1 = sdf1.format(date1);
            System.out.println(formattedDate1);
        }

    }
}
