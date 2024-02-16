package com.app.earningpoints.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.IntroModel;

import java.util.List;

public class IntroAdapter extends PagerAdapter {

    private Context context;
    private List<IntroModel> introModels;

    public IntroAdapter(Context context, List<IntroModel> introModels) {
        this.context = context;
        this.introModels = introModels;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.intro_items, null);

        LottieAnimationView lottieAnimationView = layoutScreen.findViewById(R.id.Image);
        TextView headLines = layoutScreen.findViewById(R.id.Text);
        TextView descriptions = layoutScreen.findViewById(R.id.Text2);
        headLines.setText(introModels.get(position).getHeadLine());
        descriptions.setText(introModels.get(position).getDescription());
        lottieAnimationView.setImageAssetsFolder("raw/");
        lottieAnimationView.setAnimation(introModels.get(position).getImage());        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return introModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
