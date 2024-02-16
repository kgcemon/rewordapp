package com.app.earningpoints.ui.fragments;


import static com.app.earningpoints.restApi.WebApi.Api.USER_IMAGES;
import static com.app.earningpoints.util.Fun.coolNumberFormat;
import static com.app.earningpoints.util.Fun.data;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.adapters.ViewpagerAdapter;
import com.app.earningpoints.databinding.FragmentMainBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.ui.activity.Splash;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMain extends Fragment {
    FragmentMainBinding binding;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Session session;
    boolean doubleBackToExitPressedOnce = false;
    BottomSheetDialog bottomSheetDialog;
    private AlertDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMainBinding.inflate(getLayoutInflater(),container,false);

        session=new Session(getActivity());
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        loading = Fun.loading(getActivity());


        binding.username.setText(session.getData(session.NAME));
        binding.tvWelcome.setText(Lang.welcome);

        String profile=session.getData(session.PROFILE);
        try {
            if(profile!=null && !profile.equals("")){
                if(profile.startsWith("http")){
                    Glide.with(requireActivity()).load(profile).error(R.drawable.ic_user).into(binding.icon);
                }else{
                    Glide.with(requireActivity()).load(USER_IMAGES+profile).error(R.drawable.ic_user).into(binding.icon);
                }
            }
        }catch (Exception ignored){}

        viewPager = binding.catviewpager;
        tabLayout= binding.tablayout;
        catadapter= new ViewpagerAdapter(getChildFragmentManager());
        catadapter.AddFragment(new HomeNew(),"test");
        catadapter.AddFragment(new Games(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(Lang.top_picks);
        tabLayout.getTabAt(1).setText(Lang.play_zone);


        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if(viewPager.getCurrentItem()>0){
                    viewPager.setCurrentItem(0);
                }else if(viewPager.getCurrentItem()==0) {
                    if(doubleBackToExitPressedOnce){
                        if(session.getBoolean("exit")){
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }else {
                            RatingDialogFragment dialog = RatingDialogFragment.newInstance();
                            dialog.show(getActivity().getSupportFragmentManager(), "rating_dialog");
                        }
                    }else {
                        doubleBackToExitPressedOnce=true;
                    }
                }
            }
            return false;
        });

        binding.refresh.setOnClickListener(v -> {
            binding.refresh.setEnabled(false);
            Glide.with(getActivity()).asGif().load(R.drawable.loading).into(binding.refresh);
            reload_coin();
        });

        binding.layoutCoin.setOnClickListener(v -> {
           loadFragment(new Coins());
        });

        return binding.getRoot();
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        binding.coins.setText(coolNumberFormat(Long.parseLong(String.valueOf(session.getIntData(session.WALLET)))));
        super.onResume();
    }


    private void reload_coin() {
        ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiUser(data("","","","","","",15,0,session.Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful()){
                    try {
                        binding.coins.setText(coolNumberFormat(Long.parseLong(String.valueOf(response.body().getBalance()))));
                        session.setIntData(session.WALLET,response.body().getBalance());
                        Toast.makeText(getActivity(), "Coin Updated", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> binding.refresh.setEnabled(true),5000);
                        binding.refresh.setImageResource(R.drawable.ic_baseline_autorenew_24);

                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {

            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        session.setData(session.SELECTED_LANGUAGE,lang);
        startActivity(new Intent(getActivity(), Splash.class));
    }

    private void showDialog() {
        loading.show();
    }

    private void dismissDialog() {
        if (loading.isShowing()) {
            loading.dismiss();
        }
    }

}