package com.app.earningpoints.ui.fragments;

import static com.app.earningpoints.util.Constant_Api.AD_ADMOB;
import static com.app.earningpoints.util.Constant_Api.AD_FB;
import static com.app.earningpoints.util.Constant_Api.AD_START_IO;
import static com.app.earningpoints.util.Constant_Api.NATIVE_COUNT;
import static com.app.earningpoints.util.Constant_Api.NATIVE_TYPE;
import static com.app.earningpoints.util.Constant_Api.Pos;
import static com.app.earningpoints.util.Constant_Api.REMOVE;
import static com.app.earningpoints.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.earningpoints.util.Fun.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.TaskResp;
import com.app.earningpoints.adapters.TaskAdapter;
import com.app.earningpoints.databinding.FragmentVideoBinding;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.ui.activity.PlayTimeActivity;
import com.app.earningpoints.ui.activity.YTVideoActivity;
import com.app.earningpoints.adsManager.AdManager;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video extends Fragment implements OnItemClickListener {
    FragmentVideoBinding binding;
    Activity activity;
    Session session;
    List<TaskResp> list;
    TaskAdapter adapter;
    int item = 0;
    AdManager adManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(getLayoutInflater());
        session = new Session(getActivity());
        activity = getActivity();
        adManager = new AdManager(activity);
        adManager.loadBannerAd(binding.BANNER);

        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        binding.tool.toolbar.setText(TOOLBAR_TITLE);

        list = new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TaskAdapter(list, getActivity());
        adapter.setClickListener(this);
        binding.recyclerview.setAdapter(adapter);

        binding.tool.back.setOnClickListener(v -> {
            load_fragment(new FragmentMain());
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                load_fragment(new FragmentMain());
                return true;
            }
            return false;
        });

        if (isConnected(getActivity())) {
            getdata();
        } else {
            Error(getActivity(), Lang.no_internet);
        }

        return binding.getRoot();
    }

    private void load_fragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void getdata() {
       Objects.requireNonNull(ApiClient.getClient(getActivity())).create(ApiInterface.class).ApiTask(data("","","","","","",6,2,session.Auth(),1)).enqueue(new Callback<List<TaskResp>>() {
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

    private void showItem(boolean item) {
        if(item){
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
        }else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);
            binding.noResult.tvNoResultFound.setText(Lang.no_result_found);
        }
    }

    private void displayData(Response<List<TaskResp>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            list.add(response.body().get(i));
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
    public void onResume() {
        if(REMOVE){
            removeItem(Pos);
        }
        super.onResume();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();
        Pos=0;
        REMOVE=false;
        if(list.size()<5){
            list.clear();
            getdata();
        }
    }

    @Override
    public void onClick(View view, int position) {
        Pos=position;

        if(list.get(position).getBrowser_type().equals("0")){
            Intent intent=new Intent(activity, PlayTimeActivity.class);
            intent.putExtra("video_id",list.get(position).getVideoId());
            intent.putExtra("time",list.get(position).getTimer());
            intent.putExtra("point",list.get(position).getPoint());
            intent.putExtra("url",list.get(position).getUrl());
            intent.putExtra("id",list.get(position).getId());
            intent.putExtra("type","video");
            startActivity(intent);
        }else {
            Intent go = new Intent(activity, YTVideoActivity.class);
            go.putExtra("video_id",list.get(position).getVideoId());
            go.putExtra("timer",list.get(position).getTimer());
            go.putExtra("point",list.get(position).getPoint());
            go.putExtra("id",list.get(position).getId());
            startActivity(go);
        }
    }
}