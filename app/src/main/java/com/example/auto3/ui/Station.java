package com.example.auto3.ui;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;

public class Station implements Serializable {

    /**
     * Широта
     */
    public Double longitude;
    /**
     * Долгота
     */
    public Double latitude;
    public String name;
    public String audio;
    public String audio_next;
    public File sound;
    public File sound_next;
    public Integer id;
    public Integer route_num;
    public Integer next_id;
    public Location location;
    public Float distance;

    public Station(Cursor query) {
        this.id = query.getInt(0);
        this.name = query.getString(1);
        this.latitude = query.getDouble(2);
        this.longitude = query.getDouble(3);
        this.route_num = query.getInt(4);
        this.audio  = query.getString(5);
        this.audio_next  = query.getString(6);
        this.location = new Location("");
        this.location.setLatitude(query.getDouble(2));
        this.location.setLongitude(query.getDouble(3));
        this.next_id = query.getInt(7);
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNext_id() {
        return next_id;
    }

    public void setNext_id(Integer next_id) {
        this.next_id = next_id;
    }

    public Station Next(Context context) {
        DBManager dbManager = new DBManager(context);
        return dbManager.getNextStation(this.next_id);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAudio_next() {
        return audio_next;
    }

    public void setAudio_next(String audio_next) {
        this.audio_next = audio_next;
    }

    public Integer getRoute_num() {
        return route_num;
    }

    public void setRoute_num(Integer route_id) {
        this.route_num = route_id;
    }

    public File getSound() {
        return sound;
    }

    public void setSound(Integer route_num, Context context) {

        FTPManager ftpManager = new FTPManager(context);
        this.sound = ftpManager.download(route_num, this.audio);
    }

    public File getSound_next() {
        return sound_next;
    }

    public void setSound_next(Integer route_num, Context context) {

        FTPManager ftpManager = new FTPManager(context);
        this.sound_next = ftpManager.download(route_num, this.audio_next);

    }
    public void setSound_file(Integer route_num, Context context) {

        this.sound = new File(context.getFilesDir() + "/" + route_num + "/" + audio);
    }
    public void setSound_next_file(Integer route_num, Context context) {

        this.sound_next = new File(context.getFilesDir() + "/" + route_num + "/" + audio_next);

    }

}
