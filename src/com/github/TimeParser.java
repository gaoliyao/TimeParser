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
    private int startTimeDay;
    private int startTimeHour;
    private int startTimeMinute;
    private int startTimeSecond;
    private int weekday;

    private int endTimeDay;
    private int endTimeHour;
    private int endTimeMinute;
    private int endTimeSecond;
    private int weekendTimeDay;

    private static boolean hasCalDigit = false;

    public TimeComponent timeComponent;

    TimeParser() {
        startTimeDay = 0;
        startTimeHour = 0;
        startTimeMinute = 0;
        startTimeSecond = 0;
        weekday = 0;

        endTimeDay = 0;
        endTimeHour = 0;
        endTimeMinute = 0;
        endTimeSecond = 0;
        weekendTimeDay = 0;

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
                startTimeHour = 18;
                startTimeMinute = 0;
                startTimeSecond = 0;

                endTimeHour = 23;
                endTimeMinute = 59;
                startTimeSecond = 0;
            } else {
                // let's meet at 10 tonight

                int time = Integer.parseInt((String)timeComponent.timeKeywords.get(Constants.DIGIT).get(0));
                if (time < 12) {
                    startTimeHour = time;
                    startTimeHour += 12;
                }
                startTimeMinute = 0;
                startTimeSecond = 0;
                hasCalDigit = true;
            }

        } else if (epoch.equals("morning")) {
            if (!timeComponent.containsTemporalDigits()) {
                startTimeHour = 6;
                startTimeMinute = 0;
                startTimeSecond = 0;

                endTimeHour = 11;
                endTimeMinute = 59;
                endTimeSecond = 0;
            } else {
                startTimeHour = 10;
                startTimeMinute = 0;
                startTimeSecond = 0;
            }

        } else if (epoch.equals("noon")
                || epoch.contains("lunch")) {
            startTimeHour = 12;
            startTimeMinute = 0;
            startTimeSecond = 0;

        } else if (epoch.equals("afternoon")) {
            startTimeHour = 12;
            startTimeMinute = 0;
            startTimeSecond = 0;

            endTimeHour = 17;
            endTimeMinute = 59;
            endTimeSecond = 0;
        }

    }

    // Have Digits, but no colons:
    // "3pm", "at 6", "in 20 startTimeMinutes"
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

                // "in 20 startTimeMinutes"
                else if (!timeComponent.timeKeywords.get(Constants.IN).isEmpty()) {
                    doInNext();
                } else {
                    if (!timeComponent.timeKeywords.get(Constants.FUTURE).isEmpty() || !timeComponent.timeKeywords.get(Constants.PAST).isEmpty() || !timeComponent.timeKeywords.get(Constants.WEEKDAY).isEmpty()) {
                        int time = Integer.parseInt((String) timeComponent.timeKeywords.get(Constants.DIGIT).get(0));
                        if (time < 24) {
                            startTimeHour = time;
                            startTimeMinute = 0;
                            startTimeSecond = 0;
                            if (time > 10 && time < 12) {
                                startTimeHour += 12;
                            }
                        } else if (time % 100 < 60) {

                            startTimeHour = time / 100;
                            startTimeMinute = time % 100;
                            startTimeSecond = 0;
                        }
                        if (timeComponent.timeKeywords.get(Constants.DIGIT).size() > 1) {
                            int time2 = Integer.parseInt((String) timeComponent.timeKeywords.get(Constants.DIGIT).get(1));
                            if (time2 < 24) {
                                endTimeHour = time2;
                                endTimeMinute = 0;
                                endTimeSecond = 0;

                            } else if (time2 % 100 < 60) {
                                endTimeHour = time2 / 100;
                                endTimeMinute = time2 % 100;
                                endTimeSecond = 0;
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
        int startTimeHour_real = Integer.parseInt((String)timeComponent.timeKeywords.get(Constants.BY_NEXT).get(0));
        if (startTimeHour > startTimeHour_real) {
            startTimeHour = startTimeHour_real + 12;
        } else {
            startTimeHour = startTimeHour_real;
        }
        startTimeMinute = 0;
        startTimeSecond = 0;
    }

    private void doAmPmNoColons() {
        // Three cases: 3pm, 10am, 430pm, 1030 pm
        String ampmPrev = timeComponent.timeKeywords.get(Constants.AMPM_PREV).get(0);
        setTimeWithDight(ampmPrev);
        if (timeComponent.isPM()) {
            startTimeHour += 12;
        }
        if (!timeComponent.timeKeywords.get(Constants.AMPM_PREV2).isEmpty()) {
            String ampmPrev2 = (String)timeComponent.timeKeywords.get(Constants.AMPM_PREV2).get(0);
            setTimeWithDight(ampmPrev2);
        }
    }

    private void setTimeWithDight(String ampmPrev) {
        switch (ampmPrev.length()) {
            case 1:
                if(startTimeHour > Integer.parseInt(ampmPrev)) {
                    startTimeHour = Integer.parseInt(ampmPrev) + 12;
                }
                else {
                    startTimeHour = Integer.parseInt(ampmPrev);
                }
                startTimeMinute = 0;
                startTimeSecond = 0;
                break;

            case 2:
                if(startTimeHour > Integer.parseInt(ampmPrev)) {
                    startTimeHour = Integer.parseInt(ampmPrev) + 12;
                }
                else {
                    startTimeHour = Integer.parseInt(ampmPrev);
                }                startTimeMinute = 0;
                startTimeSecond = 0;
                break;

            case 3:

                if (Integer.parseInt(ampmPrev) % 100 < 60) {
                    if(startTimeHour > Integer.parseInt(ampmPrev) / 100) {
                        startTimeHour = (Integer.parseInt(ampmPrev) / 100) + 12;
                    }
                    else {
                        startTimeHour = Integer.parseInt(ampmPrev) / 100;
                    }
                    startTimeMinute = Integer.parseInt(ampmPrev) % 100;
                    startTimeSecond = 0;
                } else {
                    startTimeHour = 0;
                    startTimeMinute = 0;
                    startTimeSecond = 0;
                }
                break;

            case 4:
                int time = Integer.parseInt(ampmPrev);
                if (time % 100 < 60) {
                    if(startTimeHour > Integer.parseInt(ampmPrev) / 100) {
                        startTimeHour = (Integer.parseInt(ampmPrev) / 100) + 12;
                    }
                    else {
                        startTimeHour = Integer.parseInt(ampmPrev) / 100;
                    }                    startTimeMinute = time % 100;
                    startTimeSecond = 0;
                } else {
                    startTimeHour = 0;
                    startTimeMinute = 0;
                    startTimeSecond = 0;
                }
                break;
            default:
                break;
        }
    }


    private void doOneColon() {
        startTimeHour = Integer.parseInt(timeComponent.colonPosition[0]);
        startTimeMinute = Integer.parseInt(timeComponent.colonPosition[1]);
        startTimeSecond = 0;

        if (timeComponent.containsAmPm()) {
            if (timeComponent.isPM())
                startTimeHour = startTimeHour + 12;
        } else if (timeComponent.containsEpoch()) {
            String epoch = (String)timeComponent.timeKeywords.get(Constants.EPOCH).get(0);
            epoch = epoch.toLowerCase();
            if (epoch.equals("tonight")
                    || epoch.equals("evening")
                    || epoch.equals("night")) {
                startTimeHour = startTimeHour + 12;
            }
        } else if (timeComponent.timeKeywords.get(Constants.DIGIT).size() > 2) {
//            Log.e("7",endTimeHour+"");
            endTimeHour = Integer.parseInt((String) timeComponent.timeKeywords.get(Constants.DIGIT).get(2));
            endTimeMinute = 0;
            endTimeSecond = 0;
        }
    }

    private void doTwoColons() {
        startTimeHour = Integer.parseInt(timeComponent.colonPosition[0]);
        startTimeMinute = Integer.parseInt(timeComponent.colonPosition[1]);
        startTimeSecond = 0;
//        Log.e("set",Integer.parseInt(colonPosition[2]));
        endTimeHour = Integer.parseInt(timeComponent.colonPosition[2]);
        endTimeMinute = Integer.parseInt(timeComponent.colonPosition[3]);
        endTimeSecond = 0;
    }

    private void setDayHourMinuteSeconds() {
        startTimeHour = 0;
        startTimeMinute = 0;
        startTimeSecond = 0;

        endTimeHour = 23;
        endTimeMinute = 59;
        endTimeSecond = 59;
    }

    private void doFuture() {
        String futureFirst = (String)timeComponent.timeKeywords.get(Constants.FUTURE).get(0);
        String futureSecond = "";
        if(timeComponent.timeKeywords.get(Constants.FUTURE).size() > 1) {
            futureSecond = (String) timeComponent.timeKeywords.get(Constants.FUTURE).get(1);
        }
        if (futureFirst.equals("next") && (timeComponent.containsWeekday() || timeComponent.containsWeekend())) {
            startTimeDay = startTimeDay + 7;
            endTimeDay = endTimeDay + 7;

        } else if (futureFirst.equals("tomorrow") || futureFirst.equals("tmw") || futureFirst.equals("tmrw")) {
            startTimeDay = startTimeDay + 1;
            endTimeDay = endTimeDay + 1;
        } else if (futureFirst.equals("next") && timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).size() != 0) {
            if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("day")) {
                startTimeDay = startTimeDay + 1;
                endTimeDay = endTimeDay + 1;
            } else if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("week")) {
                startTimeDay = startTimeDay + 7 - weekday;
                endTimeDay = endTimeDay + 14 - weekendTimeDay;
            } else if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("month")) {
                startTimeDay = 30;
                endTimeDay = 61;
            } else if (timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0).equals("year")) {
                startTimeDay = 365 - startTimeDay;
                endTimeDay = 730 - endTimeDay;
            }

        } else if (futureFirst.equals("after")) {
            if (timeComponent.timeKeywords.get(Constants.FUTURE).size() > 1 && (futureSecond.equals("tomorrow") || futureSecond.equals("tmw") || futureSecond.equals("tmrw"))) {
                startTimeDay = startTimeDay + 2;
                endTimeDay = endTimeDay + 2;
            } else {
                startTimeDay = startTimeDay + 1;
                endTimeDay = endTimeDay + 1;
            }
        } else if (futureFirst.equals("today")) {
            startTimeDay = startTimeDay;
            endTimeDay = endTimeDay;
        }
        // This will be overwritten if there are digits in the end.
        setDayHourMinuteSeconds();
    }

    private void doPast() {
        String past = (String) timeComponent.timeKeywords.get(Constants.PAST).get(0);
        if (past.equals("last") && (timeComponent.containsWeekday() || timeComponent.containsWeekend())) {
            startTimeDay = startTimeDay - 7;
            endTimeDay = endTimeDay - 7;
        } else if (past.equals("yesterday")) {
            startTimeDay = startTimeDay - 1;
            endTimeDay = endTimeDay - 1;
        } else if (past.equals("last") && timeComponent.containsEpoch()) {
            startTimeDay = startTimeDay - 1;
            endTimeDay = endTimeDay - 1;
        } else if (past.equals("last") && timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).size() != 0) {
            String duration = (String) timeComponent.timeKeywords.get(Constants.DURATION_KEYWORD).get(0);
            if (duration.equals("day")) {
                startTimeDay = startTimeDay - 1;
                endTimeDay = endTimeDay - 1;
            } else if (duration.equals("week")) {
                startTimeDay = startTimeDay - weekday - 7;
                endTimeDay = endTimeDay - weekendTimeDay;
            } else if (duration.equals("month")) {
                startTimeDay = -31;
                endTimeDay = -1;
            } else if (duration.equals("year")) {
                startTimeDay = -366 - startTimeDay;
                endTimeDay = -1 - endTimeDay;
            }
        }
        //day before yesterday case
        else if (past.equals("before")) {
            if (timeComponent.timeKeywords.get(Constants.PAST).size() > 1) {
                String pastSecond = (String) timeComponent.timeKeywords.get(Constants.PAST).get(1);
                if (pastSecond.equals("yesterday")) {
                    startTimeDay = startTimeDay - 2;
                    endTimeDay = endTimeDay - 2;
                } else {
                    startTimeDay = startTimeDay - 1;
                    endTimeDay = endTimeDay - 1;
                }
            }
        }

        // This will be overwritten if there are digits in the end.
        setDayHourMinuteSeconds();
    }

    private void doWeekend() {
        if (timeComponent.timeKeywords.get(Constants.WEEKEND).size() == 1) {
            int weekday_real = 6;
            endTimeDay = endTimeDay + weekday_real - weekday + 1;
            if (weekday_real >= weekday) {
                startTimeDay = startTimeDay + weekday_real - weekday;
            } else {
                startTimeDay = startTimeDay + 7 - weekday + weekday_real;
                if (timeComponent.timeKeywords.get(Constants.FUTURE).contains("next")) {
                    startTimeDay = startTimeDay - 7;
                    endTimeDay = endTimeDay - 7;
                } else if (timeComponent.timeKeywords.get(Constants.PAST).contains("last")) {
                    startTimeDay = startTimeDay + 7;
                    endTimeDay = endTimeDay + 7;
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
                endTimeDay = startTimeDay = startTimeDay + weekday_real - weekday;

            } else {
                endTimeDay = startTimeDay = startTimeDay + 7 - weekday + weekday_real;
                if (timeComponent.timeKeywords.get(Constants.FUTURE).contains("next")) {
                    endTimeDay = startTimeDay = startTimeDay - 7;
                } else if (timeComponent.timeKeywords.get(Constants.PAST).contains("last")) {
                    endTimeDay = startTimeDay = startTimeDay + 7;
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
                startTimeMinute = startTimeMinute + Integer.parseInt(String.valueOf(timeComponent.timeKeywords.get(Constants.IN_NEXT).get(0)));
            } else if (timeComponent.timeKeywords.get(Constants.IN_NEXT2).get(0).contains("h")) {
                startTimeHour = startTimeHour + Integer.parseInt(String.valueOf(timeComponent.timeKeywords.get(Constants.IN_NEXT).get(0)));
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

        time.add(Calendar.DAY_OF_MONTH, startTimeDay - time.get(Calendar.DAY_OF_MONTH));
        time.add(Calendar.HOUR_OF_DAY, startTimeHour - time.get(Calendar.HOUR_OF_DAY));
        time.add(Calendar.MINUTE, startTimeMinute - time.get(Calendar.MINUTE));
        time.add(Calendar.SECOND, startTimeSecond - time.get(Calendar.SECOND));


        time2.add(Calendar.DAY_OF_MONTH, endTimeDay - time.get(Calendar.DAY_OF_MONTH));
        time2.set(Calendar.HOUR_OF_DAY, endTimeHour);
        time2.set(Calendar.MINUTE, endTimeMinute);
        time2.set(Calendar.SECOND, endTimeSecond);


        System.out.println(time + "  " + time2);

        if (!timeComponent.containsTime()) {
            return new long[]{-1, -1};
        } else if (timeComponent.containsTemporalDigits() && timeComponent.timeKeywords.get(Constants.DIGIT).size() == 1 && timeComponent.timeKeywords.get(Constants.WEEKDAY).isEmpty() && timeComponent.timeKeywords.get(Constants.FUTURE).isEmpty() && timeComponent.timeKeywords.get(Constants.PAST).isEmpty()) {
            return new long[]{time.getTimeInMillis(), -1};
        } else {
            //ZJ - to print 2 time like from 12:00 to 13:00
            if (timeComponent.timeKeywords.get(Constants.AMPM_PREV2).isEmpty() || (timeComponent.timeKeywords.get(Constants.DIGIT).size() > 1 && !timeComponent.containsColon()) || timeComponent.timeKeywords.get(Constants.DIGIT).size() > 2)
                timeComponent.timeKeywords.get(Constants.DIGIT).clear();
            return new long[]{time.getTimeInMillis(), time2.getTimeInMillis()};
        }
    }

    private void getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        endTimeDay = startTimeDay = cal.get(Calendar.DAY_OF_MONTH);
        endTimeHour = startTimeHour = cal.get(Calendar.HOUR_OF_DAY);

        endTimeMinute = startTimeMinute = cal.get(Calendar.MINUTE);
        endTimeSecond = startTimeSecond = cal.get(Calendar.SECOND);
        weekendTimeDay = weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (weekday == 0)
            weekday = 7;

        if (weekendTimeDay == 0)
            weekendTimeDay = 7;

    }

}
