package com.app.earningpoints.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.databinding.ActivityClaimBonusBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimBonus extends AppCompatActivity {
    ActivityClaimBonusBinding bind;
    Activity activity;
    Session session;
    private AlertDialog loading,alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityClaimBonusBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        activity=this;
        session=new Session(activity);
        loading = Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        bind.toolbar.setText(Lang.claim_refer);
        bind.tvclaimRefer.setText(Lang.claim_refer);
        bind.tvReferralIdIfAvailable.setText(Lang.refer_id_if_available);
        bind.claimbonus.setText(Lang.apply);
        bind.refer.setHint(Lang.refer_code);

        bind.claimbonus.setOnClickListener(v -> {
            claimBonus(bind.refer.getText().toString());
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void claimBonus(String refer) {
        showDialog();
        ApiClient.getClient(activity).create(ApiInterface.class).ApiUser(Fun.data(refer,"","","","","",20,0,session.Auth(),0)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.setIntData(session.WALLET,response.body().getBalance());
                    session.setIntData(session.from_refer,1);
                    showAlert(response.body().getMsg());
                }else{
                    showAlert(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
                Toast.makeText(activity, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(Lang.okay);
        btn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
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