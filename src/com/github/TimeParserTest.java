package com.github;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * JUnit Test that tests the TimeParser class given test cases in TimeParserTestFile.txt
 */
public class TimeParserTest {
    @Test
    public void testTimeParser(){
        File file = new File("TimeParserUnitTest.txt");
        ArrayList<String> strArr = parseText(file);

        //Get current time/date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        for(String str: strArr){
            str = str.trim();
            System.out.println("\t" + str);
            System.out.println("result: " + getFormattedDate(str) + "\n");
        }
    }

    /**
     * Retrieves the date given the text message string
     * @param str text message string
     * @return formatted date
     */
    public String getFormattedDate(String str){

        TimeParser tp = new TimeParser();
        long[] time = tp.GetInput(str);
        Date date0 = new Date(time[0]); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formatting (see comment at the bottom
        String formattedDate = sdf.format(date0);
        return formattedDate;

    }

    /**
     * Parses the test file to return an arraylist of the lines
     * @param file text file with the test cases
     * @return arraylist with lines of the test file
     */
    public ArrayList<String> parseText(File file){

        ArrayList<String> strArr = new ArrayList<String>();
        try {
            Scanner sc = new Scanner(file).useDelimiter("\n");
            while(sc.hasNext()){
                String line = sc.next();
                strArr.add(line);
            }
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.out.println("File not found");
            System.err.println(file.getAbsolutePath());
        }
        return strArr;
    }

}