package com.app.earningpoints.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.app.earningpoints.R;
import com.app.earningpoints.databinding.ActivityMainBinding;
import com.app.earningpoints.ui.fragments.Coins;
import com.app.earningpoints.ui.fragments.FragmentMain;
import com.app.earningpoints.ui.fragments.Invite;
import com.app.earningpoints.ui.fragments.Leaderboard;
import com.app.earningpoints.ui.fragments.Profile;
import com.app.earningpoints.ui.fragments.RatingDialogFragment;
import com.app.earningpoints.util.Lang;
import com.app.earningpoints.util.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    Session session;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        session = new Session(this);
        session.setBoolean(session.ENABLE_SESSION, true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setTitle(Lang.home);
        bottomNavigationView.getMenu().findItem(R.id.navigation_invite).setTitle(Lang.invite);
        bottomNavigationView.getMenu().findItem(R.id.navigation_coin).setTitle(Lang.history);
        bottomNavigationView.getMenu().findItem(R.id.navigation_leaderboard).setTitle(Lang.leaderboard);
        bottomNavigationView.getMenu().findItem(R.id.navigation_profile).setTitle(Lang.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                default:
                    loadFragment(new FragmentMain());
                case R.id.navigation_home:
                    loadFragment(new FragmentMain());
                    return true;
                case R.id.navigation_invite:
                    loadFragment(new Invite());
                    return true;
                case R.id.navigation_coin:
                    loadFragment(new Coins());
                    return true;
                case R.id.navigation_leaderboard:
                    loadFragment(new Leaderboard());
                    return true;
                case R.id.navigation_profile:
                    loadFragment(new Profile());
                    return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        RatingDialogFragment dialog = RatingDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "rating_dialog");
    }


}
