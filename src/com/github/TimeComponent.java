package com.github;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.StringUtils.*;
import static com.github.StringUtils.isTemporalInteger;
import static com.github.StringUtils.stringToInteger;

/**
 * Created by mars on 9/5/17.
 */
public class TimeComponent {
    // 0 : digit
    // 1 : Weekday
    // 2 : Epoch
    // 3 : ampm
    // 4 : words_today
    // 5 : words_tomorrow
    // 6 : words_in
    // 7 : Count of ":"
    // 8 : weekend

    String sentence = "";
    String timeExpressionString = "";


    TimeComponent(String sentence){
        this.sentence = sentence;
        timeKeywords.put(Constants.WEEKDAY, new ArrayList());
        timeKeywords.put(Constants.WEEKEND, new ArrayList());
        timeKeywords.put(Constants.EPOCH, new ArrayList());
        timeKeywords.put(Constants.AMPM, new ArrayList());
        timeKeywords.put(Constants.AT, new ArrayList());
        timeKeywords.put(Constants.BY, new ArrayList());
        timeKeywords.put(Constants.FUTURE, new ArrayList());
        timeKeywords.put(Constants.PAST, new ArrayList());
        timeKeywords.put(Constants.IN, new ArrayList());
        timeKeywords.put(Constants.DIGIT, new ArrayList());
        timeKeywords.put(Constants.DURATION_KEYWORD, new ArrayList());
        timeKeywords.put(Constants.AMPM_PREV, new ArrayList());
        timeKeywords.put(Constants.AMPM_PREV2, new ArrayList());
        timeKeywords.put(Constants.AT_NEXT, new ArrayList());
        timeKeywords.put(Constants.BY_NEXT, new ArrayList());
        timeKeywords.put(Constants.IN_NEXT, new ArrayList());
        timeKeywords.put(Constants.IN_NEXT2, new ArrayList());
    }

    public void extractTemporalKeywords(String sentence) {

        if (sentence.isEmpty())
            return;

        int count = 0;
        ArrayList<String> wordList = split(sentence);

        for (int i = 0; i < wordList.size(); i++) {

            if (wordList.get(i).isEmpty()) {
                break;
            } else if (isTemporalInteger(wordList.get(i))) {
                addTimeKeyword(Constants.DIGIT, wordList.get(i));

            } else if (!stringToInteger(wordList.get(i)).equals("-1")) {
                addTimeKeyword(Constants.DIGIT, wordList.get(i));
            } else if (isEpoch(wordList.get(i))) {
                if (!contain(Constants.KEYWORDS_EPOCH, wordList.get(i).toLowerCase())
                        || (i - 1) < 0
                        || !wordList.get(i - 1).toLowerCase().equals("good")) {
                    addTimeKeyword(Constants.EPOCH, wordList.get(i).toLowerCase());
                }
            } else if (isWeekday(wordList.get(i))) {
                addTimeKeyword(Constants.WEEKDAY, wordList.get(i));
            } else if (isWeekend(wordList.get(i))) {
                addTimeKeyword(Constants.WEEKEND, wordList.get(i));
            } else if (isAmPm(wordList.get(i))) {
                if (isTemporalInteger(wordList.get(i - 1))) {
                    addTimeKeyword(Constants.AMPM, wordList.get(i));
                    //AmPMKeywords.add(wordList.get(i));
                    if (Integer.parseInt(wordList.get(i - 1)) < 1200) {
                        if (!timeKeywords.containsKey(Constants.AMPM_PREV)) {
                            addTimeKeyword(Constants.AMPM_PREV, wordList.get(i - 1));
                        } else {
                            addTimeKeyword(Constants.AMPM_PREV2, wordList.get(i - 1));
                        }
                    }
                }
            } else if (isWordAt(wordList.get(i)) && (i + 1) < wordList.size()
                    && wordList.get(i + 1) != null) {
                if (isTemporalInteger(wordList.get(i + 1))) {
                    addTimeKeyword(Constants.AT, wordList.get(i));
                    addTimeKeyword(Constants.AT_NEXT, wordList.get(i + 1));
                }
            } else if (isWordBy(wordList.get(i)) && (i + 1) < wordList.size() && wordList.get(i + 1) != null) {
                if (isTemporalInteger(wordList.get(i + 1))) {
                    addTimeKeyword(Constants.BY, wordList.get(i));
                    addTimeKeyword(Constants.BY_NEXT, wordList.get(i + 1));
                }
            } else if (isFutureWord(wordList.get(i))) {
                addTimeKeyword(Constants.FUTURE, wordList.get(i));
                if (i + 1 <= wordList.size() - 1) {
                    if (isDuration(wordList.get(i + 1)))
                        addTimeKeyword(Constants.DURATION_KEYWORD, wordList.get(i + 1));
                }
            } else if (isPastWord(wordList.get(i))) {
                addTimeKeyword(Constants.PAST, wordList.get(i));
                if (i + 1 <= wordList.size() - 1) {
                    if (isDuration(wordList.get(i + 1)))
                        addTimeKeyword(Constants.DURATION_KEYWORD, wordList.get(i + 1));
                }
            } else if (isWordIn(wordList.get(i))) {
                if (wordList.get(i).equals(Constants.IN) && (i + 1) < wordList.size()
                        && wordList.get(i + 1) != null
                        && (i + 2) < wordList.size()
                        && wordList.get(i + 2) != null) {

                    if (isTemporalInteger(wordList.get(i + 1))) {
                        addTimeKeyword(Constants.IN_NEXT, wordList.get(i + 1));
                        addTimeKeyword(Constants.IN_NEXT2, wordList.get(i + 2));
                    } else if (!stringToInteger(wordList.get(i + 1)).equals("-1")) {
                        addTimeKeyword(Constants.IN_NEXT, stringToInteger(wordList.get(i + 1)));
                        addTimeKeyword(Constants.IN_NEXT2, wordList.get(i + 2));
                    }
                }
                addTimeKeyword(Constants.IN, stringToInteger(wordList.get(i)));
            } else if (isColon(wordList.get(i))) {
                if ((i - 1) >= 0 && (i + 1) < wordList.size()
                        && isTemporalInteger(wordList.get(i - 1))
                        && isTemporalInteger(wordList.get(i + 1))) {
                    colonPosition[2 * count] = wordList.get(i - 1);
                    colonPosition[2 * count + 1] = wordList.get(i + 1);
                    count++;
                }
            }
        }
        if (timeKeywords.containsKey(Constants.WEEKDAY)) {
            if (timeKeywords.get(Constants.WEEKDAY).contains("Sun") || timeKeywords.get(Constants.WEEKDAY).contains("Sat") || timeKeywords.get(Constants.WEEKDAY).contains("Sunday")
                    || timeKeywords.get(Constants.WEEKDAY).contains("Saturday")){
                timeKeywords.get(Constants.WEEKEND).clear();
            }
        }
        colonCount = count;
    }
    public static int colonCount;
    public static String[] colonPosition = {"", "", "", "", "", ""};
    public HashMap<String, ArrayList<String>> timeKeywords = new HashMap<>();



