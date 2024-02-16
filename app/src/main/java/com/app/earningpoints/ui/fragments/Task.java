package com.app.earningpoints.ui.fragments;

import static com.app.earningpoints.util.Constant_Api.*;
import static com.app.earningpoints.util.Fun.data;
import static com.app.earningpoints.util.Fun.isAppInstalled;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.CallbackResp;
import com.app.earningpoints.Responsemodel.TaskResp;
import com.app.earningpoints.adapters.TaskAdapter;
import com.app.earningpoints.adsManager.RewardAds;
import com.app.earningpoints.databinding.AppdialogBinding;
import com.app.earningpoints.databinding.FragmentHomeBinding;
import com.app.earningpoints.databinding.LayoutCollectBonusBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.restApi.WebApi;
import com.app.earningpoints.adsManager.AdManager;
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

public class Task extends Fragment implements OnItemClickListener{
    FragmentHomeBinding binding;
    List<TaskResp> list;
    AppdialogBinding appbind;
    Boolean isValidInstall = false,rewardMode=false;
    String packageSent = "", id;
    Activity activity;
    Session session;
    private AlertDialog app, loading, alert, bonus;
    TaskAdapter adapter;
    AdManager adManager;
    int item,posi;
    RewardAds.Builder adNetwork;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        init();
        initDialog();
        initrv();
        getData();

        return binding.getRoot();
    }

    private void init() {
        binding.toolbar.setText(TOOLBAR_TITLE);

        session = new Session(getActivity());
        activity = getActivity();
        alert = Fun.Alert(activity);
        loading = Fun.loading(activity);
        adManager = new AdManager(activity);
        adNetwork = new RewardAds.Builder(activity);

        adManager.loadBannerAd(binding.getRoot());
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        loadReward();

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                FragmentMain NameofFragment = new FragmentMain();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, NameofFragment);
                transaction.commit();
                return true;
            }
            return false;
        });

        binding.back.setOnClickListener(v -> {
            load_fragment(new FragmentMain());
        });


    }

    private void initDialog() {
        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        Objects.requireNonNull(bonus.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bonus.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus.setCanceledOnTouchOutside(false);

        appbind = AppdialogBinding.inflate(getLayoutInflater());
        app = new AlertDialog.Builder(activity).setView(appbind.getRoot()).create();
        Objects.requireNonNull(app.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        app.getWindow().setWindowAnimations(R.style.Dialoganimation);
        app.setCanceledOnTouchOutside(false);
    }

    void initrv() {
        list = new ArrayList<>();
        binding.recyclerViewApps.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TaskAdapter(list, getActivity());
        adapter.setClickListener(this);
        binding.recyclerViewApps.setAdapter(adapter);
    }

    private void load_fragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    void showbonus(String msg, String type) {
        bonus.show();

        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(Lang.close);
        if(type.equals("error")){
            layoutCollectBonusBinding.congrts.setText(Lang.oops);
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.red));
        }else {
            layoutCollectBonusBinding.congrts.setText(Lang.congratulations);
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.green));
        }
        layoutCollectBonusBinding.closebtn.setOnClickListener(view -> {bonus.dismiss();});
    }

    void showAlert(String msg) {
        alert.show();
        TextView tv = alert.findViewById(R.id.txt);
        tv.setText(msg);
        Button btn=alert.findViewById(R.id.close);
        btn.setText(Lang.okay);
        btn.setOnClickListener(v -> {
            alert.dismiss();
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

    private void showItem(boolean item) {
        if (item) {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerViewApps.setVisibility(View.VISIBLE);
        } else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);
            binding.noResult.tvNoResultFound.setText(Lang.no_result_found);
        }
    }

    private void installApp(String appId) {
        showDialog();
        ApiClient.getClient(getActivity()).create(ApiInterface.class).Api(data("","","","","","",11,Integer.parseInt(id),session.Auth(),5)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if (response.isSuccessful() && response.body().getCode() != 0) {
                    session.setIntData(session.WALLET,response.body().getBalance());
                    removeItem(posi);
                    showbonus(response.body().getMsg(), "");
                    isValidInstall = false;
                    packageSent = "";
                } else {
                    try {
                        showbonus(response.body().getMsg(), "error");
                    }catch (Exception e){}
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void showDownloadInfo() {
        app.show();
        Glide.with(requireActivity()).load(WebApi.Api.IMAGES + list.get(posi).getImage()).placeholder(R.drawable.placeholder).into(appbind.images);
        appbind.info.setText(Lang.instructions);
        appbind.tvTitle.setText(list.get(posi).getAppName());
        appbind.desc.setText(Html.fromHtml(list.get(posi).getDetails()));

        appbind.startoffer.setOnClickListener(view -> {
            isValidInstall = true;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(posi).getAppurl()));
            startActivity(intent);

        });

        appbind.close.setOnClickListener(v -> {
            app.dismiss();
        });

    }

    @Override
    public void onResume() {
        if (isValidInstall) {
            if (isAppInstalled(getActivity(),list.get(posi).getUrl())) {
                if(app.isShowing()){ app.dismiss();}
                rewardMode=true;
                adNetwork.showReward();
            }else {
                if(app.isShowing()){ app.dismiss();}
                Toast.makeText(activity,Lang.task_not_completed, Toast.LENGTH_SHORT).show();
                adNetwork.showReward();
            }
        }

        if(rewardMode) {
            if (adNetwork.isCompleted()) {
                adNetwork.setCompleted(false);
                adNetwork.buildAd();
                installApp(id);
            }
        }
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

    private void getData() {
        ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiTask(Fun.data("","","","","","",5,0,session.Auth(),2)).enqueue(new Callback<List<TaskResp>>() {
            @Override
            public void onResponse(Call<List<TaskResp>> call, Response<List<TaskResp>> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    showItem(true);
                    displayData(response);
                } else {
                    showItem(false);
                }
            }

            @Override
            public void onFailure(Call<List<TaskResp>> call, Throwable t) {
                showItem(false);
            }
        });
    }

    private void displayData(Response<List<TaskResp>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            if (isAppInstalled(requireActivity(),response.body().get(i).getUrl())) {

            }else{
                list.add(response.body().get(i));
            }
            item++;
            if (item == NATIVE_COUNT) {
                item = 0;
                switch (NATIVE_TYPE) {
                    case AD_FB:
                        list.add(new TaskResp().setViewType(3));
                        break;
                    case AD_ADMOB:
                        list.add(new TaskResp().setViewType(4));
                        break;
                    case AD_START_IO:
                        list.add(new TaskResp().setViewType(5));
                        break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view, int position) {
        id = list.get(position).getId();
        this.posi=position;
        showDownloadInfo();
    }

    private void loadReward() {
        adNetwork= new RewardAds.Builder(activity);
        adNetwork.buildAd();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();

        if(list.size()<5){
            list.clear();
            getData();
        }
    }
}