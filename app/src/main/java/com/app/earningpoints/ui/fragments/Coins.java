package com.app.earningpoints.ui.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.app.earningpoints.R;
import com.app.earningpoints.adapters.ViewpagerAdapter;
import com.app.earningpoints.databinding.FragmentCoinsBinding;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.Lang;
import com.google.android.material.tabs.TabLayout;

public class Coins extends Fragment {
    FragmentCoinsBinding binding;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    AdManager adManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCoinsBinding.inflate(getLayoutInflater());
        binding.toolbar.setText(Lang.history);
        adManager=new AdManager(getActivity());
        adManager.loadBannerAd(binding.getRoot());

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                goback();
                return true;
            }
            return false;
        });

        binding.back.setOnClickListener(v -> {
            goback();
        });

        viewPager = binding.catviewpager;
        tabLayout= binding.tablayout;
        catadapter= new ViewpagerAdapter(getChildFragmentManager());
        catadapter.AddFragment(new CoinHistory(),"test");
        catadapter.AddFragment(new RewardHistory(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(Lang.coin_history);
        tabLayout.getTabAt(1).setText(Lang.reward_history);

        return binding.getRoot();
    }

    private void goback() {
        FragmentMain NameofFragment = new FragmentMain();
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.container,NameofFragment);
        transaction.commit();
    }

}