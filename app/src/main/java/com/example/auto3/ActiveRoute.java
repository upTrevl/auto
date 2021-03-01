package com.example.auto3;

import android.Manifest;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.auto3.ui.DBManager;
import com.example.auto3.ui.MyRoutesAdapter;
import com.example.auto3.ui.Route;
import com.example.auto3.ui.Station;
import com.example.auto3.ui.StationAdapter;
import com.example.auto3.ui.home.HomeFragment;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ActiveRoute extends AppCompatActivity implements LocationListener {
    private static final String TAG = "Active";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public Route route;
    public ArrayList<Station> listStations = new ArrayList<>();
    public RecyclerView recyclerView;
    public StationAdapter adapter;
    public Station CloseStation;
    public Station next_Station;
    public float distanse;
    public LocationManager locationManager;
    float minDistanse = 5000;
    public Location oldLocation = new Location("");
    public boolean isAdapterStart = false;
    public int idItem;
    public ProgressBar routeprogress;
    int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_route);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        routeprogress = findViewById(R.id.routeProgress);
        checkGPS();
        routeInit();

        oldLocation.setLongitude(0d);
        oldLocation.setLatitude(0d);

    }

    public void routeInit() {
        DBManager db = new DBManager(this);
        route = (Route) getIntent().getSerializableExtra("route");
        listStations = db.getStations(route.getId());
        routeprogress.setMax(listStations.size());
        for (int i = 0; i < listStations.size(); i++) {
            listStations.get(i).setSound_file(route.getNumber(), this);
            listStations.get(i).setSound_next_file(route.getNumber(), this);
        }

        route.setStations(listStations);
    }

    public void startStationAdapter() {
        adapter = new StationAdapter(route.getStations(), this);
        recyclerView = (RecyclerView) findViewById(R.id.rv_stations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void updateStationAdapter(int pos) {
        int offset = 80;
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(pos, offset);
        adapter.notifyDataSetChanged();
    }

    public void checkGPS() {
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_EXTERNAL_STORAGE);
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Для работы приложения необходимо включить GPS").setPositiveButton("Включить", dialogClickListener)
                    .setNegativeButton("Отклонить", dialogClickListener).show();

        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Растояние от предыдущей точки 10 метров необходимо для того что бы проверка произходила не каждую секунду
        if (oldLocation.distanceTo(location) > 10) {
            // Дистанция от текущей позиции до первой в спике остановки,
            minDistanse = location.distanceTo(listStations.get(0).getLocation());
            // Цикл для проверки ближайшей остановки
            for (int i = 0; i < listStations.size(); i++) {
                // Растояние до остановки
                distanse = location.distanceTo(listStations.get(i).getLocation());
                // Записываем растояние до остановки в обьект
                listStations.get(i).setDistance(distanse);
                // находим ближайшую остановку
                if (distanse < minDistanse) {
                    minDistanse = distanse;
                    CloseStation = listStations.get(i);
                    for (int j = 0; j < listStations.size(); j++) {
                        if (listStations.get(i).next_id == listStations.get(j).id) {
                            next_Station = listStations.get(j);
                            break;
                        }
                    }
                    idItem = i;
                    // если расстояние до остановки меньше 20 метров и скорость меньше 5 км в час воспроизводим.
                    if (distanse < 20 && location.getSpeed() < 5) {
                        progress = i;
                        MediaPlayer song = MediaPlayer.create(this, Uri.parse(CloseStation.getSound().getAbsolutePath()));
                        song.start();
                        song.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer play) {

                                play = MediaPlayer.create(getBaseContext(), Uri.parse(next_Station.getSound_next().getAbsolutePath()));
                                play.start();
                            }
                        });
                    }
                }
                routeprogress.setProgress(progress);
            }
            oldLocation = location;
        }
        if (!isAdapterStart) {
            startStationAdapter();
            isAdapterStart = true;
        } else {
            updateStationAdapter(idItem);
        }
    }


    public void openInMap(View view) {
        Intent intent = new Intent(this, Map.class);
        //intent.putExtra("route", (Route)  this.route );
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}