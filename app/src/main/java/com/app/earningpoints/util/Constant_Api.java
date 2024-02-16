package com.app.earningpoints.util;

import static com.app.earningpoints.util.SimpleCountDownTimer.*;

import com.app.earningpoints.Responsemodel.ConfigResp;
import com.ironsource.mediationsdk.IronSourceBannerLayout;

public class Constant_Api {
    public static ConfigResp.SpinItem AppSpin = null;
    public static String APi = iurlog(Const.getAU());
    public static final String BANNER_SPIN = "spin";
    public static final String LINK = "link";
    public static String TOOLBAR_TITLE = "";
    public static final int GAME_MINUTE =0 ;
    public static boolean REMOVE = false;
    public static int Pos = 0;
    public static int ScreenType = 0;
    public static int MIN_AMOUNT = 1;
    public static int MAX_AMOUNT = 2;
    public static boolean isNpv =false;
    public static final String UPDATE = "update";
    public static final String MAINTENANCE = "maintenance";
    public static String SHARE_MSG ="" ;
    public static boolean INTERSTITIAL = false;
    public static int COUNT = 0;
    public static final String LOAD_IMAGES ="/images/";
    public static final String AUTH = "Bearer ";
    public static final String Authorization = "Authorization";
    public static final String TOKEN = "token";
    public static final String CONTENT_TYPE = "application/json";
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    public static final String CLOSE = "close";
    public static final String RESTART = "restart";
    public static final String MYTOKEN ="Authorization" ;
    public static final String OLDTOKEN = "Basic ";
    public static final String MY_DEVICE = "WkdWMmFXTmxYMmxr";
    public static IronSourceBannerLayout bannerLayout;
    public static String TITLE = "";
    public static String APP_DESCRIPTION = "";
    public static String PRIVACY_POLICY = "";
    public static String APP_AUTHOR = "";

    public static String BANNER_ID = "";
    public static String BANNER_TYPE = "";

    public static String INTERSTITAL_ID = "";
    public static String INTERSTITAL_TYPE = "";
    public static String INTERSTITAL_ADUNIT = "";
    public static int  INTERSTITAL_COUNT = 0;

    public static String NATIVE_ID = "";
    public static String NATIVE_TYPE = "";
    public static String NATIVE_ADUNIT = "";
    public static int NATIVE_COUNT = 0;

    public static String ADMOB_APP_ID = "";
    public static String ADMOB_AD_TYPE = "";
    public static String ADMOB_AD_ADUNIT = "";

    public static String FB_AD_TYPE = "";
    public static String FB_AD_ADUNIT = "";

    public static String UNITY_GAME_ID = "";
    public static String UNITY_AD_TYPE = "";
    public static String UNITY_AD_ADUNIT = "";

    public static String APPLOVIN_AD_TYPE = "";
    public static String APPLOVIN_AD_ADUNIT = "";

    public static String IRONSOURCE_APP_KEY = "";
    public static String IRONSOURCE_AD_TYPE = "";

    public static String START_IO_APPID = "";
    public static String START_IO_AD = "";

    public static String ADCOLONY_APP_ID = "";
    public static String ADCOLONY_ZONE_ID = "";
    public static String ADCOLONY_AD_TYPE = "";
    public static String AD_NOT_LOAD_CREDIT = "false";




    public static final  String AD_ADMOB = "admob";
    public static final  String AD_IRONSOURCE = "ironsource";
    public static final  String AD_FB = "fb";
    public static final  String AD_APPLOVIN= "applvoin";
    public static final  String AD_OFF= "off";
    public static final  String AD_ON= "on";
    public static final  String AD_UNITY= "unity";
    public static  final String AD_START_IO= "startapp";
    public static final String AD_ADCOLONY= "adcolony";
    public static final String AD_INTERSTITAL= "inter";
    public static final String AD_REWARDED= "reward";
}
