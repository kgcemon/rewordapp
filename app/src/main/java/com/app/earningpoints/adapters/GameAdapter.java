package com.app.earningpoints.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.GameResponse;
import com.app.earningpoints.listener.OnItemClickListener;
import com.app.earningpoints.restApi.WebApi;
import com.bumptech.glide.Glide;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<GameResponse.DataItem> dataItems;
    Context contx;
    OnItemClickListener clickListener;

    public GameAdapter(Context ctx, List<GameResponse.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getTitle());
        holder.desc.setText(dataItems.get(position).getDescription());
        holder.coin.setText(dataItems.get(position).getCoin());
        holder.time.setText(dataItems.get(position).getTime()+" min");
        Glide.with(holder.itemView.getContext()).load(WebApi.Api.IMAGES +dataItems.get(position).getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,desc,time,coin;
        ImageView imageView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            imageView = itemView.findViewById(R.id.image);
            desc = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            coin = itemView.findViewById(R.id.coin);

            itemView.setOnClickListener(v -> {
                clickListener.onClick(v,getAdapterPosition());
            });
        }
    }

}
