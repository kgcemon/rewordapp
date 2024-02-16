package com.app.earningpoints.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.app.earningpoints.Responsemodel.RefListResp;
import com.app.earningpoints.adapters.ReferHistorAdapter;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.earningpoints.databinding.ActivityReferHistoryBinding;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferHistoryActivity extends AppCompatActivity {
    private ActivityReferHistoryBinding bind;
    private Activity activity;
    ReferHistorAdapter adapter;
    List<RefListResp.DataItem> refListResps;
    AdManager adManager;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityReferHistoryBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        activity=this;

        session=new Session(activity);
        adManager=new AdManager(this);
        adManager.loadBannerAd(bind.BANNER);

        bind.toolbar.setText(Lang.refer_history);
        bind.tvAllJoined.setText(Lang.all_joined);
        bind.tvTodayJoined.setText(Lang.today_joined);

        refListResps=new ArrayList<>();
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new ReferHistorAdapter(activity,refListResps);
        bind.recyclerView.setAdapter(adapter);

        getReferData();

        bind.back.setOnClickListener(view -> {
            onBackPressed();
        });


    }

    private void getReferData() {
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).getReferList(session.getData(session.REFER_ID)).enqueue(new Callback<RefListResp>() {
            @Override
            public void onResponse(Call<RefListResp> call, Response<RefListResp> response) {
                bind.shimmerViewContainer.setVisibility(View.GONE);
                bind.cv.setVisibility(View.VISIBLE);
                if(response.isSuccessful() && Objects.requireNonNull(response.body()).getSuccess()==1){
                    bind.shimmerViewContainer.setVisibility(View.GONE);
                    bind.todayCount.setText(response.body().getToday());
                    bind.totalCount.setText(response.body().getTotal());
                    bind.recyclerView.setVisibility(View.VISIBLE);
                    refListResps.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }else{
                    bind.noResult.lyt.setVisibility(View.VISIBLE);
                    bind.noResult.tvNoResultFound.setText(Lang.no_result_found);
                }
            }

            @Override
            public void onFailure(Call<RefListResp> call, Throwable t) {
                bind.shimmerViewContainer.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onBackPressed() {
        adManager.loadInterstitalAd();
        super.onBackPressed();
    }
}