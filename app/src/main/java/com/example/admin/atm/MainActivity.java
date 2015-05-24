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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.atm.dialog.DialogRoute;
import com.example.admin.atm.dialog.DialogSearch;
import com.example.admin.atm.fragment.MenuFragment;
import com.example.admin.atm.models.Bank;
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

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng LOCATION_BISHKEK_CENTER=new LatLng(42.871909, 74.611501);
    private MenuFragment menuFragment;
    private Route route;
    private long minDistance=10000000;
    private int countSuccess=0;
    private Boolean selected_bank_changed;

    public SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    public Bank selectedBank;
    public List<Point> bank_points;
    public List<Point> filtered_points;
    private ImageView menu_image;
    private Polyline line;

    DialogSearch dialogSearch=new DialogSearch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuFragment  = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu);
        menuFragment.setUp(R.id.menu,(DrawerLayout) findViewById(R.id.drawer_layout));
        menu_image=(ImageView)findViewById(R.id.menu_image);

        mSharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(Constants.CHECKED_BRANCHES,true).commit();
        mSharedPreferences.edit().putBoolean(Constants.CHECKED_ATMS,true).commit();
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
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if(selectedBank!=null) {
                        DialogRoute progress = new DialogRoute();
                        progress.show(getFragmentManager(), "progress");
                    }
                    else
                        Toast.makeText(MainActivity.this,R.string.bank_not_selected,Toast.LENGTH_SHORT).show();
                }
            });
        } else setUpMap();
    }

    public Bitmap bitmapCompression(Bitmap source){
        if(source.getHeight()>100||source.getWidth()>100) {
            int nh = (int) (source.getHeight() * (75.0 / source.getWidth()));
            return Bitmap.createScaledBitmap(source, 75, nh, true);
        }
        return source;
    }

    public void setUpMap() {

        if(mSharedPreferences.getBoolean(Constants.UPDATE_DONE,false)){
            clearMap();
            mSharedPreferences.edit().putBoolean(Constants.UPDATE_DONE,false).commit();
        }
        getSelectedBank();
        if(selectedBank!=null && selected_bank_changed){
            mSharedPreferences.edit().putBoolean(Constants.SELECTED_BANK_CHANGED,false).commit();
            drawPoints(filterPoint());

            //Toast.makeText(MainActivity.this, "Долгий тап для доп.меню", Toast.LENGTH_LONG).show();

            /*if(selectedBank.imageUrl!=null || selectedBank.imageUrl!="none"){
                Picasso.with(this).load(selectedBank.imageUrl).into(menu_image);
            }*/
        }
    }

    public Boolean locationIsFounded(){
        if(mMap.getMyLocation()!=null) return true;
        else Toast.makeText(MainActivity.this,R.string.you_location_not_found, Toast.LENGTH_SHORT).show();
        return false;
    }

    public List<Point> filterPoint(){
        bank_points = (selectedBank.points);
        filtered_points=new ArrayList<>();
        Boolean checked_branches=mSharedPreferences.getBoolean(Constants.CHECKED_BRANCHES,false);
        Boolean checked_atms=mSharedPreferences.getBoolean(Constants.CHECKED_ATMS,false);
        for(int i=0;i<bank_points.size();i++){
            if(checked_branches){
                if(bank_points.get(i).type.toString().equals("bank"))filtered_points.add(bank_points.get(i));
            }
            if(checked_atms){
                if(bank_points.get(i).type.toString().equals("atm"))filtered_points.add(bank_points.get(i));
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
        if(top5Points.size()==0) Toast.makeText(MainActivity.this,R.string.points_not_found,Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainActivity.this, R.string.error_access_to_internet, Toast.LENGTH_SHORT).show();
                            }
                        }
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
                    bmp_marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker1);
                }else
                {
                    bmp_marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker2);
                }

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(list_filtered.get(i).lat, list_filtered.get(i).lng))
                        .title(selectedBank.name)
                        .snippet(bank_points.get(i).name)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapCompression(bmp_marker))));
                mMap.setInfoWindowAdapter(new MyCustomInfoWindowAdapter());
            }
        }
    }

    public void clearMap(){
        mMap.clear();
    }

    public void clearRoute(){
        if(line!=null ){
            line.remove();
            minDistance=10000000;
        }
    }

    private void drawNearestRoute() {
        if(route.points!=null){
            clearRoute();
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
            Toast.makeText(MainActivity.this,"Не найдено!",Toast.LENGTH_SHORT).show();
        }

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
