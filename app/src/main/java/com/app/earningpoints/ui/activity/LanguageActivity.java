package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Lang.*;
import static com.app.earningpoints.util.Lang._1;
import static com.app.earningpoints.util.Lang._2;
import static com.app.earningpoints.util.Lang._3;
import static com.app.earningpoints.util.Lang.all_joined;
import static com.app.earningpoints.util.Lang.apply;
import static com.app.earningpoints.util.Lang.are_you_sure;
import static com.app.earningpoints.util.Lang.are_you_sure_you_want_to_logout;
import static com.app.earningpoints.util.Lang.choose_language;
import static com.app.earningpoints.util.Lang.choose_language_subtitle;
import static com.app.earningpoints.util.Lang.claim_refer;
import static com.app.earningpoints.util.Lang.complete_offer;
import static com.app.earningpoints.util.Lang.completed;
import static com.app.earningpoints.util.Lang.continue_with;
import static com.app.earningpoints.util.Lang.copy;
import static com.app.earningpoints.util.Lang.delete_account_message;
import static com.app.earningpoints.util.Lang.delete_account_subtitle;
import static com.app.earningpoints.util.Lang.delete_my_account;
import static com.app.earningpoints.util.Lang.detail;
import static com.app.earningpoints.util.Lang.enter;
import static com.app.earningpoints.util.Lang.enter_answer;
import static com.app.earningpoints.util.Lang.feedback_subtitle;
import static com.app.earningpoints.util.Lang.fill_required_detail;
import static com.app.earningpoints.util.Lang.have_a_refer_code;
import static com.app.earningpoints.util.Lang.invalid_otp;
import static com.app.earningpoints.util.Lang.leaderboard;
import static com.app.earningpoints.util.Lang.login;
import static com.app.earningpoints.util.Lang.logout_accout;
import static com.app.earningpoints.util.Lang.maintenance;
import static com.app.earningpoints.util.Lang.my_coin;
import static com.app.earningpoints.util.Lang.my_profile;
import static com.app.earningpoints.util.Lang.my_profile_subtitle;
import static com.app.earningpoints.util.Lang.new_user;
import static com.app.earningpoints.util.Lang.no_ad_available;
import static com.app.earningpoints.util.Lang.old_password;
import static com.app.earningpoints.util.Lang.password_not_match;
import static com.app.earningpoints.util.Lang.password_update_message;
import static com.app.earningpoints.util.Lang.privacy_policy_subtitle;
import static com.app.earningpoints.util.Lang.refer_code;
import static com.app.earningpoints.util.Lang.refer_history;
import static com.app.earningpoints.util.Lang.reffer_history;
import static com.app.earningpoints.util.Lang.rewards_subtitle;
import static com.app.earningpoints.util.Lang.right_answer;
import static com.app.earningpoints.util.Lang.submit_answer;
import static com.app.earningpoints.util.Lang.support;
import static com.app.earningpoints.util.Lang.task_not_completed;
import static com.app.earningpoints.util.Lang.today_joined;
import static com.app.earningpoints.util.Lang.top_offers;
import static com.app.earningpoints.util.Lang.top_surveys;
import static com.app.earningpoints.util.Lang.tutorial;
import static com.app.earningpoints.util.Lang.update;
import static com.app.earningpoints.util.Lang.update_available;
import static com.app.earningpoints.util.Lang.update_password;
import static com.app.earningpoints.util.Lang.watch_video_subtitle;
import static com.app.earningpoints.util.Lang.welcome;
import static com.app.earningpoints.util.Lang.write_answer;
import static com.app.earningpoints.util.Lang.wrong_answer;
import static com.app.earningpoints.util.Lang.you_ve_won;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.LangDataResp;
import com.app.earningpoints.Responsemodel.SocialResp;
import com.app.earningpoints.adapters.SocialAdapter;
import com.app.earningpoints.databinding.ActivityLanguageBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageActivity extends AppCompatActivity implements OnItemClickListener {

    ActivityLanguageBinding bind;
    Activity activity;
    Session session;
    SocialAdapter adapter;
    List<SocialResp> dataItems=new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity=this;
        session=new Session(activity);

        progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage("Setting up Language");
        progressDialog.setCancelable(false);
        bind.toolbar.setText(Lang.choose_language);

        if(getIntent().getStringExtra("type").equals("start")){

        }else {
            bind.back.setVisibility(View.VISIBLE);
            bind.back.setOnClickListener(v -> {
                onBackPressed();
            });
        }

        bind.recyclerview.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new SocialAdapter(activity,dataItems,0);
        adapter.setClickListener(this::onClick);
        bind.recyclerview.setAdapter(adapter);

        getLanguage();


    }

    private void getLanguage() {
        ApiClient.getClient(activity).create(ApiInterface.class).get_lang().enqueue(new Callback<List<SocialResp>>() {
            @Override
            public void onResponse(Call<List<SocialResp>> call, Response<List<SocialResp>> response) {
                if(response.isSuccessful() && response.body().size()>0){
                    bind.shimmerViewContainer.setVisibility(View.GONE);
                    bind.recyclerview.setVisibility(View.VISIBLE);
                    dataItems.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SocialResp>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        progressDialog.show();
        ApiClient.getClient(activity).create(ApiInterface.class).getLangData(dataItems.get(position).getCode()).enqueue(new Callback<List<LangDataResp>>() {
            @Override
            public void onResponse(Call<List<LangDataResp>> call, Response<List<LangDataResp>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
                    session.setData(session.SELECTED_LANGUAGE,dataItems.get(position).getCode());
                    prepareLang(response.body());
                    progressDialog.dismiss();
                    if(Objects.equals(getIntent().getStringExtra("type"), "start")){
                        startActivity(new Intent(activity, IntroActivity.class));
                    }else {
                        Toast.makeText(activity, "Language Changed Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity, Splash.class));
                    }

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LangDataResp>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareLang(List<LangDataResp> lang) {
        int i=0;
        enter_email = lang.get(i).getTxt_value();
        enter_pass = lang.get(++i).getTxt_value();
        email = lang.get(++i).getTxt_value();
        password = lang.get(++i).getTxt_value();
        create_account = lang.get(++i).getTxt_value();
        forgot_password = lang.get(++i).getTxt_value();
        sign_in_to_continue = lang.get(++i).getTxt_value();
        welcome_back = lang.get(++i).getTxt_value();
        error_invalid_email = lang.get(++i).getTxt_value();
        username = lang.get(++i).getTxt_value();
        phone = lang.get(++i).getTxt_value();
        invite_friend_get_bonus = lang.get(++i).getTxt_value();
        fill_detail_continue = lang.get(++i).getTxt_value();
        home = lang.get(++i).getTxt_value();
        instructions = lang.get(++i).getTxt_value();
        notification = lang.get(++i).getTxt_value();
        history = lang.get(++i).getTxt_value();
        feedback = lang.get(++i).getTxt_value();
        privacy_policy = lang.get(++i).getTxt_value();
        about_us = lang.get(++i).getTxt_value();
        rate_us = lang.get(++i).getTxt_value();
        already_have_an_account_signin_here = lang.get(++i).getTxt_value();
        signup = lang.get(++i).getTxt_value();
        email_address = lang.get(++i).getTxt_value();
        phone_no = lang.get(++i).getTxt_value();
        refer_id_if_available = lang.get(++i).getTxt_value();
        company = lang.get(++i).getTxt_value();
        name = lang.get(++i).getTxt_value();
        version = lang.get(++i).getTxt_value();
        contact_us = lang.get(++i).getTxt_value();
        website = lang.get(++i).getTxt_value();
        current_coin = lang.get(++i).getTxt_value();
        _00000 = lang.get(++i).getTxt_value();
        rewards = lang.get(++i).getTxt_value();
        connect_with_us = lang.get(++i).getTxt_value();
        www_example_com = lang.get(++i).getTxt_value();
        who_we_are = lang.get(++i).getTxt_value();
        more_offers = lang.get(++i).getTxt_value();
        invite = lang.get(++i).getTxt_value();
        confirm = lang.get(++i).getTxt_value();
        coin_history = lang.get(++i).getTxt_value();
        reward_history = lang.get(++i).getTxt_value();
        no_internet = lang.get(++i).getTxt_value();
        enter_username = lang.get(++i).getTxt_value();
        enter_phone = lang.get(++i).getTxt_value();
        ok = lang.get(++i).getTxt_value();
        coin = lang.get(++i).getTxt_value();
        try_again_later = lang.get(++i).getTxt_value();
        logout = lang.get(++i).getTxt_value();
        invite_friend = lang.get(++i).getTxt_value();
        tap_copy = lang.get(++i).getTxt_value();
        your_refer_code = lang.get(++i).getTxt_value();
        profile = lang.get(++i).getTxt_value();
        hello = lang.get(++i).getTxt_value();
        mobile = lang.get(++i).getTxt_value();
        play = lang.get(++i).getTxt_value();
        scratch_again = lang.get(++i).getTxt_value();
        today_remaining_spin = lang.get(++i).getTxt_value();
        today_remaining_scratch = lang.get(++i).getTxt_value();
        today_remaining_quiz = lang.get(++i).getTxt_value();
        skip = lang.get(++i).getTxt_value();
        congratulations = lang.get(++i).getTxt_value();
        share = lang.get(++i).getTxt_value();
        complete_challenge = lang.get(++i).getTxt_value();
        redeem_reward = lang.get(++i).getTxt_value();
        loading = lang.get(++i).getTxt_value();
        please_enter_id = lang.get(++i).getTxt_value();
        submit = lang.get(++i).getTxt_value();
        task_challenge = lang.get(++i).getTxt_value();
        no_result_found = lang.get(++i).getTxt_value();
        refer_desc = lang.get(++i).getTxt_value();
        we_sent_otp_on_email = lang.get(++i).getTxt_value();
        verify = lang.get(++i).getTxt_value();
        otp_verification = lang.get(++i).getTxt_value();
        enter_registerd_email = lang.get(++i).getTxt_value();
        confirm_password = lang.get(++i).getTxt_value();
        create_new_password = lang.get(++i).getTxt_value();
        new_password = lang.get(++i).getTxt_value();
        no_thanks = lang.get(++i).getTxt_value();
        please_wait = lang.get(++i).getTxt_value();
        vpn_not_connected = lang.get(++i).getTxt_value();
        read_article_subtitle = lang.get(++i).getTxt_value();
        proceed = lang.get(++i).getTxt_value();
        get_started = lang.get(++i).getTxt_value();
        next = lang.get(++i).getTxt_value();
        rate_our_app = lang.get(++i).getTxt_value();
        share_experience_with_us = lang.get(++i).getTxt_value();
        okay = lang.get(++i).getTxt_value();
        close = lang.get(++i).getTxt_value();
        oops = lang.get(++i).getTxt_value();
        play_game = lang.get(++i).getTxt_value();
        exit = lang.get(++i).getTxt_value();
        confirm_exit = lang.get(++i).getTxt_value();
        yes = lang.get(++i).getTxt_value();
        no = lang.get(++i).getTxt_value();
        top_picks = lang.get(++i).getTxt_value();
        play_zone = lang.get(++i).getTxt_value();
        history_subtitle = lang.get(++i).getTxt_value();
        contact_us_subtitle = lang.get(++i).getTxt_value();
        rewards_subtitle = lang.get(++i).getTxt_value();
        feedback_subtitle = lang.get(++i).getTxt_value();
        logout_accout = lang.get(++i).getTxt_value();
        privacy_policy_subtitle = lang.get(++i).getTxt_value();
        support = lang.get(++i).getTxt_value();
        my_coin = lang.get(++i).getTxt_value();
        are_you_sure_you_want_to_logout = lang.get(++i).getTxt_value();
        maintenance = lang.get(++i).getTxt_value();
        update = lang.get(++i).getTxt_value();
        update_available = lang.get(++i).getTxt_value();
        refer_code = lang.get(++i).getTxt_value();
        login = lang.get(++i).getTxt_value();
        new_user = lang.get(++i).getTxt_value();
        copy = lang.get(++i).getTxt_value();
        complete_offer = lang.get(++i).getTxt_value();
        task_not_completed = lang.get(++i).getTxt_value();
        choose_language_subtitle = lang.get(++i).getTxt_value();
        choose_language = lang.get(++i).getTxt_value();
        tutorial = lang.get(++i).getTxt_value();
        submit_answer = lang.get(++i).getTxt_value();
        write_answer = lang.get(++i).getTxt_value();
        enter_answer = lang.get(++i).getTxt_value();
        fill_required_detail = lang.get(++i).getTxt_value();
        detail = lang.get(++i).getTxt_value();
        password_update_message = lang.get(++i).getTxt_value();
        password_not_match = lang.get(++i).getTxt_value();
        invalid_otp = lang.get(++i).getTxt_value();
        completed = lang.get(++i).getTxt_value();
        delete_my_account = lang.get(++i).getTxt_value();
        delete_account_subtitle = lang.get(++i).getTxt_value();
        are_you_sure = lang.get(++i).getTxt_value();
        delete_account_message = lang.get(++i).getTxt_value();
        have_a_refer_code = lang.get(++i).getTxt_value();
        you_ve_won = lang.get(++i).getTxt_value();
        apply = lang.get(++i).getTxt_value();
        _1 = lang.get(++i).getTxt_value();
        _2 = lang.get(++i).getTxt_value();
        _3 = lang.get(++i).getTxt_value();
        continue_with = lang.get(++i).getTxt_value();
        leaderboard = lang.get(++i).getTxt_value();
        reffer_history = lang.get(++i).getTxt_value();
        today_joined = lang.get(++i).getTxt_value();
        all_joined = lang.get(++i).getTxt_value();
        my_profile = lang.get(++i).getTxt_value();
        my_profile_subtitle = lang.get(++i).getTxt_value();
        update_password = lang.get(++i).getTxt_value();
        old_password = lang.get(++i).getTxt_value();
        welcome = lang.get(++i).getTxt_value();
        top_surveys = lang.get(++i).getTxt_value();
        top_offers = lang.get(++i).getTxt_value();
        claim_refer = lang.get(++i).getTxt_value();
        right_answer = lang.get(++i).getTxt_value();
        wrong_answer = lang.get(++i).getTxt_value();
        refer_history = lang.get(++i).getTxt_value();
        no_ad_available = lang.get(++i).getTxt_value();
        enter = lang.get(++i).getTxt_value();
        watch_video_subtitle = lang.get(++i).getTxt_value();
        intro_slide_one_title = lang.get(++i).getTxt_value();
        intro_slide_one_description = lang.get(++i).getTxt_value();
        intro_slide_two_title = lang.get(++i).getTxt_value();
        intro_slide_two_description = lang.get(++i).getTxt_value();
        intro_slide_three_title = lang.get(++i).getTxt_value();
        intro_slide_three_description = lang.get(++i).getTxt_value();
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getStringExtra("type").equals("start")){
        }else {
            super.onBackPressed();
        }
    }
}