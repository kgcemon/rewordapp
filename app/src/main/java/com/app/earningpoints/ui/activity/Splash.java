package com.app.earningpoints.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.app.earningpoints.BuildConfig;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.ConfigResp;
import com.app.earningpoints.Responsemodel.LangDataResp;
import com.app.earningpoints.databinding.LayoutDialogBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;

import static com.app.earningpoints.Config.Config.*;
import static com.app.earningpoints.util.Const.*;
import static com.app.earningpoints.util.Const.homeStyle;

import com.app.earningpoints.util.DatabaseHandler;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.ironsource.mediationsdk.IronSource;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.earningpoints.util.Constant_Api.*;
import static com.app.earningpoints.util.Fun.data;
import static com.app.earningpoints.util.Fun.encrypt;
import static com.app.earningpoints.util.Fun.isConnected;
import static com.app.earningpoints.util.Lang.*;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class Splash extends AppCompatActivity {
    Session session;
    AlertDialog alertDialog, dialog;
    Activity activity;
    CountDownTimer count;
    public static final String TAG = "Splash Activity : ";
    DatabaseHandler db;
    LayoutDialogBinding layoutDialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(this);
        activity = this;

        session = new Session(this);
        alertDialog = Fun.Alert(this);
        db = new DatabaseHandler(this);

        layoutDialogBinding = LayoutDialogBinding.inflate(getLayoutInflater());
        dialog = new AlertDialog.Builder(activity).setView(layoutDialogBinding.getRoot()).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (isConnected(this)) {
            loadConfig();
        } else {
            showAlert(no_internet);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void showAlert(String msg) {
        alertDialog.show();
        TextView tv = alertDialog.findViewById(R.id.txt);
        assert tv != null;
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(Lang.okay);
        btn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void loadConfigSetting() {

        Call<ConfigResp> call = Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).getConfig();
        call.enqueue(new Callback<ConfigResp>() {
            @Override
            public void onResponse(@NonNull Call<ConfigResp> call, Response<ConfigResp> response) {
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getSuccess() != 0) {

                    isNpv = response.body().isVpn();

                    homeStyle = response.body().getData().get(0).getHome_style();
                    offerwallStyle = response.body().getData().get(0).getOfferwall_style();
                    offerwallLayout = response.body().getData().get(0).getOfferwall_layout();
                    surveyStyle = response.body().getData().get(0).getSurvey_style();
                    surveyLayout = response.body().getData().get(0).getSurvey_layout();
                    AppSpin = response.body().getSpin().get(0);
                    db.removeData();
                    db.insert(response.body().getOffers());

                    prepareLang(response.body().getLang());
                    parseData(response.body().getData());

                    if (response.body().getData().get(0).isUp_status()) {
                        if (response.body().getData().get(0).getUp_mode().equals(UPDATE)) {
                            if (BuildConfig.VERSION_CODE < response.body().getData().get(0).getUp_version()) {
                                show(response.body().getData().get(0).getUp_msg(), "update", response.body().getData().get(0).getUp_link(), response.body().getData().get(0).isUp_btn());
                            } else {
                                doTask();
                            }
                        } else {
                            show(response.body().getData().get(0).getUp_msg(), MAINTENANCE, "", false);
                        }
                    } else {
                        doTask();
                    }
                } else {
                    showAlert(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ConfigResp> call, Throwable t) {
            }
        });
    }


    private void parseData(List<ConfigResp.DataItem> data) {
        try {

            BANNER_ID = data.get(0).getBannerid();
            BANNER_TYPE = data.get(0).getBannerType();

            INTERSTITAL_ID = data.get(0).getInterstital_ID();
            INTERSTITAL_TYPE = data.get(0).getInterstital_type();
            INTERSTITAL_ADUNIT = data.get(0).getInterstital_ID();
            INTERSTITAL_COUNT = data.get(0).getInterstital_count();

            NATIVE_ID = data.get(0).getNativeId();
            NATIVE_TYPE = data.get(0).getNativeType();
            NATIVE_ADUNIT = data.get(0).getNativeId();
            NATIVE_COUNT = data.get(0).getNativeCount();

            APP_AUTHOR = data.get(0).getAppAuthor();
            APP_DESCRIPTION = data.get(0).getAppDescription();
            PRIVACY_POLICY = data.get(0).getPrivacyPolicy();

            JSONObject obj = new JSONObject(data.get(0).getAdConfig().replace("[", "").replace("]", ""));
            ADMOB_APP_ID = obj.getString("admob_app_id");
            ADMOB_AD_TYPE = obj.getString("admob_adtype");
            ADMOB_AD_ADUNIT = obj.getString("au_admob");

            FB_AD_TYPE = obj.getString("fb_adtype");
            FB_AD_ADUNIT = obj.getString("au_fb");

            APPLOVIN_AD_TYPE = obj.getString("applovin_adtype");
            APPLOVIN_AD_ADUNIT = obj.getString("au_applovin");

            UNITY_GAME_ID = obj.getString("unity_gameid");
            UNITY_AD_TYPE = obj.getString("unity_adtype");
            UNITY_AD_ADUNIT = obj.getString("au_unity");

            IRONSOURCE_APP_KEY = obj.getString("ironsource_key");
            IRONSOURCE_AD_TYPE = obj.getString("iron_adtype");

            ADCOLONY_APP_ID = obj.getString("adcolony_appid");
            ADCOLONY_ZONE_ID = obj.getString("adcolony_zoneid");
            ADCOLONY_AD_TYPE = obj.getString("adcolony_adtype");

            START_IO_APPID = obj.getString("startio_id");
            START_IO_AD = obj.getString("ad_startio");
            AD_NOT_LOAD_CREDIT = obj.getString("ad_not_load_credit");

            prepareAd();
        } catch (Exception e) {
            Toast.makeText(activity, "Parse Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void prepareAd() throws PackageManager.NameNotFoundException {
        if (ADMOB_APP_ID != null && !ADMOB_APP_ID.equals("ca-app-pub-3940256099942544~3347511713")) {
            ApplicationInfo ai = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", ADMOB_APP_ID);
            MobileAds.initialize(this);
        }

        if (IRONSOURCE_APP_KEY != null && !IRONSOURCE_APP_KEY.equals("") || BANNER_TYPE.equals(IRONSOURCE_AD_TYPE) || INTERSTITAL_TYPE.equals(IRONSOURCE_AD_TYPE) || !IRONSOURCE_AD_TYPE.equals(AD_OFF)) {
            IronSource.init(activity, IRONSOURCE_APP_KEY);
        }

        if (!FB_AD_TYPE.equals(AD_OFF) || BANNER_TYPE.equals(FB_AD_TYPE) || INTERSTITAL_TYPE.equals(FB_AD_TYPE)) {
            AudienceNetworkAds.initialize(activity);
        }

        if (START_IO_APPID != null && !START_IO_APPID.equals("") || BANNER_TYPE.equals(AD_START_IO) || INTERSTITAL_TYPE.equals(AD_START_IO) || NATIVE_TYPE.equals(AD_START_IO) || !START_IO_AD.equals(AD_OFF)) {
            StartAppAd.disableSplash();
            StartAppSDK.init(activity, START_IO_APPID, false);
        }

        if (!getString(R.string.APPLOVIN_SDK_KEY).equals("xxx")) {
            AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
            AppLovinSdk.getInstance(activity).initializeSdk(config -> {
            });
        }

        if (UNITY_GAME_ID != null) {
            UnityAds.initialize(activity.getApplicationContext(), UNITY_GAME_ID, false, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    Log.d(TAG, "Unity Ads Initialization Complete with ID : " + UNITY_GAME_ID);
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    Log.d(TAG, "Unity Ads Initialization Failed: [" + error + "] " + message);
                }
            });
        }

        if (ADCOLONY_APP_ID != null) {
            AdColonyAppOptions appOptions = new AdColonyAppOptions().setGDPRConsentString("1")
                    .setKeepScreenOn(true).setGDPRRequired(false);
            AdColony.configure(activity, appOptions, ADCOLONY_APP_ID);
        }

    }

    private String getLang() {
        if (session.getData(session.SELECTED_LANGUAGE) != null) {
            DEFAULT_LANGUAGE = session.getData(session.SELECTED_LANGUAGE);
        }
        return DEFAULT_LANGUAGE;
    }

    private void doTask() {
        if (session.getBoolean(session.LOGIN)) {
            reqLogin(session.getData(session.EMAIL), session.getData(session.PASSWORD), session.Auth());
        } else {
            if (!session.getBoolean(session.FIRSTTIME)) {

                startActivity(new Intent(Splash.this,LanguageActivity.class).putExtra("type","start"));
            } else {
                startActivity(new Intent(Splash.this, FrontLogin.class));
            }
        }
    }

    private void loadConfig() {
        loadConfigSetting();
    }


    private void reqLogin(String email, String password, String randomNumber) {
        Call<CallbackResp> login = ApiClient.getClient(this).create(ApiInterface.class).ApiUser(data("", email, password, randomNumber, "", "", 1, 0, session.Auth(), 0));
        login.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    session.saveUserData(response.body().getUser().getGoogle(),
                            response.body().getUser().getEmail(),
                            response.body().getUser().getPhone(),
                            response.body().getUser().getProfile(),
                            response.body().getUser().getName(),
                            password,
                            response.body().getUser().getRefferalId(),
                            response.body().getUser().getFrom_refer(),
                            AUTH + encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(), ""),
                            response.body().getUser().getType());
                    session.setData(session.emailVerified, response.body().getUser().getEmailVerified());
                    session.setIntData(session.WALLET, response.body().getUser().getBalance());
                    session.setBoolean(session.LOGIN, true);
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Splash.this, FrontLogin.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                Log.e("splash", "onFailure: " + t.getMessage());
            }
        });
    }

    void show(String msg, String type, String url, boolean skip) {
        dialog.show();

        layoutDialogBinding.no.setText(Lang.skip);

        if (type.equals(MAINTENANCE)) {
            layoutDialogBinding.yes.setVisibility(View.GONE);
            layoutDialogBinding.img.setImageResource(R.drawable.ic_baseline_cloud_off_24);
            layoutDialogBinding.congrts.setText(maintenance);
            layoutDialogBinding.congrts.setTextColor(getResources().getColor(R.color.red));
            layoutDialogBinding.no.setText(close);
        } else {
            layoutDialogBinding.img.setImageResource(R.drawable.ic_baseline_autorenew_24);
            layoutDialogBinding.congrts.setText(update_available);
            layoutDialogBinding.yes.setVisibility(View.VISIBLE);
            layoutDialogBinding.yes.setText(update);
        }
        if (!skip) {
            layoutDialogBinding.no.setVisibility(View.GONE);
        }
        layoutDialogBinding.no.setOnClickListener(view -> {
            if (type.equals(MAINTENANCE)) {
                dialog.dismiss();
            } else {
                doTask();
            }
        });

        layoutDialogBinding.yes.setOnClickListener(view -> {
            if (type.equals(MAINTENANCE)) {
                dialog.dismiss();
            } else {
                openUrl(url);
            }
        });
    }

    private void openUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception ignored) {
        }
    }

    private void prepareLang(List<LangDataResp> lang) {
        int i=0;
        enter_email = lang.get(i).getTxt_value();
        enter_pass = lang.get(++i).getTxt_value();
        email = lang.get(++i).getTxt_value();
        password = lang.get(++i).getTxt_value();
        create_account = lang.get(++i).getTxt_value();
        forgot_password = lang.get(++i).getTxt_value();
        sign_in_to_continue = lang.get(++i).getTxt_value();
        welcome_back = lang.get(++i).getTxt_value();
        error_invalid_email = lang.get(++i).getTxt_value();
        username = lang.get(++i).getTxt_value();
        phone = lang.get(++i).getTxt_value();
        invite_friend_get_bonus = lang.get(++i).getTxt_value();
        fill_detail_continue = lang.get(++i).getTxt_value();
        home = lang.get(++i).getTxt_value();
        instructions = lang.get(++i).getTxt_value();
        notification = lang.get(++i).getTxt_value();
        history = lang.get(++i).getTxt_value();
        feedback = lang.get(++i).getTxt_value();
        privacy_policy = lang.get(++i).getTxt_value();
        about_us = lang.get(++i).getTxt_value();
        rate_us = lang.get(++i).getTxt_value();
        already_have_an_account_signin_here = lang.get(++i).getTxt_value();
        signup = lang.get(++i).getTxt_value();
        email_address = lang.get(++i).getTxt_value();
        phone_no = lang.get(++i).getTxt_value();
        refer_id_if_available = lang.get(++i).getTxt_value();
        company = lang.get(++i).getTxt_value();
        name = lang.get(++i).getTxt_value();
        version = lang.get(++i).getTxt_value();
        contact_us = lang.get(++i).getTxt_value();
        website = lang.get(++i).getTxt_value();
        current_coin = lang.get(++i).getTxt_value();
        _00000 = lang.get(++i).getTxt_value();
        rewards = lang.get(++i).getTxt_value();
        connect_with_us = lang.get(++i).getTxt_value();
        www_example_com = lang.get(++i).getTxt_value();
        who_we_are = lang.get(++i).getTxt_value();
        more_offers = lang.get(++i).getTxt_value();
        invite = lang.get(++i).getTxt_value();
        confirm = lang.get(++i).getTxt_value();
        coin_history = lang.get(++i).getTxt_value();
        reward_history = lang.get(++i).getTxt_value();
        no_internet = lang.get(++i).getTxt_value();
        enter_username = lang.get(++i).getTxt_value();
        enter_phone = lang.get(++i).getTxt_value();
        ok = lang.get(++i).getTxt_value();
        coin = lang.get(++i).getTxt_value();
        try_again_later = lang.get(++i).getTxt_value();
        logout = lang.get(++i).getTxt_value();
        invite_friend = lang.get(++i).getTxt_value();
        tap_copy = lang.get(++i).getTxt_value();
        your_refer_code = lang.get(++i).getTxt_value();
        profile = lang.get(++i).getTxt_value();
        hello = lang.get(++i).getTxt_value();
        mobile = lang.get(++i).getTxt_value();
        play = lang.get(++i).getTxt_value();
        scratch_again = lang.get(++i).getTxt_value();
        today_remaining_spin = lang.get(++i).getTxt_value();
        today_remaining_scratch = lang.get(++i).getTxt_value();
        today_remaining_quiz = lang.get(++i).getTxt_value();
        skip = lang.get(++i).getTxt_value();
        congratulations = lang.get(++i).getTxt_value();
        share = lang.get(++i).getTxt_value();
        complete_challenge = lang.get(++i).getTxt_value();
        redeem_reward = lang.get(++i).getTxt_value();
        loading = lang.get(++i).getTxt_value();
        please_enter_id = lang.get(++i).getTxt_value();
        submit = lang.get(++i).getTxt_value();
        task_challenge = lang.get(++i).getTxt_value();
        no_result_found = lang.get(++i).getTxt_value();
        refer_desc = lang.get(++i).getTxt_value();
        we_sent_otp_on_email = lang.get(++i).getTxt_value();
        verify = lang.get(++i).getTxt_value();
        otp_verification = lang.get(++i).getTxt_value();
        enter_registerd_email = lang.get(++i).getTxt_value();
        confirm_password = lang.get(++i).getTxt_value();
        create_new_password = lang.get(++i).getTxt_value();
        new_password = lang.get(++i).getTxt_value();
        no_thanks = lang.get(++i).getTxt_value();
        please_wait = lang.get(++i).getTxt_value();
        vpn_not_connected = lang.get(++i).getTxt_value();
        read_article_subtitle = lang.get(++i).getTxt_value();
        proceed = lang.get(++i).getTxt_value();
        get_started = lang.get(++i).getTxt_value();
        next = lang.get(++i).getTxt_value();
        rate_our_app = lang.get(++i).getTxt_value();
        share_experience_with_us = lang.get(++i).getTxt_value();
        okay = lang.get(++i).getTxt_value();
        close = lang.get(++i).getTxt_value();
        oops = lang.get(++i).getTxt_value();
        play_game = lang.get(++i).getTxt_value();
        exit = lang.get(++i).getTxt_value();
        confirm_exit = lang.get(++i).getTxt_value();
        yes = lang.get(++i).getTxt_value();
        no = lang.get(++i).getTxt_value();
        top_picks = lang.get(++i).getTxt_value();
        play_zone = lang.get(++i).getTxt_value();
        history_subtitle = lang.get(++i).getTxt_value();
        contact_us_subtitle = lang.get(++i).getTxt_value();
        rewards_subtitle = lang.get(++i).getTxt_value();
        feedback_subtitle = lang.get(++i).getTxt_value();
        logout_accout = lang.get(++i).getTxt_value();
        privacy_policy_subtitle = lang.get(++i).getTxt_value();
        support = lang.get(++i).getTxt_value();
        my_coin = lang.get(++i).getTxt_value();
        are_you_sure_you_want_to_logout = lang.get(++i).getTxt_value();
        maintenance = lang.get(++i).getTxt_value();
        update = lang.get(++i).getTxt_value();
        update_available = lang.get(++i).getTxt_value();
        refer_code = lang.get(++i).getTxt_value();
        login = lang.get(++i).getTxt_value();
        new_user = lang.get(++i).getTxt_value();
        copy = lang.get(++i).getTxt_value();
        complete_offer = lang.get(++i).getTxt_value();
        task_not_completed = lang.get(++i).getTxt_value();
        choose_language_subtitle = lang.get(++i).getTxt_value();
        choose_language = lang.get(++i).getTxt_value();
        tutorial = lang.get(++i).getTxt_value();
        submit_answer = lang.get(++i).getTxt_value();
        write_answer = lang.get(++i).getTxt_value();
        enter_answer = lang.get(++i).getTxt_value();
        fill_required_detail = lang.get(++i).getTxt_value();
        detail = lang.get(++i).getTxt_value();
        password_update_message = lang.get(++i).getTxt_value();
        password_not_match = lang.get(++i).getTxt_value();
        invalid_otp = lang.get(++i).getTxt_value();
        completed = lang.get(++i).getTxt_value();
        delete_my_account = lang.get(++i).getTxt_value();
        delete_account_subtitle = lang.get(++i).getTxt_value();
        are_you_sure = lang.get(++i).getTxt_value();
        delete_account_message = lang.get(++i).getTxt_value();
        have_a_refer_code = lang.get(++i).getTxt_value();
        you_ve_won = lang.get(++i).getTxt_value();
        apply = lang.get(++i).getTxt_value();
        _1 = lang.get(++i).getTxt_value();
        _2 = lang.get(++i).getTxt_value();
        _3 = lang.get(++i).getTxt_value();
        continue_with = lang.get(++i).getTxt_value();
        leaderboard = lang.get(++i).getTxt_value();
        reffer_history = lang.get(++i).getTxt_value();
        today_joined = lang.get(++i).getTxt_value();
        all_joined = lang.get(++i).getTxt_value();
        my_profile = lang.get(++i).getTxt_value();
        my_profile_subtitle = lang.get(++i).getTxt_value();
        update_password = lang.get(++i).getTxt_value();
        old_password = lang.get(++i).getTxt_value();
        welcome = lang.get(++i).getTxt_value();
        top_surveys = lang.get(++i).getTxt_value();
        top_offers = lang.get(++i).getTxt_value();
        claim_refer = lang.get(++i).getTxt_value();
        right_answer = lang.get(++i).getTxt_value();
        wrong_answer = lang.get(++i).getTxt_value();
        refer_history = lang.get(++i).getTxt_value();
        no_ad_available = lang.get(++i).getTxt_value();
        enter = lang.get(++i).getTxt_value();
        watch_video_subtitle = lang.get(++i).getTxt_value();
        intro_slide_one_title = lang.get(++i).getTxt_value();
        intro_slide_one_description = lang.get(++i).getTxt_value();
        intro_slide_two_title = lang.get(++i).getTxt_value();
        intro_slide_two_description = lang.get(++i).getTxt_value();
        intro_slide_three_title = lang.get(++i).getTxt_value();
        intro_slide_three_description = lang.get(++i).getTxt_value();
    }


}
