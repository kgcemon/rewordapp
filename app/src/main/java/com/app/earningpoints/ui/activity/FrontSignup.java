package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.AUTH;
import static com.app.earningpoints.util.Fun.data;
import static com.app.earningpoints.util.Fun.deviceId;
import static com.app.earningpoints.util.Fun.encrypt;
import static com.app.earningpoints.util.Fun.isConnected;
import static com.app.earningpoints.util.Lang.already_have_an_account_signin_here;
import static com.app.earningpoints.util.Lang.create_account;
import static com.app.earningpoints.util.Lang.enter_email;
import static com.app.earningpoints.util.Lang.enter_pass;
import static com.app.earningpoints.util.Lang.enter_phone;
import static com.app.earningpoints.util.Lang.enter_username;
import static com.app.earningpoints.util.Lang.error_invalid_email;
import static com.app.earningpoints.util.Lang.fill_detail_continue;
import static com.app.earningpoints.util.Lang.no_internet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.databinding.ActivityFrontSignupBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontSignup extends AppCompatActivity {
    ActivityFrontSignupBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Activity activity;
    Session session;
    private AlertDialog dialog,alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityFrontSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session=new Session(this);
        activity=FrontSignup.this;
        dialog= Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        binding.titleText.setText(create_account);
        binding.msg.setText(fill_detail_continue);
        binding.username.setHint(enter_username);
        binding.email.setHint(enter_email);
        binding.phone.setHint(enter_phone);
        binding.passwordText.setHint(enter_pass);
        binding.btnSignup.setHint(create_account);
        binding.tvAlreadyHaveAccount.setHint(already_have_an_account_signin_here);

        binding.createAccountText.setOnClickListener(v -> {
            overridePendingTransition(R.anim.exit,R.anim.enter);
            startActivity(new Intent(activity,FrontLogin.class));
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

    private boolean validateData() {
        boolean valid= true;
        if(TextUtils.isEmpty(binding.username.getText().toString().trim())) {
            binding.username.setError(enter_username);
            valid = false;
        }
        if(TextUtils.isEmpty(binding.email.getText().toString().trim())){
            binding.email.setError(error_invalid_email);
            valid=false;
        }
        else if(!binding.email.getText().toString().trim().matches(emailPattern)){
            binding.email.setError(error_invalid_email);
            valid=false;
        }

        if(TextUtils.isEmpty(binding.phone.getText().toString().trim())) {
            binding.phone.setError(enter_phone);
            valid = false;
        }

        if(TextUtils.isEmpty(binding.passwordText.getText().toString().trim())) {
            binding.passwordText.setError(enter_pass);
            valid = false;
        }
        if(binding.passwordText.getText().toString().trim().length()<5) {
            binding.passwordText.setError("Enter Minimum 5 Digit or word password");
            valid = false;
        }
        return valid;
    }

    public void Signup(View view) {
        if(!isConnected(activity)){
            showAlert(no_internet,0);
        }
        if(!validateData()){
            return;
        }else {
            reqSignup(binding.username.getText().toString().trim(),binding.email.getText().toString().trim(),binding.phone.getText().toString().trim(),binding.passwordText.getText().toString().trim());
        }
    }

    private void reqSignup(String name, String email, String phone , String password) {
        showDialog();
        Call<CallbackResp> call = ApiClient.getClient(this).create(ApiInterface.class).ApiUser(data(name,email,password,phone,deviceId(this),"",0,0,"",1));
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.saveUserData(response.body().getUser().getGoogle(),
                            response.body().getUser().getEmail(),
                            response.body().getUser().getPhone(),
                            response.body().getUser().getProfile(),
                            response.body().getUser().getName(),
                            password,
                            response.body().getUser().getRefferalId(),
                            response.body().getUser().getFrom_refer(),
                            AUTH+ encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),"")
                            ,
                            response.body().getUser().getType());
                    session.setData(session.emailVerified,response.body().getUser().getEmailVerified());
                    session.setIntData(session.WALLET,response.body().getUser().getBalance());
                    session.setBoolean(session.LOGIN,true);
                    Toast.makeText(getApplicationContext(), Lang.welcome+" " + response.body().getUser().getName(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);
                }else{

                    try {
                        binding.msg.setText(response.body().getMsg());
                        binding.msg.setTextColor(Color.parseColor("#ff2200"));
                        showAlert(response.body().getMsg(),0);
                    }catch (Exception e){ e.getMessage();}
                }

            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    public void login(View view) {
        Intent intent = new Intent(activity, FrontLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    void showAlert(String msg,int type ){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(Lang.okay);
        btn.setOnClickListener(v -> {
            if(type==1){
                startActivity(new Intent(activity,FrontLogin.class));
            }else{
                alertDialog.dismiss();
            }
        });

    }

}
