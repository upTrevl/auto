package com.example.auto3.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.auto3.R;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MyRoutesAdapter extends RecyclerView.Adapter<MyRoutesAdapter.ViewHolder> {
    public List<Route> listItems;
    public Context mContext;
    public OnItemClickListener listener;


    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public MyRoutesAdapter(List<Route> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_routes_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Route route = listItems.get(position);

        holder.txtNumber.setText(route.getNumber().toString());
        holder.txtTitle.setText(route.getName());
        holder.txtStatus.setText("Активно");

        if (route.isDownloaded() == 1) {
            holder.download.setImageResource(R.drawable.ready_foreground);
        }


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTitle;
        public TextView txtNumber;
        public TextView txtStatus;
        private Context context;
        public ImageView download;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNumber = (TextView) itemView.findViewById(R.id.RouteNumber);
            txtTitle = (TextView) itemView.findViewById(R.id.RoutePoints);
            txtStatus = (TextView) itemView.findViewById(R.id.RouteStatus);
            download = (ImageView) itemView.findViewById(R.id.DownloadImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
