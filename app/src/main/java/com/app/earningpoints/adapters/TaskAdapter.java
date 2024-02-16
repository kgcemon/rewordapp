package com.app.earningpoints.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.TaskResp;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.WebApi;
import com.app.earningpoints.adsManager.holder.AdmobNativeHolder;
import com.app.earningpoints.adsManager.holder.FacebookNativeHolder;
import com.app.earningpoints.adsManager.holder.StartioHolder;
import com.app.earningpoints.util.Constant_Api;
import com.app.earningpoints.util.Lang;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter {
    Context ctx;
    List<TaskResp> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public TaskAdapter(List<TaskResp> dataItems, Context c) {
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
                View v = inflater.inflate(R.layout.item_video, parent, false);
                viewHolder= new MyViewHolder(v);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.item_weblist, parent, false);
                viewHolder= new MyViewHolder(v1);
                break;

            case 2:
                View v2 = inflater.inflate(R.layout.item_apps, parent, false);
                viewHolder= new AppHolder(v2);
                break;
            case 3:
                View v3 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                viewHolder= new FacebookNativeHolder(v3, ctx,Constant_Api.NATIVE_ID,String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent))),v3.findViewById(R.id.native_ad_container));
                break;

            case 4:
                View v4 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder= new AdmobNativeHolder(v4, ctx, Constant_Api.NATIVE_ID,String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent))),v4.findViewById(R.id.fl_adplaceholder));
                break;
            case 5:
                View v5 = inflater.inflate(R.layout.startapp_native_ad_layout, parent, false);
                viewHolder= new StartioHolder(v5, ctx,String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent))));
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)){
            case 0:
            case 1:
                ((MyViewHolder)holder_parent).bindData(position);
                break;
            case 2:
                ((AppHolder)holder_parent).bindData(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int x = 0;
        if(list.get(position).getViewType()==0){
            if(list.get(position).getVideoId()!=null){
                x= 0;
            }else if(list.get(position).getAppName()!=null){
                x=  2;
            }else if(list.get(position).getVideoId()==null && list.get(position).getAppName()==null){
                x=  1;
            } 
        }
        else {
            x=   list.get(position).getViewType();
        }
        return x;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, point,tvdesc;

        public MyViewHolder(View view) {
            super(view);
            title = itemView.findViewById(R.id.tvName);
            point = itemView.findViewById(R.id.coins);
            tvdesc = itemView.findViewById(R.id.tvdesc);

            itemView.setOnClickListener(v->{
                clickListener.onClick(v,getAdapterPosition());
            });
        }

        public void bindData(int posi){
            viewHolder.setIsRecyclable(false);
            final TaskResp resp = list.get(posi);
            title.setText(resp.getTitle());
            point.setText(" +"+resp.getPoint());

            if(list.get(posi).getVideoId()!=null){
                tvdesc.setText(Lang.watch_video_subtitle);
            }else {
                tvdesc.setText(Lang.read_article_subtitle);

            }


        }
    }

    public class AppHolder extends RecyclerView.ViewHolder {
        TextView title, point,tvdesc;
        RoundedImageView imageView;
        public AppHolder(View v2) {
            super(v2);
            title = itemView.findViewById(R.id.tvName);
            imageView = itemView.findViewById(R.id.image);
            point = itemView.findViewById(R.id.coins);
            tvdesc = itemView.findViewById(R.id.tvdesc);

            itemView.setOnClickListener(v->{
                clickListener.onClick(v, getAdapterPosition());
            });
        }

        public void bindData(int posi){
            viewHolder.setIsRecyclable(false);
            final TaskResp resp = list.get(posi);
            title.setText(resp.getAppName());
            point.setText(" +"+resp.getPoints());
            tvdesc.setText(Html.fromHtml(resp.getDetails()));
            Glide.with(itemView.getContext()).load(WebApi.Api.IMAGES +list.get(posi).getImage()).placeholder(R.drawable.placeholder).into(imageView);
        }
    }

}
