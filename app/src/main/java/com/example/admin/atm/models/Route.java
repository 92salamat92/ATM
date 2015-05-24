package com.example.admin.atm.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Admin on 16.05.2015.
 */
public class Route {
    public Long distanceValue;
    public String distanceText;
    public String startAddress;
    public String endAddress;
    public List<LatLng> points;
}
