package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.util.Constant_Api.AUTH;
import static com.app.earningpoints.util.Fun.data;
import static com.app.earningpoints.util.Fun.deviceId;
import static com.app.earningpoints.util.Fun.encrypt;
import static com.app.earningpoints.util.Fun.hideKeyboard;
import static com.app.earningpoints.util.Fun.isConnected;
import static com.app.earningpoints.util.Lang.continue_with;
import static com.app.earningpoints.util.Lang.create_account;
import static com.app.earningpoints.util.Lang.enter_email;
import static com.app.earningpoints.util.Lang.enter_pass;
import static com.app.earningpoints.util.Lang.forgot_password;
import static com.app.earningpoints.util.Lang.login;
import static com.app.earningpoints.util.Lang.new_user;
import static com.app.earningpoints.util.Lang.sign_in_to_continue;
import static com.app.earningpoints.util.Lang.welcome_back;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.databinding.ActivityFrontLoginBinding;
import com.app.earningpoints.databinding.DialogForgotPasswordBinding;
import com.app.earningpoints.databinding.LayoutPolicyBinding;
import com.app.earningpoints.databinding.RedeemdialogBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Constant_Api;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontLogin extends AppCompatActivity {
    ActivityFrontLoginBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText etemail,etpassword;
    Activity activity;
    Session session;
    private AlertDialog pass_Reset,loading,alertDialog;
    GoogleSignInClient mGoogleSignInClient;
    DialogForgotPasswordBinding passwordBinding;
    private static final int RC_SIGN_IN = 100;
    BottomSheetDialog bottomSheetDialog;
    LayoutPolicyBinding layoutPolicyBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFrontLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        activity = FrontLogin.this;
        session= new Session(activity);
        loading= Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        binding.titleText.setText(welcome_back);
        binding.msg.setText(sign_in_to_continue);
        binding.email.setHint(enter_email);
        binding.password.setHint(enter_pass);
        binding.forgotPasswordText.setHint(forgot_password);
        binding.loginEmail.setHint(login);
        binding.tvContinueWith.setHint(continue_with);
        binding.tvNewuser.setHint(new_user);
        binding.tvCreateAccount.setHint(create_account);

        preparePolicyDialog();

        if(!TextUtils.isEmpty(session.getData(session.EMAIL)) && !TextUtils.isEmpty(session.getData(session.PASSWORD))){
            binding.email.setText(session.getData(session.EMAIL));
            binding.password.setText(session.getData(session.PASSWORD));
            reqLogin(session.getData(session.EMAIL),session.getData(session.PASSWORD));
        }

        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);

        binding.createAccountText.setOnClickListener(v -> {
            Create();
        });

        binding.loginEmail.setOnClickListener(v -> {
            hideKeyboard(v,activity);
            if(!isConnected(this)){
                Fun.Error(activity,Lang.no_internet);
            }
            if(!validateData()){
                return;
            }else {
                reqLogin(etemail.getText().toString().trim(),etpassword.getText().toString().trim());
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        binding.loginGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            LoginUserGoogle(acct.getDisplayName(), acct.getEmail(), String.valueOf(acct.getPhotoUrl()), acct.getId());

        } catch (ApiException e) {
            Log.w("LOgin Activtiy", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void preparePolicyDialog() {
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        layoutPolicyBinding= LayoutPolicyBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(layoutPolicyBinding.getRoot());
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bottomSheetDialog.show();

        layoutPolicyBinding.agree.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        layoutPolicyBinding.disagree.setOnClickListener(v -> {
            finishAffinity();
        });

        layoutPolicyBinding.visitPrivacyPolicy.setOnClickListener(v -> {
            Fun.launchCustomTabs(activity,Constant_Api.PRIVACY_POLICY);
        });

    }


    private void LoginUserGoogle(String username,String email,String profile,String google) {
        showDialog();
        Call<CallbackResp> login= Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).ApiUser(data(username,email,google,profile,deviceId(this),"",3,0,"",1));
        login.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.saveUserData(google,
                            response.body().getUser().getEmail(),
                            response.body().getUser().getPhone(),
                            response.body().getUser().getProfile(),
                            response.body().getUser().getName(),
                            etpassword.getText().toString().trim(),
                            response.body().getUser().getRefferalId(),
                            response.body().getUser().getFrom_refer(),
                            AUTH+ encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),"")
                            ,
                            response.body().getUser().getType());
                    session.setIntData(session.WALLET,response.body().getUser().getBalance());
                    session.setBoolean(session.LOGIN,true);
                    Intent intent = new Intent(FrontLogin.this, MainActivity.class);
                    FrontLogin.this.startActivity(intent);

                }else {
                    try {
                        showAlert(response.body().getMsg());
                    }catch (Exception e){ e.getMessage();}
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }


    private boolean validateData() {
        boolean valid= true;
        if(TextUtils.isEmpty(etemail.getText().toString().trim())){
            binding.email.setError(Lang.error_invalid_email);
            valid=false;
        }
        else if(!etemail.getText().toString().trim().matches(emailPattern)){
            binding.email.setError(Lang.error_invalid_email);
            valid=false;
        }

        if(TextUtils.isEmpty(etpassword.getText().toString().trim())){
            binding.password.setError(enter_pass);
            valid=false;
        }
            return valid;
    }

    public void Forgot(View view) {
        passwordBinding=DialogForgotPasswordBinding.inflate(getLayoutInflater());
        pass_Reset=new AlertDialog.Builder(activity).setView(passwordBinding.getRoot()).create();
        pass_Reset.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pass_Reset.getWindow().setWindowAnimations(R.style.Dialoganimation);
        pass_Reset.setCancelable(false);
        pass_Reset.show();

        passwordBinding.submit.setText(Lang.verify);
        passwordBinding.tv.setText(Lang.enter_registerd_email);
        passwordBinding.close.setOnClickListener(view1 -> {pass_Reset.dismiss();});
        passwordBinding.submit.setOnClickListener(view1 -> {
            hideKeyboard(view,activity);
            if(TextUtils.isEmpty(passwordBinding.useremail.getText().toString().trim())){
                showAlert(Lang.error_invalid_email);
            }
            else {
                generateOTP(passwordBinding.useremail.getText().toString().trim());
            }
        });
    }

    private void showDialog() {
        loading.show();
    }

    private void dismissDialog() {
        if(loading.isShowing()){
            loading.dismiss();
        }
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

    private void generateOTP(String emails) {
        showDialog();
        Call<CallbackResp> call=ApiClient.getClient(this).create(ApiInterface.class).ResetPass(emails);
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful()){
                  if(response.body().getCode()==201){
                      Toast.makeText(activity,"Otp Sent to Email Please Check",Toast.LENGTH_LONG).show();
                      Intent intent = new Intent(activity,OtpVerification.class);
                      intent.putExtra("email",emails);
                      startActivity(intent);
                  }else{
                      showAlert(response.body().getMsg());
                  }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    public void Create() {
        Intent intent = new Intent(activity, FrontSignup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        overridePendingTransition(R.anim.exit,R.anim.enter);
        startActivity(intent);
    }

    public void Login(View view) {

    }

    private void reqLogin(String email,String password) {
        showDialog();
        Call<CallbackResp> login= ApiClient.getClient(this).create(ApiInterface.class).ApiUser(data("",email,password,"","","",1,0,"",1));
        login.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                        session.saveUserData(response.body().getUser().getGoogle(),
                                response.body().getUser().getEmail(),
                                response.body().getUser().getPhone(),
                                response.body().getUser().getProfile(),

                                response.body().getUser().getName(),
                                etpassword.getText().toString().trim(),
                                response.body().getUser().getRefferalId(),
                                response.body().getUser().getFrom_refer(),
                                AUTH+ encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),"")
                                ,
                                response.body().getUser().getType());
                        session.setData(session.emailVerified,response.body().getUser().getEmailVerified());
                        session.setIntData(session.WALLET,response.body().getUser().getBalance());
                        session.setBoolean(session.LOGIN,true);
                        Toast.makeText(getApplicationContext(), "Welcome "+ response.body().getUser().getName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FrontLogin.this, MainActivity.class);
                        FrontLogin.this.startActivity(intent);

                }else {
                   try {
                       showAlert(response.body().getMsg());
                   }catch (Exception e){ e.getMessage();}
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }


}
