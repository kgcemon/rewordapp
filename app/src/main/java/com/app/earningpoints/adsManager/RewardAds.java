package com.app.earningpoints.adsManager;

import static com.app.earningpoints.util.Constant_Api.*;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.facebook.ads.RewardedVideoAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
import com.startapp.sdk.adsbase.StartAppAd;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class RewardAds {
    public static class Builder {
        private static final String TAG = "AdNetwork__Rewarded : ";
        boolean statartapp_reward, unityadLoaded;
        private int retryAttempt = 1;
        public boolean isCompleted;
        private AdColonyInterstitial adcolonyInt;
        Activity a;
        RewardedAd ar;
        RewardedVideoAd fr;
        com.google.android.gms.ads.interstitial.InterstitialAd ai;

        MaxRewardedAd mr;
        MaxInterstitialAd mi;
        StartAppAd sr;
        boolean isAdLoaded;

        public Builder(Activity act) {
            this.a = act;
        }

        public boolean isAdLoaded() {
            return isAdLoaded;
        }

        public void setAdLoaded(boolean adLoaded) {
            isAdLoaded = adLoaded;
        }

        public Activity getA() {
            return a;
        }

        public Builder buildAd() {
            loadReward();
            return this;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }

        public void setA(Activity a) {
            this.a = a;
        }

        public boolean isUnityadLoaded() {
            return unityadLoaded;
        }

        public void setUnityadLoaded(boolean unityadLoaded) {
            this.unityadLoaded = unityadLoaded;
        }

        private void loadReward() {
            if (ADMOB_AD_TYPE.equals(AD_REWARDED) && ADMOB_AD_ADUNIT != null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(a, ADMOB_AD_ADUNIT,
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d("ADMOB_REWARD__", loadAdError.toString());
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                setAdLoaded(true);
                                ar = rewardedAd;
                                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        Log.e("TAG", "Ad failed to show fullscreen content.");
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                        Log.d("TAG", "Ad recorded an impression.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when ad is shown.
                                        Log.d("TAG", "Ad showed fullscreen content.");
                                    }
                                });
                                Log.d("TAG", "Ad was loaded.");
                            }
                        });
            } else if (ADMOB_AD_TYPE.equals(AD_INTERSTITAL) && ADMOB_AD_ADUNIT != null) {
                com.google.android.gms.ads.interstitial.InterstitialAd.load(a, ADMOB_AD_ADUNIT, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        ai = interstitialAd;
                        ai.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                setCompleted(true);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                Log.d(TAG, "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                ai = null;
                                Log.d(TAG, "The ad was shown.");
                            }
                        });
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i(TAG, " AdMob : " + loadAdError.getMessage());
                        ai = null;
                    }
                });
            }

            if (!isAdLoaded && !UNITY_AD_TYPE.equals(AD_OFF) && UNITY_AD_ADUNIT != null) {
                UnityAds.load(UNITY_AD_ADUNIT, new IUnityAdsLoadListener() {
                    @Override
                    public void onUnityAdsAdLoaded(String placementId) {
                        setAdLoaded(true);
                        setUnityadLoaded(true);
                        Log.d(TAG, "unity interstitial ad loaded");
                    }

                    @Override
                    public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                        Log.e(TAG, "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
                    }
                });
            }

            if (!isAdLoaded && APPLOVIN_AD_TYPE.equals(AD_REWARDED) && APPLOVIN_AD_ADUNIT != null) {
                mr = MaxRewardedAd.getInstance(APPLOVIN_AD_ADUNIT, a);
                mr.setListener(new MaxRewardedAdListener() {
                    @Override
                    public void onRewardedVideoStarted(MaxAd ad) {

                    }

                    @Override
                    public void onRewardedVideoCompleted(MaxAd ad) {

                    }

                    @Override
                    public void onUserRewarded(MaxAd ad, MaxReward reward) {
                    }

                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        setAdLoaded(true);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        setCompleted(true);
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        Log.e(TAG, "onAdLoadFailed: " + error);
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                    }
                });
                mr.loadAd();
            } else if (!isAdLoaded && APPLOVIN_AD_TYPE.equals(AD_INTERSTITAL) && APPLOVIN_AD_ADUNIT != null) {
                mi = new MaxInterstitialAd(APPLOVIN_AD_ADUNIT, a);
                mi.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        setAdLoaded(true);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        setCompleted(true);
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {

                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        mi.loadAd();
                    }
                });
                mi.loadAd();
            }

            if (!isAdLoaded && IRONSOURCE_AD_TYPE.equals(AD_REWARDED)) {
                IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                    @Override
                    public void onAdAvailable(AdInfo adInfo) {
                        setAdLoaded(true);
                    }

                    @Override
                    public void onAdUnavailable() {
                    }

                    @Override
                    public void onAdOpened(AdInfo adInfo) {
                    }

                    @Override
                    public void onAdClosed(AdInfo adInfo) {
                        setCompleted(true);
                    }

                    @Override
                    public void onAdRewarded(Placement placement, AdInfo adInfo) {
                    }

                    @Override
                    public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                    }

                    @Override
                    public void onAdClicked(Placement placement, AdInfo adInfo) {
                    }
                });
                IronSource.loadRewardedVideo();
            } else if (!isAdLoaded && IRONSOURCE_AD_TYPE.equals(AD_INTERSTITAL)) {
                IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                    @Override
                    public void onAdReady(AdInfo adInfo) {
                        setAdLoaded(true);
                    }

                    @Override
                    public void onAdLoadFailed(IronSourceError ironSourceError) {

                    }

                    @Override
                    public void onAdOpened(AdInfo adInfo) {

                    }

                    @Override
                    public void onAdShowSucceeded(AdInfo adInfo) {

                    }

                    @Override
                    public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                    }

                    @Override
                    public void onAdClicked(AdInfo adInfo) {
                        setCompleted(true);
                    }

                    @Override
                    public void onAdClosed(AdInfo adInfo) {
                    }
                });
                IronSource.loadInterstitial();
            }

            if (!isAdLoaded && ADCOLONY_AD_TYPE.equals(AD_INTERSTITAL) && ADCOLONY_APP_ID != null && ADCOLONY_ZONE_ID != null) {
                AdColonyAppOptions appOptions = new AdColonyAppOptions().setGDPRConsentString("1")
                        .setKeepScreenOn(true).setGDPRRequired(true);
                AdColony.configure(a, appOptions, ADCOLONY_APP_ID, ADCOLONY_ZONE_ID);
                AdColonyAdOptions options = new AdColonyAdOptions().enableConfirmationDialog(false).enableResultsDialog(false);
                AdColonyInterstitialListener adColonyInterstitialListener = new AdColonyInterstitialListener() {
                    @Override
                    public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                        adcolonyInt = adColonyInterstitial;
                    }

                    @Override
                    public void onRequestNotFilled(AdColonyZone zone) {
                        adcolonyInt = null;
                        Log.e(TAG, "onRequestNotFilled: " + zone);
                    }

                    @Override
                    public void onClosed(AdColonyInterstitial ad) {
                        setCompleted(true);
                    }
                };
                AdColony.requestInterstitial(ADCOLONY_ZONE_ID, adColonyInterstitialListener, options);
            }

        }

        public void showReward() {
            if (ADMOB_AD_TYPE.equals(AD_INTERSTITAL) && ADMOB_AD_ADUNIT != null && ai != null) {
                ai.show(a);
            } else if (ADMOB_AD_TYPE.equals(AD_REWARDED) && ADMOB_AD_ADUNIT != null && ar != null) {
                ar.show(a, rewardItem -> {
                    setCompleted(true);
                });
            } else if (!UNITY_AD_TYPE.equals(AD_OFF) && UNITY_AD_ADUNIT != null && isUnityadLoaded()) {
                UnityAds.show(a, UNITY_AD_ADUNIT, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                        new Handler().postDelayed(() -> {
                            setCompleted(true);
                        }, 7000);
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {

                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {

                    }
                });
            } else if (APPLOVIN_AD_TYPE.equals(AD_REWARDED) && APPLOVIN_AD_ADUNIT != null && mr.isReady()) {
                mr.showAd(APPLOVIN_AD_ADUNIT);
            } else if (APPLOVIN_AD_TYPE.equals(AD_INTERSTITAL) && APPLOVIN_AD_ADUNIT != null && mi.isReady()) {
                mi.showAd(APPLOVIN_AD_ADUNIT);
            } else if (IRONSOURCE_AD_TYPE.equals(AD_REWARDED) && IronSource.isRewardedVideoAvailable()) {
                IronSource.showRewardedVideo();
            } else if (IRONSOURCE_AD_TYPE.equals(AD_INTERSTITAL) && IronSource.isInterstitialReady()) {
                IronSource.showInterstitial();
            } else if (ADCOLONY_AD_TYPE.equals(AD_INTERSTITAL) && ADCOLONY_APP_ID != null && ADCOLONY_ZONE_ID != null && adcolonyInt != null) {
                adcolonyInt.show();
            } else if (START_IO_AD.equals(AD_ON)) {
                StartAppAd.showAd(a);
                setCompleted(true);
            } else if (AD_NOT_LOAD_CREDIT.equals("on")) {
                setCompleted(true);
            }
        }
    }
}
