package com.app.earningpoints.ui.fragments;

import static com.app.earningpoints.util.Const.*;
import static com.app.earningpoints.util.Const.homeStyle;
import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Fun.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.BannerResponse;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.ConfigResp;
import com.app.earningpoints.Responsemodel.OfferwallResp;
import com.app.earningpoints.adapters.HomeAdapter;
import com.app.earningpoints.adapters.OfferwallAdapter;
import com.app.earningpoints.adapters.SliderAdapterExample;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.FragmentHomeNewBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.ui.activity.MathQuiz;
import com.app.earningpoints.ui.activity.ScratchActivity;
import com.app.earningpoints.ui.activity.SpinActivity;
import com.app.earningpoints.ui.activity.WeburlActivity;
import com.app.earningpoints.ui.activity.WithdrawActivity;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.DatabaseHandler;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeNew extends Fragment implements  OnItemClickListener {
    FragmentHomeNewBinding binding;
    Session session;
    private AlertDialog bonus_dialog;
    AlertDialog dialog;
    RewardAds.Builder adNetwork;
    AdManager adManager;
    ProgressDialog pb;
    DatabaseHandler db;
    HomeAdapter adapter;
    Activity activity;
    List<ConfigResp.OfferItem> offerItems;
    List<OfferwallResp> offerwallResps=new ArrayList<>();
    List<OfferwallResp> surveyResp=new ArrayList<>();
    OfferwallAdapter offerwallAdapter;
    OfferwallAdapter surveyAdapter;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @SuppressLint({"InflateParams", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeNewBinding.inflate(getLayoutInflater());
        session = new Session(getActivity());
        dialog = Fun.loading(getActivity());

        requireActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

        adManager = new AdManager(getActivity());

        binding.tvTopOffer.setText(Lang.top_offers);
        binding.tvTopSurvey.setText(Lang.top_surveys);

        pb = new ProgressDialog(getActivity());
        pb.setMessage(Lang.loading);
        pb.setCancelable(false);
        activity = getActivity();
        db = new DatabaseHandler(activity);


        offerItems = new ArrayList<>();
        if (!offerItems.isEmpty()) {
            offerItems.clear();
        }

        offerItems = db.getHomeList();
        switch (homeStyle) {
            case 0:
            case 1:
            case 2:
            case 3:
                binding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                break;

            case 4:
                binding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
                break;

            case 5:
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                break;

            case 6:
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
                break;
        }
        adapter = new HomeAdapter(offerItems, getActivity());
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        initOffer();
        getOfferwall();

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        Objects.requireNonNull(bonus_dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        slideBanner();

        return binding.getRoot();
    }

    private void initOffer() {
        switch (offerwallLayout) {
            case 0:
                binding.recyclerViewOfferwall.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.HORIZONTAL, false));
                break;
            case 1:
                binding.recyclerViewOfferwall.setLayoutManager(new GridLayoutManager(activity, 3));
                break;
            case 2:
                binding.recyclerViewOfferwall.setLayoutManager(new LinearLayoutManager(activity));
                break;

        }
        offerwallAdapter = new OfferwallAdapter(offerwallResps, getActivity());
        binding.recyclerViewOfferwall.setAdapter(offerwallAdapter);

        switch (surveyLayout) {
            case 0:
                binding.recyclerViewSurvey.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                break;
            case 1:
                binding.recyclerViewSurvey.setLayoutManager(new GridLayoutManager(activity, 3));
                break;
            case 2:
                binding.recyclerViewSurvey.setLayoutManager(new LinearLayoutManager(activity));
                break;

        }
        surveyAdapter = new OfferwallAdapter(surveyResp, getActivity());
        binding.recyclerViewSurvey.setAdapter(surveyAdapter);

    }

    private void getOfferwall() {
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).getOfferwall().enqueue(new Callback<List<OfferwallResp>>() {
            @Override
            public void onResponse(Call<List<OfferwallResp>> call, Response<List<OfferwallResp>> response) {
              
                if(response.isSuccessful() && response.body().size()!=0){
                    for(int i=0; i<response.body().size(); i++){
                        if(response.body().get(i).getOffer_type().equals("offers")){
                            offerwallResps.add(response.body().get(i));
                        }else {
                            surveyResp.add(response.body().get(i));
                        }
                    }
                    if(surveyResp.size()==0){
                        binding.tvTopSurvey.setVisibility(View.GONE);
                        binding.recyclerViewSurvey.setVisibility(View.GONE);
                    }else if(offerwallResps.size()==0){
                        binding.tvTopOffer.setVisibility(View.GONE);
                        binding.recyclerViewOfferwall.setVisibility(View.GONE);
                    }
                    surveyAdapter.notifyDataSetChanged();
                    offerwallAdapter.notifyDataSetChanged();
                }else{
                    binding.tvTopSurvey.setVisibility(View.GONE);
                    binding.tvTopOffer.setVisibility(View.GONE);
                    binding.recyclerViewSurvey.setVisibility(View.GONE);
                    binding.recyclerViewOfferwall.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<OfferwallResp>> call, Throwable t) {

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

    private void dailycheckin() {
        showDialog();
        Objects.requireNonNull(ApiClient.getClient(getActivity())).create(ApiInterface.class).Api(data("", "", "", "", "", "", 3, 0, session.Auth(), 1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    session.setIntData(session.WALLET, response.body().getBalance());
                    showbonus(response.body().getMsg(), "");
                } else {
                    showbonus(response.body().getMsg(), "error");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(requireActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onResume() {
        if (adNetwork!=null && adNetwork.isCompleted()) {
            adNetwork.setCompleted(false);
            dailycheckin();
        }
        super.onResume();
    }

    private void loadReward() {
        adNetwork = new RewardAds.Builder(getActivity());
        adNetwork.buildAd();
    }

    private void slideBanner() {
        SliderView sliderView = binding.imageSlider;
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
        Call<BannerResponse> call = ApiClient.getClient(getActivity()).create(ApiInterface.class).SLideBanner();
        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                if (response.isSuccessful() && response.body().getData().size() != 0) {
                    SliderAdapterExample adapter = new SliderAdapterExample(getActivity(), response.body().getData());
                    sliderView.setSliderAdapter(adapter);
                } else {
                    binding.cvbanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void inAppReview() {
      /*  reviewManager = ReviewManagerFactory.create(getActivity());
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener(task1 -> {

                });
            }
        });*/
    }

    @Override
    public void onClick(View view, int position) {
        TOOLBAR_TITLE=offerItems.get(position).getOfferTitle();
        switch (offerItems.get(position).getType()) {
            case "daily":
                if (adNetwork!=null && adNetwork.isAdLoaded()) {
                    adNetwork.showReward();
                } else {
                    pb.show();
                    loadReward();
                    new Handler().postDelayed(() -> {
                        pb.dismiss();
                        if (adNetwork.isAdLoaded()) {
                            adNetwork.showReward();
                        } else {
                            Toast.makeText(getActivity(), "Try Again No ads Available", Toast.LENGTH_SHORT).show();
                        }
                    }, 5000);
                }

                break;

            case "spin":
                adManager.loadInterstitalAd();
                startActivity(new Intent(getActivity(), SpinActivity.class));
                break;

            case "web":
                adManager.loadInterstitalAd();
                startActivity(new Intent(getActivity(), WeburlActivity.class));
                break;

            case "video":
                adManager.loadInterstitalAd();
                loadFragment(new Video());
                break;

            case "task":
                adManager.loadInterstitalAd();
                loadFragment(new Task());
                break;

            case "reward":
                adManager.loadInterstitalAd();
                startActivity(new Intent(getActivity(), WithdrawActivity.class));
                break;

            case "quiz":
                adManager.loadInterstitalAd();
                startActivity(new Intent(getActivity(), MathQuiz.class));
                break;

            case "scratch":
                adManager.loadInterstitalAd();
                startActivity(new Intent(getActivity(), ScratchActivity.class));
                break;

        }

    }


}
