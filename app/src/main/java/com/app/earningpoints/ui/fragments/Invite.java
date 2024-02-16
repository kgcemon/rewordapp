package com.app.earningpoints.ui.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.earningpoints.R;
import com.app.earningpoints.databinding.FragmentInviteBinding;
import com.app.earningpoints.ui.activity.ClaimBonus;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.ui.activity.ReferHistoryActivity;
import com.app.earningpoints.util.Constant_Api;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

public class Invite extends Fragment {
    FragmentInviteBinding binding;
    Session session;
    Activity activity;
    AdManager adManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentInviteBinding.inflate(getLayoutInflater());
        session=new Session(getActivity());
        activity =getActivity();
        adManager=new AdManager(activity);
        adManager.loadBannerAd(binding.getRoot());

        binding.toolbar.setText(Lang.invite_friend);
        binding.tvReferearn.setText(Lang.invite_friend_get_bonus);
        binding.tvTxtrefercoin.setText(Lang.refer_desc);
        binding.share.setText(Lang.share);
        binding.claimbonus.setText(Lang.have_a_refer_code);
        binding.referHistory.setText(Lang.refer_history);

        if(session.getIntData(session.from_refer)>0){
            binding.claimbonus.setVisibility(View.GONE);
        }

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                FragmentMain NameofFragment = new FragmentMain();
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.container,NameofFragment);
                transaction.commit();
                return true;
            }
            return false;
        });

        binding.refercode.setText(session.getData(session.REFER_ID));

        binding.copy.setOnClickListener(view -> {
            adManager.loadInterstitalAd();
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link",binding.refercode.getText().toString());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Code copied to clipboard!", Toast.LENGTH_SHORT).show();
        });

        binding.share.setOnClickListener(v -> {
            adManager.loadInterstitalAd();
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(Constant_Api.SHARE_MSG)+
                    "\n" +
                    "Use my referral code "+session.getData(session.REFER_ID)+" on signup.\n" +
                    "Download link: https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        });

        binding.txtinvite.setOnClickListener(view -> {
            adManager.loadInterstitalAd();
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(Constant_Api.SHARE_MSG)+
                    "\n" +
                    "Use my referral code "+session.getData(session.REFER_ID)+" on signup.\n" +
                    "Download link: https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        });

        binding.claimbonus.setOnClickListener(v -> {
            startActivity(new Intent(activity, ClaimBonus.class));
        });

        binding.referHistory.setOnClickListener(view -> {
            startActivity(new Intent(activity, ReferHistoryActivity.class));
        });


        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
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

}
