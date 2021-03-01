package com.example.auto3.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.auto3.ActiveRoute;
import com.example.auto3.R;
import com.example.auto3.ui.DBManager;
import com.example.auto3.ui.MyRoutesAdapter;
import com.example.auto3.ui.Route;
import com.example.auto3.ui.Station;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private MyRoutesAdapter adapter;
    private ArrayList<Route> routeItems;
    public DBManager dbm;
    private ProgressBar pbHorizontal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        dbm = new DBManager(getContext());
        routeItems = dbm.getRoutes();
        initRecyclerView(root);
        return root;
    }

    public void initRecyclerView(View root) {

        recyclerView = (RecyclerView) root.findViewById(R.id.rvRoutes);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyRoutesAdapter(routeItems, getContext());
        adapter.setOnItemClickListener(new MyRoutesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Route route = routeItems.get(position);

                if (route.isDown == 0) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:

                                    ArrayList<Station> stations = dbm.getStations(route.getId());
                                    DownloadAsync download = new DownloadAsync(view, stations, route);
                                    download.execute();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast toast2 = Toast.makeText(getContext(),
                                            "не скачать файлы", Toast.LENGTH_SHORT);
                                    toast2.show();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + which);
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Для продолжения необходимо скачать данные для маршрута").setPositiveButton("Скачать", dialogClickListener)
                            .setNegativeButton("Отмена", dialogClickListener).show();

                } else {

                    Intent intent = new Intent(HomeFragment.this.getActivity(), ActiveRoute.class);
                    intent.putExtra("route", (Route) route);
                    startActivity(intent);
                }

            }
        });
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadAsync extends AsyncTask<Integer, Integer, String> {
        View view;
        ArrayList<Station> stations;
        ImageView imageView;
        RotateAnimation anim;
        Route route;

        public DownloadAsync(View mView, ArrayList<Station> mStations, Route mRoute) {
            this.view = mView;
            imageView = view.findViewById(R.id.DownloadImage);
            anim = new RotateAnimation(0.0f, 360.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            stations = mStations;
            route = mRoute;
        }

        @Override
        protected String doInBackground(Integer... params) {
            for (int i = 0; i < stations.size(); i++) {
                stations.get(i).setSound(route.getNumber(), getContext());
                stations.get(i).setSound_next(route.getNumber(), getContext());
                publishProgress(i);
            }
           // route.setStations(stations);
            return "0";
        }

        @Override
        protected void onPostExecute(String result) {

            imageView.setAnimation(null);
            imageView.setImageResource(R.drawable.ready_foreground);
            route.setDown(getContext());

        }

        @Override
        protected void onPreExecute() {
            imageView.setImageResource(R.drawable.load_foreground);

            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);
            imageView.startAnimation(anim);

            pbHorizontal = (ProgressBar) view.findViewById(R.id.downloadProgress);
            pbHorizontal.setProgress(0);
            pbHorizontal.setVisibility(ProgressBar.VISIBLE);
            pbHorizontal.setMax(stations.size());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pbHorizontal.setProgress(values[0] + 1);
        }
    }
}