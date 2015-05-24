package com.example.admin.atm.models;

import java.util.List;

public class RouteResponse {

    public List<Route> routes;

    public String getPoints() {
        return this.routes.get(0).overview_polyline.points;
    }

    public long getDistanceValue(){
        return this.routes.get(0).legs.get(0).distance.value;
    }

    public String getDistanceText() {
        return this.routes.get(0).legs.get(0).distance.text;
    }

    public String getStartAddress() {
        return this.routes.get(0).legs.get(0).start_address;
    }

    public String getEndAddress() {
        return this.routes.get(0).legs.get(0).end_address;
    }

    class Route{
        OverviewPolyline overview_polyline;
        List<Legs> legs;
    }

    class OverviewPolyline {
        String points;
    }

    class Legs {
        Distance distance;
        String start_address;
        String end_address;
    }

    class Distance {
        String text;
        long value;
    }
}