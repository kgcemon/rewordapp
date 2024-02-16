package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.APP_AUTHOR;
import static com.app.earningpoints.util.Constant_Api.APP_DESCRIPTION;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.SocialResp;
import com.app.earningpoints.adapters.SocialAdapter;
import com.app.earningpoints.databinding.ActivityAboutusBinding;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Lang;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutusActivity extends AppCompatActivity implements OnItemClickListener {
    ActivityAboutusBinding binding;
    Activity activity;
    AdManager adManager;
    SocialAdapter adapter;
    List<SocialResp> socialResps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=AboutusActivity.this;

        adManager=new AdManager(activity);
        adManager.loadBannerAd(binding.BANNER);

        binding.tvConnectWithUs.setText(Lang.connect_with_us);
        binding.toolbar.setText(Lang.about_us);

        socialResps=new ArrayList<>();
        binding.rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new SocialAdapter(activity,socialResps,0);
        adapter.setClickListener(this::onClick);
        binding.rv.setAdapter(adapter);

        binding.desc.setText(Html.fromHtml(APP_DESCRIPTION));
        binding.company.setText(APP_AUTHOR);

        getData();

        binding.back.setOnClickListener(v->{
           onBackPressed();
        });

    }

    private void getData() {
        ApiClient.getClient(activity).create(ApiInterface.class).getSocialLinks().enqueue(new Callback<List<SocialResp>>() {
            @Override
            public void onResponse(Call<List<SocialResp>> call, @NonNull Response<List<SocialResp>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
                    binding.shimmerViewContainer.setVisibility(View.GONE);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void back(View view) {
        onBackPressed();
    }


    @Override
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
}
