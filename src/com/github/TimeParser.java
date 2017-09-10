package com.github;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import static com.github.StringUtils.*;

/**
 * Created by mars on 8/31/17.
 */
public class TimeParser {
    // FROM 00:00 TO 23:59
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int weekday;

    private int day2;
    private int hour2;
    private int minute2;
    private int second2;
    private int weekday2;

    private static boolean hasCalDigit = false;

    public TimeComponent timeComponent;

    TimeParser() {
        day = 0;
        hour = 0;
        minute = 0;
        second = 0;
        weekday = 0;

        day2 = 0;
        hour2 = 0;
        minute2 = 0;
        second2 = 0;
        weekday2 = 0;

        hasCalDigit = false;
    }




    private void getDateAndTime() {

        getCurrentTime();
        // TYPE_SPAN
        // TOMORROW, NEXT
        if (timeComponent.containsFuture()) {
            doFuture();
        }

        // TYPE_SPAN
        // LAST, YESTERDAY
        if (timeComponent.containsPast()) {
            doPast();
        }

        // TYPE_SPAN
        // MONDAY, TUESDAY, .. FRIDAY
        if (timeComponent.containsWeekday()) {
            doWeekdays();
        }

        if (timeComponent.containsWeekend()) {
            doWeekend();
        }

        // TYPE_SPAN
        // MORNING, LUNCHTIME,
        if (timeComponent.containsEpoch()) {
            doEpoch();
        }

        // TYPE_TIME
        if (!hasCalDigit && timeComponent.containsTemporalDigits()) {
            // TODO: // FIXME: 4/30/17
//            if (colonCount == 2){
//                doTwoColons();
//            }
//            else
            if (timeComponent.containsColon()) {
                doOneColon();
            } else {
                doNoColons();
            }
        }
    }

    private void doEpoch() {

        String epoch = (String) timeComponent.timeKeywords.get("epoch").get(0);
        if (epoch.equals("evening")
                || epoch.equals("night")
                || epoch.equals("tonight")
                || epoch.equals("dinner")) {
            if (!timeComponent.containsTemporalDigits()) {
                hour = 18;
                minute = 0;
                second = 0;

                hour2 = 23;
                minute2 = 59;
                second = 0;
            } else {
                // let's meet at 10 tonight

                int time = Integer.parseInt((String)timeComponent.timeKeywords.get(Constants.DIGIT).get(0));
                if (time < 12) {
                    hour = time;
                    hour += 12;
                }
                minute = 0;
                second = 0;
                hasCalDigit = true;
            }

        } else if (epoch.equals("morning")) {
            if (!timeComponent.containsTemporalDigits()) {
                hour = 6;
                minute = 0;
                second = 0;

                hour2 = 11;
                minute2 = 59;
                second2 = 0;
            } else {
                hour = 10;
                minute = 0;
                second = 0;
            }

        } else if (epoch.equals("noon")
                || epoch.contains("lunch")) {
            hour = 12;
            minute = 0;
            second = 0;

        } else if (epoch.equals("afternoon")) {
            hour = 12;
            minute = 0;
            second = 0;

            hour2 = 17;
            minute2 = 59;
            second2 = 0;
        }

    }

    // Have Digits, but no colons:
    // "3pm", "at 6", "in 20 minutes"
    private void doNoColons() {

        if (!timeComponent.timeKeywords.get(Constants.AMPM).isEmpty()) {
            // "3pm"
            doAmPmNoColons();
        } else {
            // "at 6"
            if (!timeComponent.timeKeywords.get(Constants.AT_NEXT).isEmpty()) {
                doAtNext();
            } else {
                if (!timeComponent.timeKeywords.get(Constants.BY_NEXT).isEmpty()) {
                    doByNext();
                }

                // "in 20 minutes"
                else if (!timeComponent.timeKeywords.get(Constants.IN).isEmpty()) {
                    doInNext();
                } else {
                    if (!timeComponent.timeKeywords.get(Constants.FUTURE).isEmpty() || !timeComponent.timeKeywords.get(Constants.PAST).isEmpty() || !timeComponent.timeKeywords.get(Constants.WEEKDAY).isEmpty()) {
                        int time = Integer.parseInt((String) timeComponent.timeKeywords.get(Constants.DIGIT).get(0));
                        if (time < 24) {
                            hour = time;
                            minute = 0;
                            second = 0;
                            if (time > 10 && time < 12) {
                                hour += 12;
                            }
                        } else if (time % 100 < 60) {

                            hour = time / 100;
                            minute = time % 100;
                            second = 0;
                        }
                        if (timeComponent.timeKeywords.get(Constants.DIGIT).size() > 1) {
                            int time2 = Integer.parseInt((String) timeComponent.timeKeywords.get(Constants.DIGIT).get(1));
                            if (time2 < 24) {
                                hour2 = time2;
                                minute2 = 0;
                                second2 = 0;

                            } else if (time2 % 100 < 60) {
                                hour2 = time2 / 100;
                                minute2 = time2 % 100;
                                second2 = 0;
                            }
                        }
                    } else {
                        timeComponent.timeKeywords.get(Constants.DIGIT).clear();
                    }
                }
            }
        }

    }

