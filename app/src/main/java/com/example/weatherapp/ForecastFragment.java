package com.example.weatherapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForecastFragment extends Fragment {
    private static final String TAG = "all";
    private int count = 0;
    private final ForecastRecyclerViewAdapter.OnItemClickListener listener = item -> {
        MainActivity activity = (MainActivity) this.context;
        if (activity.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            activity.updateForecast(item);
        }
    };

    private String city;
    private JSONObject forecast;
    private RequestQueue requestQueue;
    private List<ForecastItem> forecastArrayList;
    private ForecastRecyclerViewAdapter forecastRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private Context context;

    public ForecastFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)   {
            this.city = ((MainActivity) context).getCity();
            if (this.city == null) {
                this.city = getString(R.string.defaultCity);
            }
        } else {
            if (bundle != null) {
                this.city = bundle.getString("arg0", getString(R.string.defaultCity));
            } else {
                this.city = getString(R.string.defaultCity);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        this.requestQueue = Volley.newRequestQueue(this.context);

        recyclerView = view.findViewById(R.id.forecast_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));

        forecastArrayList = new ArrayList<>();
        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(this.context, forecastArrayList, listener);
        recyclerView.setAdapter(forecastRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();

        getWeather();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private void getWeather() {
        String givenCity = city;
        String url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=0693c6ccaf2014d33d4bae8008660720&units=metric", givenCity);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        forecast = response;
                        count = forecast.getInt("cnt");
                        setWeather();
                    } catch (Exception e) {
                        Log.e("Error", "Weather request error");
                        Toast.makeText(getContext(), "Weather request error\"", Toast.LENGTH_SHORT).show();
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
            for (int i = 0; i < count; i++) {
                String icon = "icon_" + forecast.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                String time = forecast.getJSONArray("list").getJSONObject(i).getString("dt_txt");
                String description = forecast.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                String temperature = forecast.getJSONArray("list").getJSONObject(i).getJSONObject("main").getString("temp");
                int resourceId = getResources().getIdentifier(icon, "drawable", this.context.getPackageName());
                ForecastItem tempForecast = new ForecastItem(temperature, time, resourceId, description);
                forecastArrayList.add(tempForecast);
            }
        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }

        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(this.context, forecastArrayList, listener);
        recyclerView.setAdapter(forecastRecyclerViewAdapter);
    }

    public void setCity(String city) {
        this.city = city;

        getWeather();
    }
}