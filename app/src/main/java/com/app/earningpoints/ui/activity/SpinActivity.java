package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.AppSpin;
import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Fun.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.ConfigResp;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivitySpinBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity{
    ActivitySpinBinding binding;
    List<LuckyItem> data = new ArrayList<>();
    private int points;
    TextView spinvideopoint;
    Activity activity;
    Session session;
    private AlertDialog bonus_dialog;
    private static int spin;
    RewardAds.Builder adNetwork;
    AdManager adManager;
    ProgressDialog pb;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = SpinActivity.this;

        session = new Session(this);

        binding.toolbar.setText(TOOLBAR_TITLE);
        binding.tvTodayRemainingSpin.setText(Lang.today_remaining_spin);
        binding.play.setText(Lang.play);

        adManager = new AdManager(activity);
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

        spinvideopoint = findViewById(R.id.spinvideopoint);
        checklimit();

        final LuckyWheelView luckyWheelView = findViewById(R.id.luckyWheel);
        findViewById(R.id.play).setEnabled(true);
        findViewById(R.id.play).setAlpha(1f);

        ConfigResp.SpinItem spin = AppSpin;

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = spin.getPosition1();
        luckyItem1.color = Color.parseColor(spin.getPc1());
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = spin.getPosition2();
        luckyItem2.color = Color.parseColor(spin.getPc2());
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = spin.getPosition3();
        luckyItem3.color = Color.parseColor(spin.getPc3());
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = spin.getPosition4();
        luckyItem4.color = Color.parseColor(spin.getPc4());
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = spin.getPosition5();
        luckyItem5.color = Color.parseColor(spin.getPc5());
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = spin.getPosition6();
        luckyItem6.color = Color.parseColor(spin.getPc6());
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = spin.getPosition7();
        luckyItem7.color = Color.parseColor(spin.getPc7());
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = spin.getPosition8();
        luckyItem8.color = Color.parseColor(spin.getPc8());
        data.add(luckyItem8);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(getRandomRound());


        findViewById(R.id.play).setOnClickListener(view -> {

            int index = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
            findViewById(R.id.play).setEnabled(false);
            findViewById(R.id.play).setAlpha(.5f);
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(index -> {
            if (index == 1) {
                points = Integer.parseInt(spin.getPosition1());
            }
            if (index == 2) {
                points = Integer.parseInt(spin.getPosition2());
            }
            if (index == 3) {
                points = Integer.parseInt(spin.getPosition3());
            }
            if (index == 4) {
                points = Integer.parseInt(spin.getPosition4());
            }
            if (index == 5) {
                points = Integer.parseInt(spin.getPosition5());
            }
            if (index == 6) {
                points = Integer.parseInt(spin.getPosition6());
            }
            if (index == 7) {
                points = Integer.parseInt(spin.getPosition7());
            }
            if (index == 8) {
                points = Integer.parseInt(spin.getPosition8());
            }

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
                        binding.play.setEnabled(true);
                        binding.play.setAlpha(1f);
                        Toast.makeText(activity, "Try Again No ads Available", Toast.LENGTH_SHORT).show();
                    }
                }, 5000);
            }
        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void loadReward() {
        adNetwork = new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }

    private void credit() {
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(data("1", "", "", "", "", "", 8, points, session.Auth(),spin)).enqueue(new Callback<CallbackResp>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getCode() == 201) {
                    session.setIntData(session.WALLET, response.body().getBalance());
                    showbonus(response.body().getMsg(), "");
                    enableSpin();
                    if (spin > 0) {
                        spin = spin - 1;
                        spinvideopoint.setText(String.valueOf(spin));
                    } else {
                        spinvideopoint.setText(String.valueOf(spin));
                    }
                } else {
                    assert response.body() != null;
                    showbonus(response.body().getMsg(), "error");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    void showbonus(String msg, String type) {
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

    private void checklimit() {
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(data("1", "", "", "", "", "", 4, 1, session.Auth(),spin)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    int spinlimt = response.body().getSpinlimit();
                    int count = response.body().getCount();
                    if (count == 0) {
                        spinvideopoint.setText(String.valueOf(spinlimt));
                    } else {
                        int tot = spinlimt - count;
                        spin = tot;
                        spinvideopoint.setText(String.valueOf(tot));
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    private int getRandomIndex() {
        int[] ind = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        int rand = new Random().nextInt(ind.length);
        return ind[rand];
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
            enableSpin();
        }
        super.onResume();
    }

    private void enableSpin() {
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.play.setText("" + (millisUntilFinished / 1000));
                binding.play.setText(Lang.play);
            }

            @Override
            public void onFinish() {
                binding.play.setEnabled(true);
                binding.play.setAlpha(1f);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void back(View view) {
        onBackPressed();
    }


}