package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.AD_ADMOB;
import static com.app.earningpoints.util.Constant_Api.AD_FB;
import static com.app.earningpoints.util.Constant_Api.AD_START_IO;
import static com.app.earningpoints.util.Constant_Api.NATIVE_COUNT;
import static com.app.earningpoints.util.Constant_Api.NATIVE_TYPE;
import static com.app.earningpoints.util.Constant_Api.Pos;
import static com.app.earningpoints.util.Constant_Api.REMOVE;
import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Fun.data;
import static com.app.earningpoints.util.Fun.isConnected;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.TaskResp;
import com.app.earningpoints.adapters.TaskAdapter;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivityWeburlBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.Constant_Api;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeburlActivity extends AppCompatActivity implements  OnItemClickListener {
    ActivityWeburlBinding binding;
    Activity activity;
    AdManager adManager;
    Session session;
    TaskAdapter adapter;
    List<TaskResp> list;
    int item,pos;
    String id;
    CountDownTimer countDownTimer;
    boolean isTimerFinish,isTimerRunning;
    private AlertDialog dialog,bonus_dialog;
    RewardAds.Builder adNetwork;
    ProgressDialog pb;
    private static final int REQUEST_CODE = 100;
    public static final  String TAG="WebUrlActivity : ";
    LayoutCollectBonusBinding layoutCollectBonusBinding;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWeburlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity=WeburlActivity.this;
        session = new Session(activity);
        adManager=new AdManager(activity);
        adManager.loadBannerAd(binding.BANNER);
        adManager.loadInterstitalAd();

        dialog= Fun.loading(activity);
        pb = new ProgressDialog(activity);
        pb.setMessage(Lang.loading);
        pb.setCancelable(false);
        loadReward();


        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        Objects.requireNonNull(bonus_dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        binding.toolbar.setText(TOOLBAR_TITLE);
        list=new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(list,activity);
        binding.recyclerview.setAdapter(adapter);
        adapter.setClickListener(this);

        if (isConnected(this)){
            getdata();
        }else {
            Fun.Error(activity, Lang.no_internet);
        }

        binding.back.setOnClickListener(v->{
            onBackPressed();
        });
    }


    private void getdata(){
        ApiClient.getClient(activity).create(ApiInterface.class).ApiTask(Fun.data("","","","","","",7,2,session.Auth(),1)).enqueue(new Callback<List<TaskResp>>() {
           @Override
           public void onResponse(Call<List<TaskResp>> call, Response<List<TaskResp>> response) {
               if(response.isSuccessful() && response.body().size()>0){
                   showItem(true);
                   displayData(response);
               }else {
                   showItem(false);
               }
           }

           @Override
           public void onFailure(Call<List<TaskResp>> call, Throwable t) {
              showItem(false);
           }
       });
    }

    private void showItem(boolean item) {
        if(item){
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
        }else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);
            binding.noResult.tvNoResultFound.setText(Lang.no_result_found);
        }
    }
    private void loadReward() {
        adNetwork = new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }


    @Override
    protected void onResume() {
        if(REMOVE){
            removeItem(Pos);
        }
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            Log.e(TAG, "onActivityResult: " );
            if(adNetwork.isAdLoaded()){
                Log.e(TAG, "isAdLoaded: " );
                adNetwork.showReward();
            }else{
                Log.e(TAG, "Not Loaded: " );
                Toast.makeText(activity, "Not Loaded", Toast.LENGTH_SHORT).show();
            }

            if(isTimerFinish){
                isTimerFinish=false;
                if (adNetwork.isAdLoaded()) {
                    adNetwork.showReward();
                } else {
                    pb.show();
                    loadReward();
                    new Handler().postDelayed(() -> {
                        pb.dismiss();
                        if (adNetwork.isAdLoaded()) {
                            adNetwork.showReward();
                        }else{
                            Toast.makeText(activity, "Try Again No ads Available", Toast.LENGTH_SHORT).show();
                        }
                    }, 5000);
                }
            }else{
                countDownTimer.cancel();
                countDownTimer=null;
                isTimerRunning=false;
                if (adNetwork.isAdLoaded()) {
                    adNetwork.showReward();
                } else {
                    pb.show();
                    loadReward();
                    new Handler().postDelayed(() -> {
                        pb.dismiss();
                        if (adNetwork.isAdLoaded()) {
                            adNetwork.showReward();
                        }
                    }, 5000);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayData(Response<List<TaskResp>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            list.add(response.body().get(i));
            item++;
            if (item == NATIVE_COUNT) {
                item = 0;
                if (NATIVE_TYPE.equals(AD_FB)) {
                    list.add(new TaskResp().setViewType(3));
                } else if (NATIVE_TYPE.equals(AD_ADMOB)) {
                    list.add(new TaskResp().setViewType(4));
                } else if (NATIVE_TYPE.equals(AD_START_IO))
                    list.add(new TaskResp().setViewType(5));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void back(View view) {
        onBackPressed();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();
        Pos=0;
        REMOVE=false;
        if(list.size()<5){
            list.clear();
            getdata();
        }
    }

    @Override
    public void onClick(View view, int position) {
        id=list.get(position).getId();
        this.pos=position;
        Pos=position;

        if(list.get(position).getBrowser_type().equals("0")){
            Intent intent=new Intent(activity, PlayTimeActivity.class);
            intent.putExtra("url",list.get(position).getUrl());
            intent.putExtra("time",list.get(position).getTimer());
            intent.putExtra("point",list.get(position).getPoint());
            intent.putExtra("id",list.get(position).getId());
            intent.putExtra("type","web");
            startActivity(intent);
        }else {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setUrlBarHidingEnabled(true);
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setData(Uri.parse(list.get(position).getUrl()));
            startActivityForResult(customTabsIntent.intent,REQUEST_CODE);
            startTime();
        }
    }

    void startTime(){
        countDownTimer = new CountDownTimer((Integer.parseInt(list.get(pos).getTimer())* 60L)* 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimerRunning=true;
                Log.e(TAG, "onTick: "+(millisUntilFinished/1000) );
                Toast.makeText(activity, ""+(millisUntilFinished/1000), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                credit();
                isTimerRunning=false;
                isTimerFinish=true;
            }
        }.start();
    }

    private void credit() {
        showDialog();
        ApiClient.getClient(this).create(ApiInterface.class).Api(data("","","","","","",9,Integer.parseInt(id),session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
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
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
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

}
