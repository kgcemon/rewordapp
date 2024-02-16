package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Lang.submit_answer;
import static com.app.earningpoints.util.Lang.today_remaining_quiz;
import static com.app.earningpoints.util.Lang.write_answer;
import static com.app.earningpoints.util.Lang.wrong_answer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivityMathQuizBinding;
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

public class MathQuiz extends AppCompatActivity  {
    ActivityMathQuizBinding bind;
    Session session;
    Activity activity;
    AdManager adManager;
    RewardAds.Builder adNetwork;
    private static int quiz;
    int num1, num2,total;
    boolean complete=false;
    ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityMathQuizBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        activity=this;
        bind.toolbar.setText(TOOLBAR_TITLE);

        session=new Session(activity);
        adManager=new AdManager(this);
        adManager.loadBannerAd(bind.BANNER);

        pb = new ProgressDialog(activity);
        pb.setMessage(Lang.loading);
        pb.setCancelable(false);

        bind.tvTodayRemainingQuiz.setText(today_remaining_quiz);
        bind.ans.setHint(write_answer);
        bind.play.setText(submit_answer);
        Que();
        loadReward();
        checklimit();

        bind.play.setOnClickListener(v -> {
            if(bind.ans.getText().toString().isEmpty()){
                Toast.makeText(activity,Lang.enter_answer, Toast.LENGTH_SHORT).show();
            }else{
                if(total==Integer.parseInt(bind.ans.getText().toString().trim())){
                    complete=true;
                    showads();
                    Toast.makeText(this, Lang.right_answer, Toast.LENGTH_SHORT).show();
                    bind.play.setEnabled(false);
                    bind.play.setAlpha(0.1f);
                }else{
                    showads();
                    Toast.makeText(this, wrong_answer, Toast.LENGTH_SHORT).show();
                    Que();
                }
            }
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void showads() {
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


    void Que(){
        Random random = new Random();
        num1=random.nextInt(60);
        num2=random.nextInt(99);
        bind.que.setText("What is Result of "+num1+" + "+num2);
        bind.ans.setText(null);
        total=num1+num2;
        bind.play.setAlpha(1f);
        bind.play.setEnabled(true);

    }

    private void checklimit() {
        ApiClient.getClient(this).create(ApiInterface.class).Api(Fun.data("2","","","","","",4,2,session.Auth(),quiz)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful() && response.body().getCode()==201){
                    int spinlimt=response.body().getSpinlimit();
                    int count=response.body().getCount();
                    if(count==0){
                        bind.limit.setText(String.valueOf(spinlimt));
                    }else {
                        int tot=spinlimt-count;
                        quiz=tot;
                        bind.limit.setText(String.valueOf(tot));
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    private void loadReward() {
        adNetwork= new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }

    private void credit() {
        ApiClient.getClient(this).create(ApiInterface.class).Api(Fun.data("2","","","","","",8,0,session.Auth(),quiz)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.setIntData(session.WALLET,response.body().getBalance());
                    complete=false;
                    Toast.makeText(activity, ""+response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if(quiz>0){
                        quiz=quiz-1;
                        bind.limit.setText(""+quiz);
                        Que();
                    }else {
                        bind.limit.setText(""+quiz);
                    }
                }else {
                    Toast.makeText(activity, ""+response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if(adNetwork.isCompleted()){
            adNetwork.setCompleted(false);
            adNetwork.buildAd();
            loadReward();
            if(complete){credit();}
        }else{
            loadReward();
        }
        super.onResume();
    }

}