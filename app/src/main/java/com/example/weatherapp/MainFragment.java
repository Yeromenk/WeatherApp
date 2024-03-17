package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment {
    private static final String TAG = "all";

    private Context context;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private EditText cityEditText;
    private JSONObject currentWeather;
    private RequestQueue requestQueue;
    private String city;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public MainFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        temperatureTextView = view.findViewById(R.id.temperature_textView);
        descriptionTextView = view.findViewById(R.id.description_textView);
        iconImageView = view.findViewById(R.id.icon_imageView);
        cityEditText = view.findViewById(R.id.city_editText);
        cityEditText.setText(R.string.defaultCity);
        city = cityEditText.getText().toString();

        Button button = view.findViewById(R.id.ok_button);
        button.setOnClickListener(this::okClick);

        Button location = view.findViewById(R.id.find_location);
        location.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                getCurrentLocationAndWeather();
            }
        });

        return view;
    }

    private void getCurrentLocationAndWeather() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location ->
                    {
                        if (location != null) {
                            fetchWeatherFromLocation(location.getLatitude(), location.getLongitude());
                        }
                    })
                    .addOnFailureListener(getActivity(), e -> Log.e("LocationError", "Error getting location", e));
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private void fetchWeatherFromLocation(double latitude, double longitude) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=0693c6ccaf2014d33d4bae8008660720&units=metric", latitude, longitude);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        currentWeather = response;
                        setWeather();
                    } catch (Exception e) {
                        Log.e("error", "Weather request error");
                        Toast.makeText(getContext(), "Weather request error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("error", "Something went wrong");
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
        );
        req.setTag(TAG);
        requestQueue.add(req);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private void updateWeather() {
        if (this.getCity().isEmpty()) {
            return;
        }

        String givenCity = this.getCity();
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=0693c6ccaf2014d33d4bae8008660720&units=metric", givenCity);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        currentWeather = response;
                        setWeather();
                    } catch (Exception e) {
                        Log.e("error", "Weather request error");
                        Toast.makeText(getContext(), "Weather request error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("error", "Something went wrong");
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
        );

        req.setTag(TAG);
        requestQueue.add(req);
    }

    private void setWeather() {
        try {
            String icon = "icon_" + currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon");
            int resourceId = getResources().getIdentifier(icon, "drawable", context.getPackageName());
            iconImageView.setImageResource(resourceId);

            String main = currentWeather.getJSONArray("weather").getJSONObject(0).getString("main");
            String description = currentWeather.getJSONArray("weather").getJSONObject(0).getString("description");
            String whole_description = main + "\n" + description;
            descriptionTextView.setText(whole_description);

            String currentTemperature = String.format("%s °C", currentWeather.getJSONObject("main").getString("temp"));
            String tempMin = String.format("%s °C", currentWeather.getJSONObject("main").getString("temp_min"));
            String tempMax = String.format("%s °C", currentWeather.getJSONObject("main").getString("temp_max"));
            String temperatures = currentTemperature + "\nMin: " + tempMin + ", Max: " + tempMax;
            temperatureTextView.setText(temperatures);
        } catch (JSONException e) {
            Log.e("error json", e.getMessage());
        }
    }

    public void setIcon(int icon) {
        iconImageView.setImageResource(icon);
    }

    public void setDescription(String description) {
        descriptionTextView.setText(description);
    }

    public void setTemperature(String temperature) {
        temperatureTextView.setText(temperature);
    }

    public void okClick(View view) {
        setCity(this.cityEditText.getText().toString());

        MainActivity activity = (MainActivity) context;
        activity.setCity(this.getCity());

        updateWeather();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;

        updateWeather();
    }
}