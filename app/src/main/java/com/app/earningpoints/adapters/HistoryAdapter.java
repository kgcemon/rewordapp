package com.app.earningpoints.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.HistoryResp;
import com.app.earningpoints.adsManager.holder.AdmobNativeHolder;
import com.app.earningpoints.adsManager.holder.FacebookNativeHolder;
import com.app.earningpoints.adsManager.holder.StartioHolder;
import com.app.earningpoints.util.Constant_Api;
import com.app.earningpoints.util.Lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class HistoryAdapter extends RecyclerView.Adapter  {
    Context ctx;
    List<HistoryResp> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    String color;


    public HistoryAdapter(List<HistoryResp> dataItems, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.list = dataItems;
        this.ctx = c;
        color=String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent)));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewHolder=null;
        switch (viewType){
            case 0:
                View v = inflater.inflate(R.layout.item_history, parent, false);
                viewHolder= new MyViewHolder(v);
                break;
            case 1:
                View v3 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                viewHolder= new FacebookNativeHolder(v3, ctx,Constant_Api.NATIVE_ID,color,v3.findViewById(R.id.native_ad_container));
                break;

            case 2:
                View v4 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder= new AdmobNativeHolder(v4, ctx, Constant_Api.NATIVE_ID,color,v4.findViewById(R.id.fl_adplaceholder));
                break;
            case 3:
                View v5 = inflater.inflate(R.layout.startapp_native_ad_layout, parent, false);
                viewHolder= new StartioHolder(v5, ctx,color);
                break;
        }

        assert viewHolder != null;
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)){
            case 0:
                ((MyViewHolder)holder_parent).bindData(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return  list.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmt,tvRemarks,tvDate,type;

        public MyViewHolder(View view) {
            super(view);
            tvAmt = itemView.findViewById(R.id.coin);
            tvDate =  itemView.findViewById(R.id.date);
            tvRemarks = itemView.findViewById(R.id.remark);
            type = itemView.findViewById(R.id.type);
        }

        public void bindData(int posi){
            HistoryResp resp=list.get(posi);
            String mtype = null;
            viewHolder.setIsRecyclable(false);
            tvRemarks.setText(resp.getRemarks());
            type.setText(resp.getType());
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date inputDate = null;
            try {
                if(resp.getDate()!=null){
                    inputDate = fmt.parse(resp.getDate());
                    mtype=resp.getType();
                }else {
                    inputDate = fmt.parse(resp.getInsertedAt());
                    mtype=resp.getTranType();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String amount=resp.getAmount();

            String dateString = fmt.format(inputDate);
            tvDate.setText(dateString);

            int colorGreen = Color.parseColor("#2ABF88");
            int colorRed = Color.parseColor("#ff0000");
            int coloryellow = Color.parseColor("#FEB337");

            if(resp.getDate()!=null){
                tvAmt.setText(resp.getStatus());
                if(resp.getStatus().equalsIgnoreCase("Pending")){
                    tvAmt.setTextColor(coloryellow);
                    tvAmt.setText(Lang.proceed);
                }else if(resp.getStatus().equalsIgnoreCase("Reject")){
                    tvAmt.setTextColor(colorRed);
                }else{
                    tvAmt.setText(Lang.completed);
                    tvAmt.setTextColor(colorGreen);

                }


            }else {
                assert mtype != null;
                if(mtype.equals("credit")){
                    tvAmt.setText("+"+amount+"");
                    tvAmt.setTextColor(colorGreen);
                }else if(mtype.equals("debit")){
                    tvAmt.setText("-"+amount+"");
                    tvAmt.setTextColor(colorRed);
                }else {
                    tvAmt.setText("-"+amount+"");
                    tvAmt.setTextColor(colorRed);
                }
            }
        }
    }
}