    private void doAtNext() {
        String hour_real = timeComponent.timeKeywords.get(Constants.AT_NEXT).get(0);
        setTimeWithDight(hour_real);

    }

    private void doByNext() {
        int hour_real = Integer.parseInt((String)timeComponent.timeKeywords.get(Constants.BY_NEXT).get(0));
        if (hour > hour_real) {
            hour = hour_real + 12;
        } else {
            hour = hour_real;
        }
        minute = 0;
        second = 0;
    }

    private void doAmPmNoColons() {
        // Three cases: 3pm, 10am, 430pm, 1030 pm
        String ampmPrev = timeComponent.timeKeywords.get(Constants.AMPM_PREV).get(0);
        setTimeWithDight(ampmPrev);
        if (timeComponent.isPM()) {
            hour += 12;
        }
        if (!timeComponent.timeKeywords.get(Constants.AMPM_PREV2).isEmpty()) {
            String ampmPrev2 = (String)timeComponent.timeKeywords.get(Constants.AMPM_PREV2).get(0);
            setTimeWithDight(ampmPrev2);
        }
    }

    private void setTimeWithDight(String ampmPrev) {
        switch (ampmPrev.length()) {
            case 1:
                if(hour > Integer.parseInt(ampmPrev)) {
                    hour = Integer.parseInt(ampmPrev) + 12;
                }
                else {
                    hour = Integer.parseInt(ampmPrev);
                }
                minute = 0;
                second = 0;
                break;

            case 2:
                if(hour > Integer.parseInt(ampmPrev)) {
                    hour = Integer.parseInt(ampmPrev) + 12;
                }
                else {
                    hour = Integer.parseInt(ampmPrev);
                }                minute = 0;
                second = 0;
                break;

            case 3:

                if (Integer.parseInt(ampmPrev) % 100 < 60) {
                    if(hour > Integer.parseInt(ampmPrev) / 100) {
                        hour = (Integer.parseInt(ampmPrev) / 100) + 12;
                    }
                    else {
                        hour = Integer.parseInt(ampmPrev) / 100;
                    }
                    minute = Integer.parseInt(ampmPrev) % 100;
                    second = 0;
                } else {
                    hour = 0;
                    minute = 0;
                    second = 0;
                }
                break;

            case 4:
                int time = Integer.parseInt(ampmPrev);
                if (time % 100 < 60) {
                    if(hour > Integer.parseInt(ampmPrev) / 100) {
                        hour = (Integer.parseInt(ampmPrev) / 100) + 12;
                    }
                    else {
                        hour = Integer.parseInt(ampmPrev) / 100;
                    }                    minute = time % 100;
                    second = 0;
                } else {
                    hour = 0;
                    minute = 0;
                    second = 0;
                }
                break;
            default:
                break;
        }
    }


    private void doOneColon() {
        hour = Integer.parseInt(timeComponent.colonPosition[0]);
        minute = Integer.parseInt(timeComponent.colonPosition[1]);
        second = 0;

        if (timeComponent.containsAmPm()) {
            if (timeComponent.isPM())
                hour = hour + 12;
        } else if (timeComponent.containsEpoch()) {
            String epoch = (String)timeComponent.timeKeywords.get(Constants.EPOCH).get(0);
            epoch = epoch.toLowerCase();
            if (epoch.equals("tonight")
                    || epoch.equals("evening")
                    || epoch.equals("night")) {
                hour = hour + 12;
            }
        } else if (timeComponent.timeKeywords.get(Constants.DIGIT).size() > 2) {
//            Log.e("7",hour2+"");
            hour2 = Integer.parseInt((String) timeComponent.timeKeywords.get(Constants.DIGIT).get(2));
            minute2 = 0;
            second2 = 0;
        }
    }

