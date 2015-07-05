package com.example.admin.atm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.atm.dialog.DialogRoute;
import com.example.admin.atm.dialog.DialogSearch;
import com.example.admin.atm.fragment.MenuFragment;
import com.example.admin.atm.models.Bank;
import com.example.admin.atm.models.MyItem;
import com.example.admin.atm.models.Point;
import com.example.admin.atm.models.Route;
import com.example.admin.atm.models.RouteResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity {
    private GoogleMap mMap;
    private LatLng LOCATION_BISHKEK_CENTER=new LatLng(42.871909, 74.611501);
    private MenuFragment menuFragment;
    private ImageButton filter_button;
    private TextView bank_name;
    private TextView route_nearest;
    private Route route;
    private long minDistance=10000000;
    private int countSuccess=0;
    private Boolean selected_bank_changed;

    public SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    public Bank selectedBank;
    public List<Point> bank_points;
    public List<Point> filtered_points;
    private Polyline line;
    private ClusterManager<MyItem> mClusterManager;

    DialogSearch dialogSearch=new DialogSearch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuFragment  = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu);
        menuFragment.setUp(R.id.menu,(DrawerLayout) findViewById(R.id.drawer_layout));
        bank_name=(TextView)findViewById(R.id.main_bank_name);
        route_nearest=(TextView)findViewById(R.id.main_route);

        filter_button=(ImageButton)findViewById(R.id.filter_button);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedBank!=null) {
                    DialogRoute progress = new DialogRoute();
                    progress.show(getFragmentManager(), "progress");
                }
                else
                    Toast.makeText(MainActivity.this,R.string.bank_not_selected,Toast.LENGTH_SHORT).show();
            }
        });

        mSharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_SATELLITE,false).commit();
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_BRANCHES,true).commit();
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_ATMS,true).commit();
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_NEAREST,false).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor = mSharedPreferences.edit();
        editor.putString(Constants.SELECTED_BANK,null);
        editor.putBoolean(Constants.SELECTED_BANK_CHANGED, false);
        editor.commit();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            CameraUpdate update=CameraUpdateFactory.newLatLngZoom(LOCATION_BISHKEK_CENTER,14);
            mMap.animateCamera(update);

            /*mClusterManager = new ClusterManager<MyItem>(this, mMap);

            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                @Override
                public boolean onClusterItemClick(MyItem myItem) {
                    Toast.makeText(MainActivity.this,myItem.getPosition().toString(),Toast.LENGTH_SHORT).show();
                    return true;
                }
            });*/
        } else setUpMap();
    }

    public Bitmap bitmapCompression(Bitmap source){
        if(source.getHeight()>=720||source.getWidth()>=1280) {
            int nh = (int) (source.getHeight() * (80.0 / source.getWidth()));
            return Bitmap.createScaledBitmap(source, 80, nh, true);
        }else {
            int nh = (int) (source.getHeight() * (50.0 / source.getWidth()));
            return Bitmap.createScaledBitmap(source, 50, nh, true);
        }
        //return source;
    }

    public void setUpMap() {
        if(mSharedPreferences.getBoolean(Constants.UPDATE_DONE,false)){
            bank_name.setText("");
            clearMap();
            clearRoute();
            mSharedPreferences.edit().putBoolean(Constants.UPDATE_DONE,false).commit();
        }
        getSelectedBank();
        if(selectedBank!=null && selected_bank_changed){
            bank_name.setText(selectedBank.name);
            clearRoute();
            mSharedPreferences.edit().putBoolean(Constants.SELECTED_BANK_CHANGED,false).commit();
            drawPoints(filterPoint());
        }
    }

    public void setSatellite(boolean mapType){
        if (mapType)mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);else mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public boolean locationIsFounded(){
        if(mMap.getMyLocation()!=null) return true;
        else Toast.makeText(MainActivity.this,R.string.you_location_not_found, Toast.LENGTH_SHORT).show();
        return false;
    }

    public List<Point> filterPoint(){
        bank_points = (selectedBank.points);
        filtered_points=new ArrayList<>();
        Boolean switched_branches=mSharedPreferences.getBoolean(Constants.SWITCHED_BRANCHES,false);
        Boolean switched_atms=mSharedPreferences.getBoolean(Constants.SWITCHED_ATMS,false);
        for(int i=0;i<bank_points.size();i++){
            if(switched_branches){
                if(bank_points.get(i).type.equals("bank"))filtered_points.add(bank_points.get(i));
            }
            if(switched_atms){
                if(bank_points.get(i).type.equals("atm"))filtered_points.add(bank_points.get(i));
            }
        }
        return filtered_points;
    }

    public List<Point>getTop5(List<Point>list_top){
        List<Point> top5 = new ArrayList<>();
        List<Distance> distances = new ArrayList<>();

        LatLng currentPosition = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        for(int i=0;i<list_top.size();i++){
            Distance distance = new Distance(Math.sqrt(Math.pow((currentPosition.latitude-list_top.get(i).lat),2)+ Math.pow((currentPosition.longitude-list_top.get(i).lng),2)),i);
            distances.add(distance);
        }

        for (int i=0;i<distances.size()-1;i++)
            for(int j=i+1;j<distances.size();j++)
                if(distances.get(i).distance>distances.get(j).distance){
                    double distance = distances.get(i).distance;
                    int index=distances.get(i).index;
                    distances.get(i).distance = distances.get(j).distance;
                    distances.get(i).index = distances.get(j).index;
                    distances.get(j).distance = distance;
                    distances.get(j).index = index;
                }

        int top = distances.size();
        if(top>5) top = 5;
        for (int i=0;i<top;i++)
            top5.add(list_top.get(distances.get(i).index));
        return top5;
    }

    public void foundNearestPoint(final List<Point> top5Points){
        if(top5Points.size()==0) {
            clearRouteText();
            Toast.makeText(MainActivity.this,R.string.points_not_found,Toast.LENGTH_SHORT).show();
        }
        else {
            dialogSearch.show(getFragmentManager(), "progress1");
            String origin = mMap.getMyLocation().getLatitude() + "," + mMap.getMyLocation().getLongitude();
            for (int i = 0; i < top5Points.size(); i++) {
                String destination = top5Points.get(i).lat + "," + top5Points.get(i).lng;
                API.RouteApi api = Constants.RestAdapterForGoogleMap().create(API.RouteApi.class);
                api.getRoute(origin, destination, "walking", true, "ru", new Callback<RouteResponse>() {
                    @Override
                    public void success(RouteResponse routeResponse, Response response) {
                        if (response.getStatus() == 200) {
                            countSuccess++;
                            if (routeResponse.routes.size() > 0) {
                                if (routeResponse.getDistanceValue() < minDistance) {
                                    route = new Route();
                                    route.distanceValue = routeResponse.getDistanceValue();
                                    route.distanceText = routeResponse.getDistanceText();
                                    route.startAddress = routeResponse.getStartAddress();
                                    route.endAddress = routeResponse.getEndAddress();
                                    route.points = PolyUtil.decode(routeResponse.getPoints());

                                    minDistance = routeResponse.getDistanceValue();
                                }
                                if (countSuccess >= 5 || countSuccess == top5Points.size()) {
                                    countSuccess = 0;
                                    drawNearestRoute();
                                    dialogSearch.dismiss();
                                }
                            } else if (countSuccess == 5 || countSuccess == bank_points.size()) {
                                countSuccess = 0;
                                dialogSearch.dismiss();
                                clearRouteText();
                                clearRoute();
                                Toast.makeText(MainActivity.this,R.string.route_not_found, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.isNetworkError()) {
                            countSuccess++;
                            if (countSuccess == 5 || countSuccess == bank_points.size()) {
                                countSuccess = 0;
                                dialogSearch.dismiss();
                                Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                            }
                        }else  Toast.makeText(MainActivity.this, R.string.another_error_nearest, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void drawPoints(List<Point> list_filtered){
        Bitmap bmp_marker;
        clearMap();
        if(list_filtered!=null){
            for (int i = 0; i < list_filtered.size(); i++) {
                if(list_filtered.get(i).type.toString().equals("atm")){
                    bmp_marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker_atm);
                }else
                {
                    bmp_marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker_branches);
                }
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(list_filtered.get(i).lat, list_filtered.get(i).lng))
                        .title(selectedBank.name)
                        .snippet(list_filtered.get(i).name)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapCompression(bmp_marker))));
                mMap.setInfoWindowAdapter(new MyCustomInfoWindowAdapter());

                /*final MyItem offsetItem = new MyItem(list_filtered.get(i).lat, list_filtered.get(i).lng);
                mClusterManager.addItem(offsetItem);
                mMap.setOnCameraChangeListener(mClusterManager);*/

            }
        }
    }

    private void drawNearestRoute() {
        if(route.points!=null){
            clearRoute();
            if(route.distanceValue>=1000)route_nearest.setText("до ближайшего: "+String.format("%.2f", route.distanceValue / 1000.0)+"км");
            else route_nearest.setText("до ближайшего: "+route.distanceValue.toString()+"м");

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(5f).color(Color.RED);
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

            for (LatLng point : route.points) {
                polylineOptions.add(point);
                latLngBuilder.include(point);
            }

            line=mMap.addPolyline(polylineOptions);
            int size = getResources().getDisplayMetrics().widthPixels;
            LatLngBounds latLngBounds = latLngBuilder.build();
            CameraUpdate nearestRoute = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
            mMap.moveCamera(nearestRoute);
        }else
        {
            Toast.makeText(MainActivity.this,R.string.route_not_found,Toast.LENGTH_SHORT).show();
            clearRouteText();
        }
    }

    public void clearMap(){
        mMap.clear();
    }

    public void clearRoute(){
        clearRouteText();
        if(line!=null ){
            line.remove();
            minDistance=10000000;
        }
    }

    private void clearRouteText(){
        route_nearest.setText("");
    }

    class MyCustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mymarkerview;

        MyCustomInfoWindowAdapter() {
            mymarkerview = getLayoutInflater().inflate(
                    R.layout.marker_info_window, null);
        }

        public View getInfoWindow(Marker marker) {
            render(marker, mymarkerview);
            ((TextView) mymarkerview.findViewById(R.id.title_marker)).setText(marker
                    .getTitle());
            ((TextView) mymarkerview.findViewById(R.id.snippet_marker)).setText(marker
                    .getSnippet());
            return mymarkerview;
        }

        public View getInfoContents(Marker marker) {
            ((TextView) mymarkerview.findViewById(R.id.title_marker)).setText(marker
                    .getTitle());
            ((TextView) mymarkerview.findViewById(R.id.snippet_marker)).setText(marker
                    .getSnippet());
            return null;
        }

        private void render(Marker marker, View view) {
        }
    }

    public void getSelectedBank(){
        mSharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.SELECTED_BANK,null);
        selectedBank=gson.fromJson(json, Bank.class);
        minDistance = 10000000;
        selected_bank_changed=mSharedPreferences.getBoolean(Constants.SELECTED_BANK_CHANGED,false);
    }

    public class Distance{
        public double distance;
        public int index;

        public Distance(double dis,int in){
            distance=dis;
            index = in;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Вы уверены, что хотите выйти?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}
