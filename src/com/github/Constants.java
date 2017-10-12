package com.github;

/**
 * All the string constants necessary for time parsing
 *
 * @author mars
 */
public class Constants {

    //Values for timeKeywords Arraylist
    public static String[] KEYWORDS_WEEKEND = {"weekend"};
    public static String[] DURATION = {"day", "week", "month", "year"};
    public static String[] KEYWORD_WEEKDAY = {"Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun", "Thu"};
    // MORNING FROM 06:00 TO 12:00// AFTERNOON FROM 12:00 TO 06:00// EVENING FROM 06:00 TO 23:59
    public static String[] KEYWORDS_EPOCH = {"morning", "afternoon",
            "evening", "night", "tonight", "noon", "morning", "lunch", "dinner"}; // ZJ -add lunch, dinner
    public static String[] KEYWORDS_AMPM = {"a.m.", "p.m.", "AM", "PM"};
    public static String[] KEYWORDS_FUTURE = {"next", "tomorrow", "tmw", "tmrw", "after", "today"};
    public static String[] KEYWORDS_PAST = {"last", "yesterday", "before"};

    public static String KEYWORD_COLON = ":";
    public static String IN = "in";
    public static String AT = "at";
    public static String[] PUNCTUATION = {"!", ",", ".", "?", "-"};

    //Keys for timeKeywords ArrayList
    public static final String WEEKDAY = "weekday";
    public static final String WEEKEND = "weekend";
    public static final String EPOCH = "epoch";
    public static final String AMPM = "ampm";
    public static final String BY = "by";
    public static final String FUTURE = "future";
    public static final String PAST = "past";
    public static final String DIGIT = "digit";
    public static final String DURATION_KEYWORD = "duration_keyword";
    public static final String AMPM_PREV = "ampm_prev";
    public static final String AMPM_PREV2 = "ampm_prev2";
    public static final String AT_NEXT = "at_next";
    public static final String BY_NEXT = "by_next";
    public static final String IN_NEXT = "in_next";
    public static final String IN_NEXT2 = "in_next2";
}
