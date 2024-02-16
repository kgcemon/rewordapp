package com.app.earningpoints.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.BannerResponse;
import com.app.earningpoints.restApi.WebApi;
import com.app.earningpoints.ui.activity.SpinActivity;
import com.app.earningpoints.ui.activity.WeburlActivity;
import com.app.earningpoints.ui.fragments.Invite;
import com.app.earningpoints.ui.fragments.Video;
import com.app.earningpoints.util.Constant_Api;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {


    private Context context;
    private List<BannerResponse.DataItem> mSliderItems;
    public SliderAdapterExample(Context context, List<BannerResponse.DataItem> mSliderItems ) {
        this.context = context;
        this.mSliderItems = mSliderItems;
        notifyDataSetChanged();
    }

    public void renewItems(List<BannerResponse.DataItem> sliderItems) {

    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(BannerResponse.DataItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }


    @Override
    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {

        final BannerResponse.DataItem sliderItem = mSliderItems.get(position);

        Glide.with(viewHolder.itemView.getContext())
                .load(WebApi.Api.IMAGES+sliderItem.getBanner())
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(v -> {
            String type = sliderItem.getOnclick();
            if (type.equals(Constant_Api.LINK)) {
                try {
                    String url = sliderItem.getLink();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    viewHolder.itemView.getContext().startActivity(browserIntent);
                } catch (Exception e) {
                }
            } else if (type.equals(Constant_Api.BANNER_SPIN)) {
                Intent start = new Intent(viewHolder.itemView.getContext(), SpinActivity.class);
                viewHolder.itemView.getContext().startActivity(start);
            }else if (type.equals("video")) {
                AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();
                Fragment myFragment = new Video();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, myFragment)
                        .addToBackStack(null).commit();
            }else if (type.equals("web")) {
                Intent start = new Intent(viewHolder.itemView.getContext(), WeburlActivity.class);
                viewHolder.itemView.getContext().startActivity(start);
            }
            else if (type.equals("refer")) {
                AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();
                Fragment myFragment = new Invite();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, myFragment)
                        .addToBackStack(null).commit();
            }

        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterVH extends  SliderAdapterExample.ViewHolder  {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }


}