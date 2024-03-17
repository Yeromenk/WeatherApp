package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastViewHolder>{
    private final List<ForecastItem> data;
    private final LayoutInflater layoutInflater;
    private final ForecastRecyclerViewAdapter.OnItemClickListener listener;

    public ForecastRecyclerViewAdapter(Context context, List<ForecastItem> data,  ForecastRecyclerViewAdapter.OnItemClickListener listener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = layoutInflater.inflate(R.layout.forecast_row, parent, false);
        return new ForecastViewHolder(rowItem);
    }


    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        ForecastItem forecastItem = data.get(position);
        String temperature = forecastItem.getTemp() + "°C; " + forecastItem.getDescription();

        holder.getTemp().setText(temperature);
        holder.getTime().setText(String.valueOf(forecastItem.getTime()));
        holder.getIcon().setImageResource(forecastItem.getIcon());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        holder.bind(data.get(position), listener);
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void click(ForecastItem item);
    }
}
