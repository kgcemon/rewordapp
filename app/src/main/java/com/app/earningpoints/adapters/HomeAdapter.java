package com.app.earningpoints.adapters;


import static com.app.earningpoints.util.Const.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.ConfigResp;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.WebApi;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter {
    Context ctx;
    List<ConfigResp.OfferItem> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public HomeAdapter(List<ConfigResp.OfferItem> dataItems, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.list = dataItems;
        this.ctx = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         viewHolder=null;
        switch (viewType){
            case 0:
                View v = inflater.inflate(R.layout.item_home1, parent, false);
                viewHolder= new MyViewHolder(v);
                break;

            case 1:
                View v1 = inflater.inflate(R.layout.item_home2, parent, false);
                viewHolder= new MyViewHolder(v1);
                break;

            case 2:
                View v2 = inflater.inflate(R.layout.item_home3, parent, false);
                viewHolder= new MyViewHolder(v2);
                break;

            case 3:
                View v3 = inflater.inflate(R.layout.item_home4, parent, false);
                viewHolder= new MyViewHolder(v3);
                break;

            case 4:
                View v4 = inflater.inflate(R.layout.item_home5, parent, false);
                viewHolder= new MyViewHolder(v4);
                break;

            case 5:
                View v5 = inflater.inflate(R.layout.item_home6, parent, false);
                viewHolder= new MyViewHolder(v5);
                break;

            case 6:
                View v6 = inflater.inflate(R.layout.item_home7, parent, false);
                viewHolder= new MyViewHolder(v6);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                ((MyViewHolder)holder_parent).bindData(position);
                break;


        }
    }

    @Override
    public int getItemViewType(int position) {
        return homeStyle;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RoundedImageView imageView;
        ImageView imageView1;

        public MyViewHolder(View view) {
            super(view);
            if(homeStyle==6) {
                imageView1 = itemView.findViewById(R.id.image);
            }else {
                tvTitle = itemView.findViewById(R.id.tvTitle);
                imageView = itemView.findViewById(R.id.image);
            }

            itemView.setOnClickListener(v->{
                clickListener.onClick(v,getAdapterPosition());
            });
        }

        public void bindData(int posi){
            viewHolder.setIsRecyclable(false);

            if(homeStyle==6) {
                Glide.with(ctx).load(WebApi.Api.IMAGES+list.get(posi).getOffer_icon()).into(imageView1);
            }else {

                tvTitle.setText(list.get(posi).getOfferTitle());
                Glide.with(ctx).load(WebApi.Api.IMAGES+list.get(posi).getOffer_icon()).into(imageView);
            }

        }
    }


}
