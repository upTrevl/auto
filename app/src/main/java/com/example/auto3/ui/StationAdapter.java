package com.example.auto3.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.auto3.R;

import java.text.NumberFormat;
import java.util.List;

import static android.content.ContentValues.TAG;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {

    public List<Station> listStations;
    public Context mContext;
    NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public StationAdapter(List<Station> listStations, Context context) {
        this.listStations = listStations;
        this.mContext = context;

        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setGroupingUsed(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Station station = listStations.get(position);
        holder.title.setText(station.getName());
        holder.distance.setText(numberFormat.format(station.getDistance()) + " м");
        if (station.getDistance() < 30) {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.text));
            holder.itemView.setBackgroundResource(R.drawable.rv_station_list_item_white);
            holder.itemView.setElevation(2);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("play : " + station.audio );
                MediaPlayer song = MediaPlayer.create(mContext, Uri.parse(station.getSound().getAbsolutePath()));
                song.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listStations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView distance;
        public ImageView play;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.stationName);
            distance = (TextView) itemView.findViewById(R.id.stationDistance);
            play = (ImageView) itemView.findViewById(R.id.imagePlay);
        }
    }
}
