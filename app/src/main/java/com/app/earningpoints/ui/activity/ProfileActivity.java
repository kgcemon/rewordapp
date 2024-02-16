package com.app.earningpoints.ui.activity;

import static com.app.earningpoints.restApi.WebApi.Api.USER_IMAGES;
import static com.app.earningpoints.util.Fun.data2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.databinding.ActivityProfileBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Fun;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding bind;
    Session session;
    Activity activity;
    private AlertDialog bonus_dialog;
    private AlertDialog loading;
    LayoutCollectBonusBinding collectBonusBinding;
    private static final int SELECT_PICTURE = 0;
    private Bitmap bitmap;
    String filepath, Id, url;
    Uri image;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean profileUpload=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity = this;
        session = new Session(activity);
        loading = Fun.loading(activity);

        bind.tvProfile.setText(Lang.profile);
        bind.tvName.setText(Lang.name);
        bind.tvEmail.setText(Lang.email);
        bind.tvPhoneno.setText(Lang.phone_no);
        bind.updateprofile.setText(Lang.update);
        bind.tvUpdatePassword.setText(Lang.update_password);
        bind.tvOldPassword.setText(Lang.old_password);
        bind.oldpass.setText(Lang.enter_pass);
        bind.newpass.setText(Lang.enter_pass);
        bind.tvNewPassword.setText(Lang.new_password);
        bind.tvConfirmPassword.setText(Lang.confirm_password);
        bind.confpass.setText(Lang.confirm_password);
        bind.updatePas.setText(Lang.update_password);

        bind.username.setText(session.getData(session.NAME));
        bind.email.setText(session.getData(session.EMAIL));
        bind.phone.setText(session.getData(session.PHONE));

        if (session.getData(session.ACTYPE).equals("email")) {
            bind.updatePasLyt.setVisibility(View.VISIBLE);
        }

       refreshProfile();

        bind.updateprofile.setOnClickListener(view -> {
            if (!bind.email.getText().toString().trim().matches(emailPattern)) {
                Toast.makeText(activity,Lang.error_invalid_email, Toast.LENGTH_SHORT).show();
            } else if (bind.phone.getText().toString().isEmpty() || bind.phone.getText().toString().trim().equals("")) {
                Toast.makeText(activity,Lang.enter_phone, Toast.LENGTH_SHORT).show();
            } else if (bind.phone.getText().toString().trim().length() < 9) {
                bind.phone.setError(Lang.enter_phone);
                Toast.makeText(activity,Lang.enter_phone, Toast.LENGTH_SHORT).show();
            } else {
                updateUserProfile(bind.email.getText().toString().trim(), bind.phone.getText().toString());
            }
        });

        bind.editProfile.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                Dexter.withContext(this)
                        .withPermissions(Manifest.permission.READ_MEDIA_IMAGES).
                        withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                chooseImage();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                Toast.makeText(activity, "Permission not Granted", Toast.LENGTH_SHORT).show();
                            }
                        }).check();
            } else {
                Dexter.withContext(this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).
                        withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                chooseImage();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                Toast.makeText(activity, "Permission not Granted", Toast.LENGTH_SHORT).show();
                            }
                        }).check();
            }
        });

        bind.updatePas.setOnClickListener(view -> {
            if (bind.oldpass.getText().toString().isEmpty()) {
                bind.oldpass.setError(Lang.fill_required_detail);
            } else if (bind.newpass.getText().toString().isEmpty()) {
                bind.newpass.setError(Lang.fill_required_detail);
            } else if (bind.confpass.getText().toString().isEmpty()) {
                bind.confpass.setError(Lang.fill_required_detail);
            } else if (bind.confpass.getText().toString().equals(bind.newpass.getText().toString().trim())) {
                processUpdate(bind.oldpass.getText().toString().trim(), bind.newpass.getText().toString().trim());
            } else {
                Fun.Error(activity,Lang.password_not_match);
            }
        });

    }

    private void refreshProfile() {
        String profile=session.getData(session.PROFILE);
        if(profile!=null && !profile.equals("")){
            if(profile.startsWith("http")){
                Glide.with(activity).load(profile).error(R.drawable.ic_user).into(bind.profile);
            }else{
                Glide.with(activity).load(USER_IMAGES+profile).error(R.drawable.ic_user).into(bind.profile);
            }
        }
    }

    void updateUserProfile(String email, String phone) {
        showDialog();
        Call<CallbackResp> call;
        if (image == null) {
            call = Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).updateProfile(data2(email, phone));
            profileUpload=false;
        } else {
            File file = new File(filepath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part parts = MultipartBody.Part.createFormData("newimage", file.getName(), requestBody);
            call = Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).updateProfilewithProfile(parts, data2(email, phone));
            profileUpload=true;
        }

        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
               try {
                   if (response.isSuccessful() && response.body().getCode() == 201) {
                       session.setData(session.EMAIL, email);
                       session.setData(session.PHONE, phone);
                       if(!response.body().getData().equals("") && profileUpload){
                           session.setData(session.PROFILE,response.body().getData());
                           profileUpload=false;
                           refreshProfile();
                       }

                       showMessage(true,response.body().getMsg());
                   } else {
                       showMessage(false,response.body().getMsg());
                   }
               }catch (Exception e){}
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            image = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(image);
                bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap.createScaledBitmap(bitmap, 350, 600, false);
                filepath = getRealPathFromURI(image);
                bind.profile.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void processUpdate(String oldpas, String newpass) {
        showDialog();
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).updateProfilePass(Fun.data1(oldpas, newpass)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getCode() == 201) {
                    showMessage(true, response.body().getMsg());
                } else {
                    showMessage(false, response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
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

    void showMessage(boolean success, String msg) {
        collectBonusBinding = LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(collectBonusBinding.getRoot()).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);
        bonus_dialog.show();

        collectBonusBinding.effect.setVisibility(View.GONE);
        collectBonusBinding.txt.setVisibility(View.GONE);

        if (success) {
            collectBonusBinding.successAnim.setImageAssetsFolder("raw/");
            collectBonusBinding.successAnim.setAnimation(R.raw.success);
            collectBonusBinding.successAnim.setSpeed(1f);
            collectBonusBinding.successAnim.playAnimation();
            collectBonusBinding.congrts.setText(msg);
        } else {

            collectBonusBinding.successAnim.setImageAssetsFolder("raw/");
            collectBonusBinding.successAnim.setAnimation(R.raw.warning);
            collectBonusBinding.successAnim.setSpeed(1f);
            collectBonusBinding.successAnim.playAnimation();
            collectBonusBinding.congrts.setText(msg);
        }

        collectBonusBinding.closebtn.setOnClickListener(view -> {
            bonus_dialog.dismiss();
        });
    }

}