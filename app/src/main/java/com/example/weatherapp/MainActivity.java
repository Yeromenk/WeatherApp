package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.city = getString(R.string.defaultCity);

        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            BottomNavigationView bottomNavigationView = this.findViewById(R.id.navigation);
            bottomNavigationView.setOnItemSelectedListener(this::selected);
        }
        if (savedInstanceState == null) {
            startFragments(getResources().getConfiguration());
        }
    }

    private boolean selected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.forecast_item:
            this.startFragment(ForecastFragment.class, city);
            break;
        case R.id.weather_item:
            this.startFragment(MainFragment.class);
            break;
        default:
            break;
        }
        return true;
    }

    public void startFragment(Class<? extends Fragment> fragmentClass) {
        startFragment(R.id.base_fragment, fragmentClass);

    }

    public void startFragment(int id, Class<? extends Fragment> fragmentClass) {
        Fragment fragment = FragmentFactory.newInstance(fragmentClass);
        this.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(id, fragment)
                .commit();

    }

    public void startFragment(Class<? extends Fragment> fragmentClass, String... args) {
        startFragment(R.id.base_fragment, fragmentClass, args);

    }

    public void startFragment(int id, Class<? extends Fragment> fragmentClass, String... args) {
        Fragment fragment = FragmentFactory.newInstance(fragmentClass, args);
        this.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(id, fragment)
                .commit();

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getOrientation() {
        return getResources().getConfiguration().orientation;
    }

    private void startFragments(Configuration configuration) {
        this.startFragment(MainFragment.class);
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.startFragment(R.id.forecast_fragment, ForecastFragment.class, city);
        }
    }

    public void updateForecast(ForecastItem item) {
        FragmentContainerView fragmentContainerView = findViewById(R.id.base_fragment);
        MainFragment fragment = fragmentContainerView.getFragment();

        fragment.setDescription(item.getDescription());
        fragment.setTemperature(item.getTemp());
        fragment.setIcon(item.getIcon());
    }
}