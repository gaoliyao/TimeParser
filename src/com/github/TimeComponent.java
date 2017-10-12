package com.github;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.StringUtils.is;
import static com.github.StringUtils.split;
import static com.github.StringUtils.isTemporalInteger;
import static com.github.StringUtils.stringToInteger;
import static com.github.StringUtils.contain;

/**
 * Represents the time component of the text string
 * @author mars
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
    public static String timeExpressionString = "";
    public static HashMap<String, ArrayList<String>> timeKeywords = new HashMap<>();
    public static int colonCount;
    public static String[] colonPosition = {"", "", "", "", "", ""};

    /**
     * Initializes the timeKeywords arrayList such that each key points to an empty arraylist
     */
    TimeComponent(){
        timeExpressionString = "";
        timeKeywords.put(Constants.WEEKDAY, new ArrayList<String>());
        timeKeywords.put(Constants.WEEKEND, new ArrayList<String>());
        timeKeywords.put(Constants.EPOCH, new ArrayList<String>());
        timeKeywords.put(Constants.AMPM, new ArrayList<String>());
        timeKeywords.put(Constants.AT, new ArrayList<String>());
        timeKeywords.put(Constants.BY, new ArrayList<String>());
        timeKeywords.put(Constants.FUTURE, new ArrayList<String>());
        timeKeywords.put(Constants.PAST, new ArrayList<String>());
        timeKeywords.put(Constants.IN, new ArrayList<String>());
        timeKeywords.put(Constants.DIGIT, new ArrayList<String>());
        timeKeywords.put(Constants.DURATION_KEYWORD, new ArrayList<String>());
        timeKeywords.put(Constants.AMPM_PREV, new ArrayList<String>());
        timeKeywords.put(Constants.AMPM_PREV2, new ArrayList<String>());
        timeKeywords.put(Constants.AT_NEXT, new ArrayList<String>());
        timeKeywords.put(Constants.BY_NEXT, new ArrayList<String>());
        timeKeywords.put(Constants.IN_NEXT, new ArrayList<String>());
        timeKeywords.put(Constants.IN_NEXT2, new ArrayList<String>());
    }

    /**
     * Looks for temporal words in the given sentence and adds them to timeKeywords
     * @param sentence The text string containing potential temporal keywords
     */
    public void extractTemporalKeywords(String sentence) {

        this.sentence = sentence;
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
                    if (Integer.parseInt(wordList.get(i - 1)) < 2400) {
                        if (timeKeywords.get(Constants.AMPM_PREV).isEmpty()) {
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
            } else if (isWordBy(wordList.get(i)) && (i + 1) < wordList.size()
                    && wordList.get(i + 1) != null) {
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



    /**
     * Adds keyword to timeExpressionString and timeKeywords
     * @param key
     * @param value
     */
    public void addTimeKeyword(String key, String value){
        if (!timeExpressionString.contains(value)) {
            timeExpressionString = timeExpressionString + value + " ";
        }
        timeKeywords.get(key).add(value);
    }
    /**
     * Checks if s is a colon
     * @param s string to check
     * @return True if is a colon, False otherwise
     */
    public boolean isColon(String s) {
        return s.equals(Constants.KEYWORD_COLON);
    }

    /**
     * Checks if s is "in"
     * @param s string to check
     * @return True if is "in", False otherwise
     */
    public boolean isWordIn(String s) {
        return is(Constants.IN, s);
    }

    /**
     * Checks if s is a word associated with the future
     * @param s string to check
     * @return True if is a future word, False otherwise
     */
    public boolean isFutureWord(String s) {
        return contain(Constants.KEYWORDS_FUTURE, s);
    }

    /**
     * Checks if s is a word associated with the past
     * @param s string to check
     * @return True if is a past word, False otherwise
     */
    public boolean isPastWord(String s) {
        return contain(Constants.KEYWORDS_PAST, s);
    }

    /**
     * Checks if s is a word associated with duration
     * @param s string to check
     * @return True if is a duration word, False otherwise
     */
    public boolean isDuration(String s) {
        return contain(Constants.DURATION, s);
    }

    /**
     * Checks if s is "at"
     * @param s string to check
     * @return True if is "at", False otherwise
     */
    public boolean isWordAt(String s) {
        return is(Constants.AT, s);
    }

    /**
     * Checks if s is "by"
     * @param s string to check
     * @return True if is "by", False otherwise
     */
    public boolean isWordBy(String s) {
        return is(Constants.BY, s);
    }

    /**
     * Checks if s is a word associated with the future
     * @param s string to check
     * @return True if is an ampm word, False otherwise
     */
    public boolean isAmPm(String s) {
        return contain(Constants.KEYWORDS_AMPM, s);
    }

    /**
     * Checks if s is a weekday
     * @param s string to check
     * @return True if is a weekday word, False otherwise
     */
    public boolean isWeekday(String s) {
        return contain(Constants.KEYWORD_WEEKDAY, s);
    }

    /**
     * Checks if s is a weekend
     * @param s string to check
     * @return True if is a weekend word, False otherwise
     */
    public boolean isWeekend(String s) {
        return contain(Constants.KEYWORDS_WEEKEND, s);
    }

    /**
     * Checks if s is a weekday
     * @param s string to check
     * @return True if is an epoch word, False otherwise
     */
    public boolean isEpoch(String s) {
        return contain(Constants.KEYWORDS_EPOCH, s);
    }

    /**
     * Checks if timeKeywords contains an epoch keyword
     * @return True if contains an epoch word, False otherwise
     */
    public boolean containsEpoch() {
        return !timeKeywords.get(Constants.EPOCH).isEmpty();
    }

    /**
     * Checks if timeKeywords contains a weekday keyword
     * @return True if contains a weekday word, False otherwise
     */
    public boolean containsWeekday() {
        return !timeKeywords.get(Constants.WEEKDAY).isEmpty();
    }

    /**
     * Checks if timeKeywords contains a weekend keyword
     * @return True if contains a weekend word, False otherwise
     */
    public boolean containsWeekend() {
        return !timeKeywords.get(Constants.WEEKEND).isEmpty();
    }

    /**
     * Checks if timeKeywords contains temporal digits
     * @return True if contains temporal digits, False otherwise
     */
    public boolean containsTemporalDigits() {

        return !timeKeywords.get(Constants.DIGIT).isEmpty();
    }

    /**
     * Checks if timeKeywords contains past keywords
     * @return True if contains a past word, False otherwise
     */
    public boolean containsPast() {
        return !timeKeywords.get(Constants.PAST).isEmpty();
    }

    /**
     * Checks if timeKeywords contains future keywords
     * @return True if contains a future word, False otherwise
     */
    public boolean containsFuture() {
        return !timeKeywords.get(Constants.FUTURE).isEmpty();
    }

    /**
     * Checks if timeKeywords contains ampm keywords
     * @return True if contains an ampm word, False otherwise
     */
    public boolean containsAmPm() {
        return !timeKeywords.get(Constants.AMPM).isEmpty();
    }

    /**
     * Checks if timeKeywords contains colon
     * @return True if contains a colon, False otherwise
     */
    public boolean containsColon() {
        return colonCount == 1 && !colonPosition[0].equals("")
                && !colonPosition[1].equals("");
    }

    /**
     * Checks if timeKeywords contains temporal keywords
     * @return True if contains a temporal keyword, False otherwise
     */
    public boolean containsTime() {
        return containsEpoch() || containsPast() || containsFuture()
                || containsAmPm() || containsWeekday() || containsWeekend()
                || containsTemporalDigits();
    }

    /**
     * Checks if timeKeywords contains am keywords
     * @return True if is an am word, False otherwise
     */
    public boolean isAM() {
        if (timeKeywords.containsKey(Constants.AMPM)){
            String str = timeKeywords.get(Constants.AMPM).get(0);
            return str.equals("a.m.") || str.equals("am");
        }
        return false;
    }
    /**
     * Checks if timeKeywords contains pm keywords
     * @return True if is a pm word, False otherwise
     */
    public boolean isPM() {
        if (timeKeywords.containsKey(Constants.AMPM)){
            String str = timeKeywords.get(Constants.AMPM).get(0);
            return str.equals("p.m.") || str.equals("pm");
        }
        return false;
    }

}
