package com.example.admin.atm.dialog;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.admin.atm.Constants;
import com.example.admin.atm.MainActivity;
import com.example.admin.atm.R;


public class DialogRoute extends DialogFragment {
    public SharedPreferences mSharedPreferences;
    private Switch switch_branches;
    private Switch switch_atms;
    private Switch switch_nearest;
    private Switch switch_satellite;

    private boolean switched_satellite;
    private boolean switched_branches;
    private boolean switched_atms;
    private boolean switched_nearest;
    private boolean switched_satellite_first;
    private boolean switched_branches_first;
    private boolean switched_atms_first;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(500);
        dialog.getWindow().setBackgroundDrawable(d);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog,container,false);
        getSwitched();
        switched_branches_first=switched_branches;
        switched_atms_first=switched_atms;
        switched_satellite_first=switched_satellite;

        switch_satellite=(Switch)view.findViewById(R.id.switch_satellite);
        switch_satellite.setChecked(switched_satellite);
        switch_satellite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switched_satellite=isChecked;
            }
        });

        switch_branches=(Switch)view.findViewById(R.id.switch_brances);
        switch_branches.setChecked(switched_branches);
        switch_branches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switched_branches=isChecked;
            }
        });

        switch_atms=(Switch)view.findViewById(R.id.switch_atms);
        switch_atms.setChecked(switched_atms);
        switch_atms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switched_atms=isChecked;
            }
        });

        switch_nearest=(Switch)view.findViewById(R.id.switch_nearest);
        switch_nearest.setChecked(switched_nearest);
        switch_nearest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switched_nearest = isChecked;
            }
        });

        Button button=(Button)view.findViewById(R.id.button_add_menu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFirstSwitchedSatellite(switched_satellite))((MainActivity) getActivity()).setSatellite(switched_satellite);
                isFirstSwitched(switched_branches, switched_atms);
                setSwitch(switched_satellite,switched_branches, switched_atms, switched_nearest);

                ((MainActivity) getActivity()).setUpMap();
                if(switched_nearest) {
                    if (((MainActivity) getActivity()).locationIsFounded()) {
                        ((MainActivity) getActivity()).foundNearestPoint(((MainActivity) getActivity()).getTop5(((MainActivity) getActivity()).filterPoint()));
                    }
                }else((MainActivity) getActivity()).clearRoute();
                finish();
            }
        });
        return view;
    }

    private boolean isFirstSwitchedSatellite(boolean isSwitchedSatellite){
        if(switched_satellite_first!=isSwitchedSatellite)return true;
        return false;
    }

    private void isFirstSwitched(boolean isSwitchedBranches,boolean isSwitchedAtms){
        if(switched_branches_first!=isSwitchedBranches || switched_atms_first!=isSwitchedAtms)mSharedPreferences.edit().putBoolean(Constants.SELECTED_BANK_CHANGED,true).commit();
    }

    private void getSwitched(){
        switched_satellite=mSharedPreferences.getBoolean(Constants.SWITCHED_SATELLITE,false);
        switched_branches=mSharedPreferences.getBoolean(Constants.SWITCHED_BRANCHES,false);
        switched_atms=mSharedPreferences.getBoolean(Constants.SWITCHED_ATMS,false);
        switched_nearest=mSharedPreferences.getBoolean(Constants.SWITCHED_NEAREST,false);
    }

    private void setSwitch(boolean isSwitchedSatellite,boolean isSwitchedBranches,boolean isSwitchedAtms,boolean isSwitchedNearest){
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_SATELLITE, isSwitchedSatellite).commit();
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_BRANCHES, isSwitchedBranches).commit();
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_ATMS,isSwitchedAtms).commit();
        mSharedPreferences.edit().putBoolean(Constants.SWITCHED_NEAREST,isSwitchedNearest).commit();
    }

    @Override
    public void onResume() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                if (keyCode ==  android.view.KeyEvent.KEYCODE_BACK) return true; else return false;}
        });
        super.onResume();
    }

    private void finish() {
        getDialog().dismiss();
    }
}
