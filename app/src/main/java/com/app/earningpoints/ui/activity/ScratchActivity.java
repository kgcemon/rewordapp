package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.MAX_AMOUNT;
import static com.app.earningpoints.util.Constant_Api.MIN_AMOUNT;
import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivityScratchBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScratchActivity extends AppCompatActivity {
    Activity activity;
    ActivityScratchBinding bind;
    Session session;
    private int points;
    private AlertDialog bonus_dialog;
    private static int scratchCount;
    RewardAds.Builder adNetwork;
    AdManager adManager;
    ProgressDialog pb;
    TextView spinvideopoint;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityScratchBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity = this;
        session=new Session(activity);

        bind.toolbar.setText(TOOLBAR_TITLE);
        bind.tvTodayRemainingScratch.setText(Lang.today_remaining_scratch);
        bind.tvYouVeWon.setText(Lang.you_ve_won);
        bind.play.setText(Lang.scratch_again);

        adManager = new AdManager(activity);
        adManager.loadBannerAd(bind.BANNER);
        adManager.loadInterstitalAd();

        pb = new ProgressDialog(activity);
        pb.setMessage(Lang.loading);
        pb.setCancelable(false);

        loadReward();
        handleListeners();

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        spinvideopoint = bind.spinvideopoint;
        checklimit();

        bind.play.setOnClickListener(v -> {
            startActivity(getIntent());
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void credit() {
        ApiClient.getClient(this).create(ApiInterface.class).Api(Fun.data("3", "", "", "", "", "", 8, points, session.Auth(),scratchCount)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    session.setIntData(session.WALLET, response.body().getBalance());
                    showbonus(response.body().getMsg(), "");
                    enableScratch();
                    if (scratchCount > 0) {
                        scratchCount = scratchCount - 1;
                        spinvideopoint.setText("" + scratchCount);
                    } else {
                        spinvideopoint.setText("" + scratchCount);
                    }
                } else {
                    showbonus(response.body().getMsg(), "error");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    private void enableScratch() {
        bind.play.setEnabled(true);
        bind.play.setAlpha(1f);
    }

    private void handleListeners() {
        bind.scratchCard.setOnScratchListener((scratchCard, visiblePercent) -> {
            if (visiblePercent > 0.8) {
                scratch(true);
                bind.codeTxt.setText(generateNewCode());
                bind.codeTxt.setVisibility(View.VISIBLE);
                points= Integer.parseInt(bind.codeTxt.getText().toString());
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
                            bind.play.setVisibility(View.VISIBLE);
                            bind.play.setText(Lang.scratch_again);
                            Toast.makeText(activity, Lang.no_ad_available, Toast.LENGTH_SHORT).show();
                        }
                    }, 5000);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if (adNetwork.isCompleted()) {
            adNetwork.setCompleted(false);
            adNetwork.setAdLoaded(false);
            adNetwork.buildAd();
            credit();
        } else {
            adNetwork.buildAd();
            bind.play.setVisibility(View.VISIBLE);
            bind.play.setText(Lang.scratch_again);
        }
        super.onResume();
    }


    static Random random = new Random();

    public static float dipToPx(Context context, float dipValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return dipValue * density;
    }

    private void scratch(boolean isScratched) {
        if (isScratched) {
            bind.scratchCard.setVisibility(View.INVISIBLE);
        } else {
            bind.scratchCard.setVisibility(View.VISIBLE);
        }
    }

    private static String generateCodePart(	int min, int max)  {
        int minNumber = min;
        int maxNumber = max;
        return String.valueOf((random.nextInt((maxNumber - minNumber) + 1) + minNumber));
    }

    public String generateNewCode() {
        return generateCodePart(MIN_AMOUNT , MAX_AMOUNT);
    }

    void showbonus(String msg, String type) {
        bonus_dialog.show();

        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(Lang.close);
        if(type.equals("error")){
            layoutCollectBonusBinding.congrts.setText(Lang.oops);
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.red));
        }
        layoutCollectBonusBinding.closebtn.setOnClickListener(view -> {bonus_dialog.dismiss();});
    }

    private void checklimit() {
        ApiClient.getClient(this).create(ApiInterface.class).Api(Fun.data("3", "", "", "", "", "", 4, 3, session.Auth(),scratchCount)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    int scLimit = response.body().getSpinlimit();
                    int count = response.body().getCount();
                    MIN_AMOUNT = Integer.parseInt(response.body().getData().substring(0, response.body().getData().indexOf(",")));
                    MAX_AMOUNT = Integer.parseInt(response.body().getData().replace(MIN_AMOUNT + ",", ""));
                    if (count == 0) {
                        spinvideopoint.setText(String.valueOf(scLimit));
                    } else {
                        int tot = scLimit - count;
                        scratchCount = tot;
                        spinvideopoint.setText(String.valueOf(tot));
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    private void loadReward() {
        adNetwork = new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(activity,MainActivity.class));
        finish();
    }
}