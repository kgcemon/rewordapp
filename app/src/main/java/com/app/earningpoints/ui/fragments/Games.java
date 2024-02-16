package com.app.earningpoints.ui.fragments;

import static com.app.earningpoints.util.Constant_Api.Pos;
import static com.app.earningpoints.util.Constant_Api.ScreenType;
import static com.app.earningpoints.util.Fun.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.GameResponse;
import com.app.earningpoints.adapters.GameAdapter;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.FragmentGamesBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.ui.activity.PlayTimeActivity;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Games extends Fragment implements OnItemClickListener {
    FragmentGamesBinding binding;
    Activity activity;
    GameAdapter adapter;
    List<GameResponse.DataItem> list;
    AdManager adManager;
    String id;
    int pos,timer;
    private static final int REQUEST_CODE = 100;
    public static final String TAG = "GameActivity : ";
    CountDownTimer countDownTimer;
    Session session;
    private AlertDialog dialog, bonus_dialog;
    RewardAds.Builder adNetwork;
    ProgressDialog pb;
    boolean isCredit = false;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGamesBinding.inflate(getLayoutInflater());
        activity = getActivity();

        adManager = new AdManager(activity);
        adManager.loadBannerAd(binding.BANNER);
        session = new Session(activity);
        dialog = Fun.loading(activity);

        pb = new ProgressDialog(activity);
        pb.setMessage(Lang.loading);
        pb.setCancelable(false);

        list = new ArrayList<>();
        binding.recyclerViewApps.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new GameAdapter(getActivity(), list);
        adapter.setClickListener(this);
        binding.recyclerViewApps.setAdapter(adapter);

        callGame();

        return binding.getRoot();
    }

    private void callGame() {
        Call<GameResponse> call = ApiClient.getClient(getActivity()).create(ApiInterface.class).getGame();
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body().getSuccess() != 0) {
                    list.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    binding.recyclerViewApps.setVisibility(View.VISIBLE);
                } else {
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        id = list.get(position).getId();
        this.pos = position;
        Pos = position;
        if(list.get(position).getBrowser_type().equals("0")){
            ScreenType= Integer.parseInt(list.get(position).getOrientation());
            Intent intent=new Intent(activity, PlayTimeActivity.class);
            intent.putExtra("id",list.get(position).getId());
            intent.putExtra("title",list.get(position).getTitle());
            intent.putExtra("time",list.get(position).getTime());
            intent.putExtra("url",list.get(position).getLink());
            intent.putExtra("type","game");
            startActivity(intent);
        }else {
            timer= Integer.parseInt(list.get(position).getTime());
            loadReward();
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setUrlBarHidingEnabled(true);
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setData(Uri.parse(list.get(position).getLink()));
            startActivityForResult(customTabsIntent.intent, REQUEST_CODE);
            startTime();
        }

    }

    private void loadReward() {
        adNetwork = new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }


    void startTime() {
        countDownTimer = new CountDownTimer(timer, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "onTick: " + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                credit();
            }
        }.start();
    }

    void showbonus(String msg, String type) {

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);
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

    private void credit() {
        showDialog();
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).Api(data("", "", "", "", "", "", 14, Integer.parseInt(id), session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    isCredit = true;
                    session.setIntData(session.WALLET, response.body().getBalance());
                    showbonus(response.body().getMsg(),"");
                    Log.e("Game_CREDIT", "onResponse: " + response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }


    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            Log.e(TAG, "onActivityResult: ");
//            if (isCredit) {
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
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}