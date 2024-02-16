package com.app.earningpoints.ui.activity;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Fun.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.ankushgrover.hourglass.Hourglass;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivityYTVideoBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.adsManager.AdManager;
import static com.app.earningpoints.util.Constant_Api.REMOVE;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class YTVideoActivity extends AppCompatActivity  {
    boolean pause=false,first=false,check=true,Rewardmode;
    ActivityYTVideoBinding binding;
    TextView tvTimer;
    int timer, newtimer;
    String userid, video_id, id, point;
    Activity activity;
    YouTubePlayerView youTubePlayerView;
    Session session;
    RewardAds.Builder adNetwork;
    private AlertDialog dialog,bonus_dialog;
    Hourglass hourglass;
    long reamintime;
    AdManager adManager;
    ProgressDialog pb;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityYTVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setText(TOOLBAR_TITLE);

        session= new Session(this);
        activity=YTVideoActivity.this;
        dialog = Fun.loading(this);
        adManager=new AdManager(activity);
        adManager.loadBannerAd(binding.BANNER);

        pb = new ProgressDialog(activity);
        pb.setMessage(Lang.loading);
        pb.setCancelable(false);
        loadReward();

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        tvTimer = findViewById(R.id.tvTimer);
        timer = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("timer")))*60;

        point = getIntent().getStringExtra("point");
        video_id = getIntent().getStringExtra("video_id");
        id = getIntent().getStringExtra("id");
        userid = session.getData(session.USER_ID);

        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        binding.viewProgressBar.startAnimation(makeVertical);
        binding.viewProgressBar.setSecondaryProgress(timer);
        binding.viewProgressBar.setProgress(0);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = video_id;
                youTubePlayer.loadVideo(videoId, 1);
                hourglass = new Hourglass(timer*1000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimerTick(long timeRemaining) {
                        // Update UI
                        tvTimer.setText(""+(int)timeRemaining/1000);
                        Log.e("TAG", "onTimerTick: "+(int)timeRemaining/1000);
                        reamintime=timeRemaining;
                    }

                    @Override
                    public void onTimerFinish() {
                        // Timer finished
                        binding.tvTimer.setText("Completed");
                        binding.viewProgressBar.setVisibility(View.GONE);
                        if(reamintime==0){
                            Rewardmode=true;
                            if (adNetwork.isAdLoaded()) {
                                adNetwork.showReward();
                            } else {
                                pb.show();
                                loadReward();
                                new Handler().postDelayed(() -> {
                                    pb.dismiss();
                                    if (adNetwork.isAdLoaded()) {
                                        adNetwork.showReward();
                                    } else {
                                        Toast.makeText(activity, "Try Again No ads Available", Toast.LENGTH_SHORT).show();
                                    }
                                }, 5000);
                            }
                        }
                        else {
                            if (adNetwork.isAdLoaded()) {
                                adNetwork.showReward();
                            } else {
                                pb.show();
                                loadReward();
                                new Handler().postDelayed(() -> {
                                    pb.dismiss();
                                    if (adNetwork.isAdLoaded()) {
                                        adNetwork.showReward();
                                    } else {
                                        Toast.makeText(activity, "Try Again No ads Available", Toast.LENGTH_SHORT).show();
                                    }
                                }, 5000);
                            }
                        }
                    }
                };

                hourglass.startTimer();
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                System.out.println("YT_STATE"+state.toString());
                if(state.toString().equals("PAUSED")){
                    pauseTimer();
                }else if(state.toString().equals("PLAYING")){
                    resumeTimer();
                }else if(state.toString().equals("BUFFERING")){
                    pauseTimer();
                }else if(state.toString().equals("UNSTARTED")){
                    pauseTimer();
                }
            }
        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void credit(){
        showDialog();
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(data("","","","","","",10,Integer.parseInt(id),session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(@NonNull Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && Objects.requireNonNull(response.body()).getCode()==201){
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

    @Override
    protected void onResume() {
        super.onResume();
        if(Rewardmode){
            if(adNetwork.isCompleted()){
                credit();
            }
        }
        if(pause) {
            pause = false;
        }
    }

    void showbonus(String msg, String type){
        bonus_dialog.show();
        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(Lang.close);
        if(type.equals("error")){
            layoutCollectBonusBinding.congrts.setText(Lang.oops);
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.red));
        }else {
            layoutCollectBonusBinding.congrts.setText(Lang.congratulations);
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.green));
        }
        layoutCollectBonusBinding.closebtn.setOnClickListener(view -> {bonus_dialog.dismiss();});
    }

    public void onStart() {
        super.onStart();
        if (SharedData.runningActivities == 0) { }
        SharedData.runningActivities++;
    }

    public static class SharedData {
        static int runningActivities = 0;
    }

    public void onStop() {
        super.onStop();
        SharedData.runningActivities--;
        if (SharedData.runningActivities == 0) {
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

    private void loadReward() {
        adNetwork= new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if(hourglass.isRunning()){
            hourglass.stopTimer();
        }
        super.onDestroy();
    }
}