    private void doTwoColons() {
        hour = Integer.parseInt(timeComponent.colonPosition[0]);
        minute = Integer.parseInt(timeComponent.colonPosition[1]);
        second = 0;
//        Log.e("set",Integer.parseInt(colonPosition[2]));
        hour2 = Integer.parseInt(timeComponent.colonPosition[2]);
        minute2 = Integer.parseInt(timeComponent.colonPosition[3]);
        second2 = 0;
    }

    private void setDayHourMinuteSeconds() {
        hour = 0;
        minute = 0;
        second = 0;

        hour2 = 23;
        minute2 = 59;
        second2 = 59;
    }

    private void doFuture() {
        String futureFirst = (String)timeComponent.timeKeywords.get(Constants.FUTURE).get(0);
        String futureSecond = "";
        if(timeComponent.timeKeywords.get(Constants.FUTURE).size() > 1) {
            futureSecond = (String) timeComponent.timeKeywords.get(Constants.FUTURE).get(1);
        }
        if (futureFirst.equals("next") && (timeComponent.containsWeekday() || timeComponent.containsWeekend())) {
            day = day + 7;
            day2 = day2 + 7;

        } else if (futureFirst.equals("tomorrow") || futureFirst.equals("tmw") || futureFirst.equals("tmrw")) {
            day = day + 1;
            day2 = day2 + 1;
        } else if (futureFirst.equals("next") && timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).size() != 0) {
            if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("day")) {
                day = day + 1;
                day2 = day2 + 1;
            } else if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("week")) {
                day = day + 7 - weekday;
                day2 = day2 + 14 - weekday2;
            } else if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("month")) {
                day = 30;
                day2 = 61;
            } else if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("year")) {
                day = 365 - day;
                day2 = 730 - day2;
            }

        } else if (futureFirst.equals("after")) {
            if (timeComponent.timeKeywords.get(Constants.FUTURE).size() > 1 && (futureSecond.equals("tomorrow") || futureSecond.equals("tmw") || futureSecond.equals("tmrw"))) {
                day = day + 2;
                day2 = day2 + 2;
            } else {
                day = day + 1;
                day2 = day2 + 1;
            }
        } else if (futureFirst.equals("today")) {
            day = day;
            day2 = day2;
        }
        // This will be overwritten if there are digits in the end.
        setDayHourMinuteSeconds();
    }

    private void doPast() {
        String past = (String) timeComponent.timeKeywords.get(Constants.PAST).get(0);
        if (past.equals("last") && (timeComponent.containsWeekday() || timeComponent.containsWeekend())) {
            day = day - 7;
            day2 = day2 - 7;
        } else if (past.equals("yesterday")) {
            day = day - 1;
            day2 = day2 - 1;
        } else if (past.equals("last") && timeComponent.containsEpoch()) {
            day = day - 1;
            day2 = day2 - 1;
        } else if (past.equals("last") && timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).size() != 0) {
            String duration = (String) timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0);
            if (duration.equals("day")) {
                day = day - 1;
                day2 = day2 - 1;
            } else if (duration.equals("week")) {
                day = day - weekday - 7;
                day2 = day2 - weekday2;
            } else if (duration.equals("month")) {
                day = -31;
                day2 = -1;
            } else if (duration.equals("year")) {
                day = -366 - day;
                day2 = -1 - day2;
            }
        }
        //day before yesterday case
        else if (past.equals("before")) {
            if (timeComponent.timeKeywords.get(Constants.PAST).size() > 1) {
                String pastSecond = (String) timeComponent.timeKeywords.get(Constants.PAST).get(1);
                if (pastSecond.equals("yesterday")) {
                    day = day - 2;
                    day2 = day2 - 2;
                } else {
                    day = day - 1;
                    day2 = day2 - 1;
                }
            }
        }

        // This will be overwritten if there are digits in the end.
        setDayHourMinuteSeconds();
    }

    private void doWeekend() {
        if (timeComponent.timeKeywords.get(Constants.WEEKEND).size() == 1) {
            int weekday_real = 6;
            day2 = day2 + weekday_real - weekday + 1;
            if (weekday_real >= weekday) {
                day = day + weekday_real - weekday;
            } else {
                day = day + 7 - weekday + weekday_real;
                if (timeComponent.timeKeywords.get(Constants.FUTURE).contains("next")) {
                    day = day - 7;
                    day2 = day2 - 7;
                } else if (timeComponent.timeKeywords.get(Constants.PAST).contains("last")) {
                    day = day + 7;
                    day2 = day2 + 7;
                }
            }

        } else {
            weekday = -1;
        }
        setDayHourMinuteSeconds();
    }

    private void doWeekdays() {
        if (timeComponent.timeKeywords.get(Constants.WEEKDAY).size() == 1) {
            int weekday_real = weekdayToInteger(timeComponent.timeKeywords.get(Constants.WEEKDAY).get(0));

            if (weekday_real >= weekday) {
                day2 = day = day + weekday_real - weekday;

            } else {
                day2 = day = day + 7 - weekday + weekday_real;
                if (timeComponent.timeKeywords.get(Constants.FUTURE).contains("next")) {
                    day2 = day = day - 7;
                } else if (timeComponent.timeKeywords.get(Constants.PAST).contains("last")) {
                    day2 = day = day + 7;
                }
                weekday = weekday_real;
            }
        } else {
            weekday = -1;
        }


        // This will be overwritten if there are digits in the end.
        setDayHourMinuteSeconds();

    }

    private void doInNext() {
        if (!timeComponent.timeKeywords.get(Constants.IN_NEXT).isEmpty() && !timeComponent.timeKeywords.get(Constants.IN_NEXT2).isEmpty()) {
            if (timeComponent.timeKeywords.get(Constants.IN_NEXT2).get(0).contains("m")) {
                minute = minute + Integer.parseInt(String.valueOf(timeComponent.timeKeywords.get(Constants.IN_NEXT).get(0)));
            } else if (timeComponent.timeKeywords.get(Constants.IN_NEXT2).get(0).contains("h")) {
                hour = hour + Integer.parseInt(String.valueOf(timeComponent.timeKeywords.get(Constants.IN_NEXT).get(0)));
            }
        }
    }

    public long[] GetInput(String message) {

        //todo:
        //Change timezone for testing
        Calendar time = Calendar.getInstance();
        Calendar time2 = Calendar.getInstance();

        message = normalize(message);
        timeComponent = new TimeComponent();

        try {

            timeComponent.extractTemporalKeywords(message);
            System.out.println("!!!" + timeComponent.timeExpressionString);
            System.out.println("!!!" + timeComponent.timeKeywords);
            getDateAndTime();

        } catch (Exception ignored) {

        }

        time.add(Calendar.DAY_OF_MONTH, day - time.get(Calendar.DAY_OF_MONTH));
        time.add(Calendar.HOUR_OF_DAY, hour - time.get(Calendar.HOUR_OF_DAY));
        time.add(Calendar.MINUTE, minute - time.get(Calendar.MINUTE));
        time.add(Calendar.SECOND, second - time.get(Calendar.SECOND));


        time2.add(Calendar.DAY_OF_MONTH, day2 - time.get(Calendar.DAY_OF_MONTH));
        time2.set(Calendar.HOUR_OF_DAY, hour2);
        time2.set(Calendar.MINUTE, minute2);
        time2.set(Calendar.SECOND, second2);


        System.out.println(time + "  " + time2);

        if (!timeComponent.containsTime()) {
            return new long[]{-1, -1};
        } else if (timeComponent.containsTemporalDigits() && timeComponent.timeKeywords.get(Constants.DIGIT).size() == 1 && timeComponent.timeKeywords.get(Constants.WEEKDAY).isEmpty() && timeComponent.timeKeywords.get(Constants.FUTURE).isEmpty() && timeComponent.timeKeywords.get(Constants.PAST).isEmpty()) {
            return new long[]{time.getTimeInMillis(), -1};
        } else {
            //ZJ - to print 2 time like from 12:00 to 13:00
            if (timeComponent.timeKeywords.get(Constants.AMPM_PREV2).isEmpty() || (timeComponent.timeKeywords.get(Constants.DIGIT).size() > 1 && !timeComponent.containsColon()) || timeComponent.timeKeywords.get(Constants.DIGIT).size() > 2)
                ;//timeComponent.timeKeywords.get(Constants.DIGIT).clear();
            return new long[]{time.getTimeInMillis(), time2.getTimeInMillis()};
        }
    }

    private void getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        day2 = day = cal.get(Calendar.DAY_OF_MONTH);
        hour2 = hour = cal.get(Calendar.HOUR_OF_DAY);

        minute2 = minute = cal.get(Calendar.MINUTE);
        second2 = second = cal.get(Calendar.SECOND);
        weekday2 = weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (weekday == 0)
            weekday = 7;

        if (weekday2 == 0)
            weekday2 = 7;

    }

}
