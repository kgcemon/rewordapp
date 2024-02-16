package com.app.earningpoints.util;

public class Const {
    static {
        System.loadLibrary("api_config");
    }

    public static native String getAU();
    public static native String getAK();

    public static int homeStyle=0;
    public static int offerwallStyle=0;
    public static int offerwallLayout=0;
    public static int surveyStyle=0;
    public static int surveyLayout=0;
}
