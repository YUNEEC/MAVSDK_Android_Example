package io.mavsdk.androidclient.utils;


import java.util.Locale;

public class CameraUtil {

    public static String getCameraShutterTime(int shutter_num, int shutter_dem) {
        int value = shutter_dem / shutter_num;
        if (value >= 8000) {
            value = 8000;
        } else if (value >= 4000) {
            value = 4000;
        } else if (value >= 2000) {
            value = 2000;
        } else if (value >= 1000) {
            value = 1000;
        } else if (value >= 500) {
            value = 500;
        } else if (value >= 250) {
            value = 250;
        } else if (value >= 125) {
            value = 125;
        } else if (value >= 60) {
            value = 60;
        } else if (value >= 30) {
            value = 30;
        } else {
            value = 10;
        }
        return String.format(Locale.ENGLISH, "%d/%d", 1, value);
    }

    public static String getCameraISO(int iso) {
        int value;
        if (iso >= 6400) {
            value = 6400;
        } else if (iso >= 3200) {
            value = 3200;
        } else if (iso >= 1600) {
            value  = 1600;
        } else if (iso >=800) {
            value = 800;
        } else if (iso >=600) {
            value = 600;
        } else if (iso >= 400) {
            value = 400;
        } else if (iso >= 300) {
            value = 300;
        } else if (iso >= 200) {
            value = 200;
        } else if (iso >= 150) {
            value = 150;
        } else if (iso >= 100) {
            value = 100;
        } else {
            value = 0;
        }
        return String.valueOf(value);
    }


   /* public static String getCameraExposureValue(int ev_num, int ev_dem) {

    }*/


    public static String getFormatRecordTime(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String hours_fmt, minu_fmt, sec_fmt;
        if (hours <= 9) {
            hours_fmt = String.format(Locale.ENGLISH, "%d%d", 0, hours);
        } else {
            hours_fmt = String.valueOf(hours);
        }
        if (minutes <= 9) {
            minu_fmt = String.format(Locale.ENGLISH, "%d%d", 0, minutes);
        } else {
            minu_fmt = String.valueOf(minutes);
        }
        if (seconds <= 9) {
            sec_fmt = String.format(Locale.ENGLISH, "%d%d", 0, seconds);
        } else {
            sec_fmt = String.valueOf(seconds);
        }
        return ((hours <= 0) ? "" : hours_fmt + ":") + minu_fmt + ":" + sec_fmt;
    }


}
