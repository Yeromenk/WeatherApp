package com.example.weatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ForecastViewHolder extends RecyclerView.ViewHolder {
    private final TextView temp;
    private final TextView time;
    private final ImageView icon;
    private final View itemView;

    public ForecastViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.temp = itemView.findViewById(R.id.forecast_temp);
        this.time = itemView.findViewById(R.id.forecast_time);
        this.icon = itemView.findViewById(R.id.forecast_icon);
    }

    public void bind(@NonNull ForecastItem item, ForecastRecyclerViewAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.click(item));
    }

    public TextView getTemp() {
        return temp;
    }

    public TextView getTime() {
        return time;
    }

    public ImageView getIcon() {
        return icon;
    }
}
