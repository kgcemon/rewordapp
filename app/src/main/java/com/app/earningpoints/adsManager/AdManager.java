package com.app.earningpoints.adsManager;

import static com.app.earningpoints.util.Constant_Api.INTERSTITAL_COUNT;
import static com.app.earningpoints.util.Constant_Api.INTERSTITAL_ID;
import static com.app.earningpoints.util.Constant_Api.INTERSTITAL_TYPE;

import android.app.Activity;
import android.widget.RelativeLayout;

public class AdManager  {
     Activity activity;
     Banner.Builder bannerAd;
     Interstital.Builder interstitalAd;
     public static int Count=0;

    public AdManager(Activity activity){
        this.activity=activity;
        bannerAd=new Banner.Builder(activity);
        interstitalAd=new Interstital.Builder(activity);
    }

    public void loadBannerAd(RelativeLayout v){
        bannerAd.buildAd(v);
    }

    public void loadInterstitalAd(){
       if(Count >= INTERSTITAL_COUNT){
           interstitalAd.setAu(INTERSTITAL_ID);
           interstitalAd.setiT(INTERSTITAL_TYPE);
           interstitalAd.buildAd();
           Count=0;
       }else{
           Count++;
       }
    }


}
