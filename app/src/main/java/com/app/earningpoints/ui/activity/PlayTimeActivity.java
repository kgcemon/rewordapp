package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.REMOVE;
import static com.app.earningpoints.util.Fun.data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ankushgrover.hourglass.Hourglass;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivityPlayTimeBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.databinding.ProgressDialogBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Constant_Api;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayTimeActivity extends AppCompatActivity  {
    ActivityPlayTimeBinding bind;
    Activity activity;
    Session session;
    String url,title,time,id,type;
    Hourglass hourglass;
    long reamintime;
    boolean pause=false,timerComplete,first=false,check=true,timestart;
    RewardAds.Builder adNetwork;
    private AlertDialog bonus_dialog;
    AdManager adManager;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    KProgressHUD progressHUD;
    WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bind=ActivityPlayTimeBinding.inflate(getLayoutInflater());
        if(Constant_Api.ScreenType==1){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(bind.getRoot());

        activity=this;
        session=new Session(activity);

        progressHUD =KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.8f)
                .show();


        adManager=new AdManager(activity);
        adManager.loadBannerAd(bind.BANNER);
        loadReward();

        url=getIntent().getStringExtra("url");
        title=getIntent().getStringExtra("title");
        time=getIntent().getStringExtra("time");
        id=getIntent().getStringExtra("id");
        type=getIntent().getStringExtra("type");


        assert type != null;
        if(type.equals("video")){
            bind.timerlyt.setVisibility(View.VISIBLE);
        }else if(type.equals("web")){
            bind.timerlyt.setVisibility(View.VISIBLE);
        }

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        Objects.requireNonNull(bonus_dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        webView=findViewById(R.id.webview);
        WebSettings webSettings =webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);


    }

    private void startTimer() {
        hourglass = new Hourglass(Integer.parseInt(time)*60000L, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimerTick(long timeRemaining) {
                Log.e("PLAY_TIME_TAG", "onTimerTick: "+(int)timeRemaining/1000);
                reamintime=timeRemaining;
                bind.tvTimer.setText(String.valueOf((int)timeRemaining/1000));

            }

            @Override
            public void onTimerFinish() {
                if(reamintime==0) {
                    timerComplete=true;
                    if (type.equals("video")) {
                        creditVideo();
                    } else if (type.equals("game")) {
                        credit();
                    } else if (type.equals("web")) {
                        creditWeb();
                    }
                }
            }
        };

        hourglass.startTimer();
    }

    private void creditWeb() {
        showDialog();
        ApiClient.getClient(this).create(ApiInterface.class).Api(data("","","","","","",9,Integer.parseInt(id),session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && Objects.requireNonNull(response.body()).getCode()==201){
                    Constant_Api.REMOVE=true;
                    session.setIntData(session.WALLET,response.body().getBalance());
                    showbonus(response.body().getMsg(),"");
                }else {
                    showbonus(response.body().getMsg(),"error");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void showDialog() {
        if(!activity.isFinishing() && !progressHUD.isShowing()){
            progressHUD.show();
        }
    }

    private void dismissDialog() {
        if(progressHUD.isShowing()){
            progressHUD.dismiss();
        }
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            showDialog();
            view.loadUrl(url);
            return false;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            dismissDialog();
            if(!timestart){
                timestart=true;
                startTimer();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            bind.webview.loadUrl("file:///android_asset/error.html");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(pause) {
            pause = false;
        }
        webView.onPause();
    }

    private void creditVideo(){
        showDialog();
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(data("","","","","","",10,Integer.parseInt(id),session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.setIntData(session.WALLET,response.body().getBalance());
                    check=false;
                    REMOVE=true;
                    showbonus(response.body().getMsg(),"");
                }else {
                    showbonus(response.body().getMsg(),"error");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void credit() {
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).Api(data("", "", "", "", "", "", 14, Integer.parseInt(id), session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(@NonNull Call<CallbackResp> call, @NonNull Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    session.setIntData(session.WALLET, response.body().getBalance());
                    Log.e("Game_CREDIT", "onResponse: " + response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void loadReward() {
        adNetwork= new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }


    void showbonus(String msg, String type){
        bonus_dialog.show();

        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(Lang.close);
        if(type.equals("error")){
            layoutCollectBonusBinding.congrts.setText(Lang.oops);
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.red));
        }
        layoutCollectBonusBinding.closebtn.setOnClickListener(view -> {bonus_dialog.dismiss();});
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }else{
            webView.stopLoading();
            webView.destroy();

            if(bonus_dialog.isShowing()){
                bonus_dialog.dismiss();
            }
            if(hourglass.isRunning()){
                hourglass.stopTimer();
                hourglass=null;
            }
            if(adNetwork.isAdLoaded()){
                adNetwork.showReward();
            }
            super.onBackPressed();
        }
    }


    public void onStop() {
        super.onStop();
        YTVideoActivity.SharedData.runningActivities--;
        if (YTVideoActivity.SharedData.runningActivities == 0) {
            Log.e("SharedData__", "onStop: ");
            pauseTimer();
            pause=true;
        }
    }

    private void resumeTimer(){
        if(hourglass!=null && hourglass.isPaused()){
            hourglass.resumeTimer();
        }
    }
    private void pauseTimer(){
        if(hourglass!=null){
            hourglass.pauseTimer();
        }

    }

}