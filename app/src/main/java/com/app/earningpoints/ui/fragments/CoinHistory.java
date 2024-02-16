package com.app.earningpoints.ui.fragments;

import static com.app.earningpoints.util.Constant_Api.*;
import static com.app.earningpoints.util.Fun.data;
import static com.app.earningpoints.util.Fun.isConnected;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.HistoryResp;
import com.app.earningpoints.adapters.HistoryAdapter;
import com.app.earningpoints.databinding.FragmentCoinHistoryBinding;
import com.app.earningpoints.restApi.ApiClient;
import com.app.earningpoints.restApi.ApiInterface;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.mateware.snacky.Snacky;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinHistory extends Fragment implements Callback<List<HistoryResp>> {
    FragmentCoinHistoryBinding binding;
    Session session;
    HistoryAdapter adapter;
    int item;
    Context context;
    List<HistoryResp> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCoinHistoryBinding.inflate(getLayoutInflater());
        context = getActivity();
        session=new Session(getActivity());

        list=new ArrayList<>();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new HistoryAdapter(list,getActivity());
        binding.recyclerView.setAdapter(adapter);

        if(isConnected(context)){
            ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiTransaction(data("","","","","","",12,0,session.Auth(),1)).enqueue(this);
        }else {
            Snacky.builder()
                    .setActivity(getActivity())
                    .setText(Lang.no_internet)
                    .setDuration(Snacky.LENGTH_INDEFINITE)
                    .setActionText(android.R.string.ok)
                    .warning()
                    .show();
        }

        return binding.getRoot();
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResponse(Call<List<HistoryResp>> call, Response<List<HistoryResp>> response) {
        if(response.isSuccessful() && response.body().size()>0){
            showItem(true);
            displayData(response);
        }else {
            showItem(false);
        }
    }

    @Override
    public void onFailure(Call<List<HistoryResp>> call, Throwable t) {
        showItem(false);
    }

    private void displayData(Response<List<HistoryResp>> response) {
        for (int i = 0; i < Objects.requireNonNull(response.body()).size(); i++) {
            list.add(response.body().get(i));
            item++;
            if (item == NATIVE_COUNT) {
                item = 0;
                switch (NATIVE_TYPE) {
                    case AD_FB:
                        list.add(new HistoryResp().setViewType(1));
                        break;
                    case AD_ADMOB:
                        list.add(new HistoryResp().setViewType(2));
                        break;
                    case AD_START_IO:
                        list.add(new HistoryResp().setViewType(3));
                        break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showItem(boolean item) {
        if(item){
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);
            binding.noResult.tvNoResultFound.setText(Lang.no_result_found);
        }
    }
}