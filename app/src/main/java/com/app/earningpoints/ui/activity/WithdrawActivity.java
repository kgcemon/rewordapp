package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Fun.isConnected;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.RedeemResponse;
import com.app.earningpoints.adapters.RedeemAdapter;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.ActivityWithdrawBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.databinding.RedeemdialogBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.restApi.WebApi;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity implements OnItemClickListener{
    ActivityWithdrawBinding binding;
    Activity activity;
    List<RedeemResponse.DataItem> list;
    Session session;
    private AlertDialog bonus_dialog,alertDialog;
    RedeemAdapter adapter;
    AdManager adManager;
    RewardAds.Builder adNetwork;
    BottomSheetDialog bottomSheetDialog;
    RedeemdialogBinding redeemBind;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=WithdrawActivity.this;
        alertDialog= Fun.Alert(activity);
        adManager=new AdManager(activity);
        adManager.loadBannerAd(binding.BANNER);
        loadReward();

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        Objects.requireNonNull(bonus_dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        session=new Session(this);
        binding.toolbar.setText(TOOLBAR_TITLE);

        list= new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RedeemAdapter(WithdrawActivity.this,list);
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);

        if (isConnected(this)){
            getdata();
        }else {
            showAlert(Lang.no_internet);
        }

        binding.back.setOnClickListener(v->{
            onBackPressed();
        });

    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        assert tv != null;
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(Lang.okay);
        btn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
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

    private void getdata(){
        Call<RedeemResponse> call = ApiClient.getClient(this).create(ApiInterface.class).getRedeem();
        call.enqueue(new Callback<RedeemResponse>() {
            @Override
            public void onResponse(Call<RedeemResponse> call, Response<RedeemResponse> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    list.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }else {
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.noResult.lyt.setVisibility(View.VISIBLE);
                    binding.noResult.tvNoResultFound.setText(Lang.no_result_found);
                }
            }

            @Override
            public void onFailure(Call<RedeemResponse> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.lyt.setVisibility(View.VISIBLE);
                binding.noResult.tvNoResultFound.setText(Lang.no_result_found);
            }
        });
    }

    private void loadReward() {
        adNetwork= new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view, int position) {
        prepareReward(position);
    }

    private void prepareReward(int pos) {
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        redeemBind= RedeemdialogBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(redeemBind.getRoot());
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bottomSheetDialog.show();

        Glide.with(this).load(WebApi.Api.IMAGES  + list.get(pos).getImage()).placeholder(R.drawable.placeholder).into(redeemBind.imageView);

        redeemBind.name.setText(Lang.enter+" "+list.get(pos).getTitle()+" "+Lang.detail);
        redeemBind.email.setHint(Lang.enter+list.get(pos).getTitle()+Lang.detail);

        redeemBind.send.setOnClickListener(v->{
            if(redeemBind.email.getText().toString().isEmpty()){
                showAlert(Lang.fill_required_detail);
            }else{
                ApiClient.getClient(WithdrawActivity.this).create(ApiInterface.class).Api(Fun.data(
                        list.get(pos).getTitle(),
                        list.get(pos).getPoints(),
                        list.get(pos).getPointvalue(),
                        list.get(pos).getDescription(),
                        redeemBind.email.getText().toString().trim(),"",2,2,session.Auth(),0)).enqueue(new Callback<CallbackResp>() {
                    @Override
                    public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                        bottomSheetDialog.dismiss();
                        adNetwork.showReward();
                        if(response.isSuccessful() && response.body().getCode()==201){
                            session.setIntData(session.WALLET,response.body().getBalance());
                            showbonus(response.body().getMsg(),"");
                        }else {
                            showbonus(response.body().getMsg(),"error");
                        }
                    }

                    @Override
                    public void onFailure(Call<CallbackResp> call, Throwable t) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(activity, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
