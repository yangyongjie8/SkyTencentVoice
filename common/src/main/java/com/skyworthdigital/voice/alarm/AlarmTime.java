package com.skyworthdigital.voice.alarm;


public class AlarmTime {
    private String apm;
    private String day;
    private String hour;
    private String minute;
    private String month;
    private String repeat;

    public String getMonth() {
        return month;
    }

    public String getMinute() {
        return minute;
    }

    public String getHour() {
        return hour;
    }

    public String getApm() {
        return apm;
    }

    public String getDay() {
        return day;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
