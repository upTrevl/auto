package com.example.auto3.ui;

import android.content.Context;
import android.database.Cursor;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Route implements Serializable {

    public Integer id;
    public String name;
    public Integer number;
    public Double price;
    public Double size;
    public ArrayList<Station> stations;
    public int isDown;
   // public ArrayList<Station> listStations = new ArrayList<>();

    public Route(Cursor query) {
        this.name = query.getString(0);
        this.number = query.getInt(1);
        this.price = query.getDouble(2);
        this.id = query.getInt(3);
        this.isDown = query.getInt(4);;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Route(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public Integer isDownloaded() {
        return isDown;
    }

    public void setDown(Context context) {
        DBManager db = new DBManager(context);
        db.setDownloaded(this.getId());
        this.isDown = 1;
    }

}