    private void addTimeKeyword(String key, String value){
        if (!timeKeywords.containsKey(key)) {
            ArrayList<String> values = new ArrayList<>();
            values.add(value);
            timeKeywords.put(key, values);
        }
        else {
            timeKeywords.get(key).add(value);
        }
        timeExpressionString = timeExpressionString + value + " ";
    }


    public boolean isUpperAlpha(char c) {
        return 'A' <= c && c <= 'Z';
    }

    public boolean isColon(String s) {
        return s.equals(Constants.KEYWORD_COLON);
    }

    public boolean isWordIn(String s) {
        return is(Constants.IN, s);
    }

    public boolean isFutureWord(String s) {
        return contain(Constants.KEYWORDS_FUTURE, s);
    }

    public boolean isPastWord(String s) {
        return contain(Constants.KEYWORDS_PAST, s);
    }

    public boolean isDuration(String s) {
        return contain(Constants.DURATION, s);
    }

    public boolean isWordAt(String s) {
        return is(Constants.AT, s);
    }

    public boolean isWordBy(String s) {
        return is(Constants.BY, s);
    }

    public boolean isAmPm(String s) {
        return contain(Constants.KEYWORDS_AMPM, s);
    }

    public boolean isWeekday(String s) {
        return contain(Constants.KEYWORD_WEEKDAY, s);
    }

    public boolean isWeekend(String s) {
        return contain(Constants.KEYWORDS_WEEKEND, s);
    }

    public boolean isEpoch(String str) {
        return contain(Constants.KEYWORDS_EPOCH, str);
    }

    public boolean containsEpoch() {
        return !timeKeywords.get(Constants.EPOCH).isEmpty();
    }

    public boolean containsWeekday() {
        return !timeKeywords.get(Constants.WEEKDAY).isEmpty();
    }

    public boolean containsWeekend() {
        return !timeKeywords.get(Constants.WEEKEND).isEmpty();
    }

    public boolean containsTemporalDigits() {

        return !timeKeywords.get(Constants.DIGIT).isEmpty();
    }

    public boolean containsPast() {
        return !timeKeywords.get(Constants.PAST).isEmpty();
    }

    public boolean containsFuture() {
        return !timeKeywords.get(Constants.FUTURE).isEmpty();
    }

    public boolean containsAmPm() {
        return !timeKeywords.get(Constants.AMPM).isEmpty();
    }

    public boolean containsColon() {
        return colonCount == 1 && !colonPosition[0].equals("")
                && !colonPosition[1].equals("");
    }

    public boolean containsTime() {
        return containsEpoch() || containsPast() || containsFuture()
                || containsAmPm() || containsWeekday() || containsWeekend()
                || containsTemporalDigits();
    }

    public boolean isAM() {
        if (timeKeywords.containsKey(Constants.AMPM)){
            String str = (String) timeKeywords.get(Constants.AMPM).get(0);
            return str.equals("a.m.") || str.equals("am");
        }
        return false;
    }

    public boolean isPM() {
        if (timeKeywords.containsKey(Constants.AMPM)){
            String str = (String) timeKeywords.get(Constants.AMPM).get(0);
            return str.equals("p.m.") || str.equals("pm");
        }
        return false;
    }

}
