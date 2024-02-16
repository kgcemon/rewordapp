package com.app.earningpoints.ui.fragments;

import static com.app.earningpoints.restApi.WebApi.Api.USER_IMAGES;
import static com.app.earningpoints.util.Constant_Api.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.SocialResp;
import com.app.earningpoints.adapters.SocialAdapter;
import com.app.earningpoints.databinding.FragmentProfileBinding;
import com.app.earningpoints.databinding.LayoutDialogBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.restApi.WebApi;
import com.app.earningpoints.ui.activity.AboutusActivity;
import com.app.earningpoints.ui.activity.BrowseActivity;
import com.app.earningpoints.ui.activity.FrontLogin;
import com.app.earningpoints.ui.activity.LanguageActivity;
import com.app.earningpoints.ui.activity.ProfileActivity;
import com.app.earningpoints.ui.activity.Splash;
import com.app.earningpoints.ui.activity.WithdrawActivity;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends Fragment implements OnItemClickListener {
    FragmentProfileBinding binding;
    Session session;
    Activity activity;
    private AlertDialog dialog_logout,loading;
    SocialAdapter adapter;
    List<SocialResp> socialResps=new ArrayList<>();
    LayoutDialogBinding layoutDialogBinding;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentProfileBinding.inflate(getLayoutInflater());
        session = new Session(getActivity());
        activity=getActivity();
        loading = Fun.loading(getActivity());

        binding.tvMycoin.setText(Lang.my_coin);
        binding.tvMyProfile.setText(Lang.my_profile);
        binding.tvMyProfileSubtitle.setText(Lang.my_profile_subtitle);
        binding.tvHistory.setText(Lang.history);
        binding.tvHistorySubtitle.setText(Lang.history_subtitle);
        binding.tvReward.setText(Lang.rewards);
        binding.tvRewardsSubtitle.setText(Lang.rewards_subtitle);
        binding.tvChooseLanguage.setText(Lang.choose_language);
        binding.tvChooseLanguageSubtitle.setText(Lang.choose_language);
        binding.tvSupport.setText(Lang.support);
        binding.tvSupportSubtitle.setText(Lang.contact_us_subtitle);
        binding.tvPrivacypolicy.setText(Lang.privacy_policy);
        binding.tvPrivacypolicySubtitle.setText(Lang.privacy_policy_subtitle);
        binding.tvAboutUs.setText(Lang.about_us);
        binding.tvAboutUsSubtitle.setText(Lang.who_we_are);
        binding.tvDeleteAccount.setText(Lang.delete_my_account);
        binding.tvDeleteAccountSubtitle.setText(Lang.delete_account_subtitle);
        binding.tvLogout.setText(Lang.logout);
        binding.tvLogoutSubtitle.setText(Lang.logout_accout);

        layoutDialogBinding=LayoutDialogBinding.inflate(getLayoutInflater());
        dialog_logout = new AlertDialog.Builder(activity).setView(layoutDialogBinding.getRoot()).create();
        Objects.requireNonNull(dialog_logout.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog_logout.getWindow().setWindowAnimations(R.style.Dialoganimation);
        dialog_logout.setCanceledOnTouchOutside(false);

        binding.email.setText("   "+session.getData(session.EMAIL));
        binding.username.setText("   "+session.getData(session.NAME));


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

        binding.rv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter=new SocialAdapter(activity,socialResps,1);
        adapter.setClickListener(this);
        binding.rv.setAdapter(adapter);

        getData();

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

        if(session.isNightModeOn()!=null) {
            System.out.println("nightMode_statua "+session.isNightModeOn());
            if (session.isNightModeOn().equalsIgnoreCase("yes")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                binding.uiswitch.playAnimation();
                new Handler().postDelayed(() -> {
                    binding.uiswitch.pauseAnimation();
                },1000);
            } else {

            }
        }

        binding.uiswitch.setOnClickListener(view -> {
            if (session.isNightModeOn() != null) {
                if (session.isNightModeOn().equalsIgnoreCase("yes")) {
                    session.setNightMode("no");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    restart();
                } else {
                    session.setNightMode("yes");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restart();
                }
            } else {
                session.setNightMode("yes");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                restart();
            }
        });

        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutusActivity.class));
        });

        binding.cvContact.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutusActivity.class));
        });

        binding.cvAccount.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        });

        binding.cvChangelang.setOnClickListener(view -> {
            startActivity(new Intent(activity, LanguageActivity.class).putExtra("type","profile"));
        });

        binding.cvHistory.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new Coins());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.cvLogout.setOnClickListener(v -> {
            Logout();
        });

        binding.cvReward.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), WithdrawActivity.class));
        });

        binding.cvPrivacy.setOnClickListener(v -> {
            launchCustomTabs(activity, PRIVACY_POLICY);
        });

        binding.cvDelete.setOnClickListener(v -> {
            Intent intent = new Intent(activity, BrowseActivity.class);
            intent.putExtra("title","");
            intent.putExtra("url", WebApi.Api.delete_account);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    public void onClick(View view, int position) {
        try {
            if(socialResps.get(position).getUrl().contains("@")){
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{socialResps.get(position).getUrl()});
                emailIntent.setData(Uri.parse("mailto:"));
                startActivity(emailIntent);
            }else{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(socialResps.get(position).getUrl()));
                startActivity(browserIntent);
            }
        }catch (Exception e){
            Toast.makeText(activity, "Invalid Url", Toast.LENGTH_SHORT).show();
        }
    }
    void restart() {
        Intent intent = new Intent(activity, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }

    private void getData() {
        ApiClient.getClient(activity).create(ApiInterface.class).getSocialLinks().enqueue(new Callback<List<SocialResp>>() {
            @Override
            public void onResponse(Call<List<SocialResp>> call, @NonNull Response<List<SocialResp>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
                    binding.rv.setVisibility(View.VISIBLE);
                    socialResps.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SocialResp>> call, Throwable t) {

            }
        });
    }

    private void deleteAccount() {
        ApiClient.getClient(activity).create(ApiInterface.class).ApiUser(Fun.data("","","","","","",16,0,session.Auth(),2)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful() && response.body().getCode()==201){
                    dismissDialog();
                    session.Logout();
                    session.setBoolean(session.LOGIN,false);
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), FrontLogin.class));
                }else {
                    dismissDialog();
                    Toast.makeText(activity, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void Logout() {
        dialog_logout.show();
        TextView title = dialog_logout.findViewById(R.id.congrts);
        TextView msg = dialog_logout.findViewById(R.id.txt);

        title.setText(Lang.logout);
        msg.setText(Lang.are_you_sure_you_want_to_logout);
        title.setTextColor(getResources().getColor(R.color.colorAccent));
        msg.setTextColor(getResources().getColor(R.color.colorAccent));

        dialog_logout.findViewById(R.id.yes).setOnClickListener(v -> {
            dialog_logout.dismiss();
            session.Logout();
            session.setBoolean(session.LOGIN,false);
            startActivity(new Intent(getActivity(), FrontLogin.class));
            getActivity().finish();
        });

        dialog_logout.findViewById(R.id.no).setOnClickListener(v -> {
            dialog_logout.dismiss();
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        binding.coin.setText(""+session.getIntData(session.WALLET));
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

    public static void launchCustomTabs(Activity activity, String url) {
        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
        customIntent.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        customIntent.setExitAnimations(activity, R.anim.exit, R.anim.enter);
        customIntent.setStartAnimations(activity, R.anim.enter, R.anim.exit);
        customIntent.setUrlBarHidingEnabled(true);
        customIntent.build().launchUrl(activity, Uri.parse(url));

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
