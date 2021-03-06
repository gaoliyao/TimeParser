package com.github;

import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 *
 * @author mars
 */
public class StringUtils {
    /**
     * Splits the sentence in to an arraylist of words
     * @param sentence Sentence to split
     * @return Arraylist of words in sentence
     */
    public static ArrayList<String> split(String sentence) {
        String words = "";
        ArrayList<String> wordList = new ArrayList<>();
        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == ' ' || i == sentence.length() - 1) {
                if (!words.equals("") || i == sentence.length() - 1) {
                    if (sentence.charAt(i) != ' ' && i == sentence.length() - 1)
                        wordList.add(words + sentence.charAt(i));
                    else {
                        wordList.add(words);
                        words = "";
                    }
                }
            } else {
                words = words + sentence.charAt(i);
            }
        }
        return wordList;
    }

    /**
     * Converts weekday strings to integer values
     * @param Weekday weekday string to convert to int
     * @return weekday as an integer
     */
    public static int weekdayToInteger(String Weekday) {
        if (Weekday.toLowerCase().equals("monday") || Weekday.toLowerCase().contains("mon"))
            return 1;
        else if (Weekday.toLowerCase().equals("tuesday") || Weekday.toLowerCase().contains("tues"))
            return 2;
        else if (Weekday.toLowerCase().equals("wednesday") || Weekday.toLowerCase().contains("wed"))
            return 3;
        else if (Weekday.toLowerCase().equals("thursday") || Weekday.toLowerCase().contains("thu"))
            return 4;
        else if (Weekday.toLowerCase().equals("friday") || Weekday.toLowerCase().contains("fri"))
            return 5;
        else if (Weekday.toLowerCase().equals("saturday") || Weekday.toLowerCase().contains("sat"))
            return 6;
        else if (Weekday.toLowerCase().equals("sunday") || Weekday.toLowerCase().contains("sun"))
            return 7;
        else
            return -1;
    }

    /**
     * Converts number strings to integers
     * @param str number string
     * @return integer representation of the given
     */
    public static String stringToInteger(String str) {
        if (str.toLowerCase().equals("one"))
            return "1";
        else if (str.toLowerCase().equals("two"))
            return "2";
        else if (str.toLowerCase().equals("three"))
            return "3";
        else if (str.toLowerCase().equals("four"))
            return "4";
        else if (str.toLowerCase().equals("five"))
            return "5";
        else if (str.toLowerCase().equals("six"))
            return "6";
        else if (str.toLowerCase().equals("seven"))
            return "7";
        else if (str.toLowerCase().equals("eight"))
            return "8";
        else if (str.toLowerCase().equals("nine"))
            return "9";
        else if (str.toLowerCase().equals("ten"))
            return "10";
        else if (str.toLowerCase().equals("eleven"))
            return "11";
        else if (str.toLowerCase().equals("twelve"))
            return "12";
        else
            return "-1";
    }

    /**
     * Normalizes the sentence to make it more parsable
     * @param sentence sentence to normalize
     * @return normalized sentence
     */
    public static String normalize(String sentence) {
        String normalized_sentence1 = "";
        String normalized_sentence2 = "";
        String normalized_sentence3 = "";
        sentence = sentence.trim();
        for (int i = 0; i < sentence.length(); i++) {
            if (isTemporalInteger("" + sentence.charAt(i)) && (i - 1) >= 0
                    && sentence.charAt(i - 1) != ' '
                    && !isTemporalInteger("" + sentence.charAt(i - 1))) {
                normalized_sentence1 = normalized_sentence1 + " "
                        + sentence.charAt(i);
            } else {
                normalized_sentence1 = normalized_sentence1
                        + sentence.charAt(i);
            }
        }

        for (int i = 0; i < normalized_sentence1.length(); i++) {
            if (isTemporalInteger("" + normalized_sentence1.charAt(i))
                    && (i + 1) < normalized_sentence1.length()
                    && normalized_sentence1.charAt(i + 1) != ' '
                    && !isTemporalInteger(""
                    + normalized_sentence1.charAt(i + 1))) {
                normalized_sentence2 = normalized_sentence2
                        + normalized_sentence1.charAt(i) + " ";
            } else {
                normalized_sentence2 = normalized_sentence2
                        + normalized_sentence1.charAt(i);
            }
        }

        for (int i = 0; i < normalized_sentence2.length(); i++) {
            if (contain(Constants.PUNCTUATION, "" + normalized_sentence2.charAt(i)))
                normalized_sentence3 = normalized_sentence3 + "";
            else
                normalized_sentence3 = normalized_sentence3
                        + normalized_sentence2.charAt(i);
        }
        return normalized_sentence3;
    }

    /**
     * Checks if string is a temporal integer
     * @param str string to check
     * @return True if string is temporal integer, False otherwise
     */
    public static boolean isTemporalInteger(String str) {
        if (Pattern.compile("^[-\\+]?[\\d]*$").matcher(str).matches()) {
            int time = Integer.parseInt(str);
            return !(time < 0 || time >= 2400);
        }
        return false;
    }

    /**
     * Checks if the content of two strings are equivalent (ignores casing)
     * @param Str first string to check
     * @param word second string to check
     * @return True if both values are the same, False otherwise
     */
    public static boolean is(String Str, String word) {
        return word.toLowerCase().equals(Str.toLowerCase());
    }

    /**
     * Checks if word is contained in given array
     * @param Str string array to check
     * @param word string to check
     * @return True if word is in array, False otherwise
     */
    public static boolean contain(String[] Str, String word) {
        for (String aStr : Str) {
            if (word.toLowerCase().equals(aStr.toLowerCase()))
                return true;
        }

        return false;
    }
}
