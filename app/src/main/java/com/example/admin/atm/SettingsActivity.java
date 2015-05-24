package com.example.admin.atm;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.admin.atm.dialog.DialogProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 29.04.2015.
 */
public class SettingsActivity extends ActionBarActivity {
    private Button button_update;
    private Button button_GPS;
    private Button button_about_program;
    private ListView list_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.menu_select_settings);

        list_settings=(ListView)findViewById(R.id.list_settings);
        list_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        String[] items = {
                 "Обновить", "Настройки GPS", "О программе"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.settings_item, items);

        list_settings.setAdapter(adapter);
    }


    private void selectItem(int position){
        switch (position){
            case 0:
                DialogProgress progress = new DialogProgress();
                progress.show(getFragmentManager(),"progress");
                break;
            case 1:
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                break;
            case 2:
                startActivity(new Intent(SettingsActivity.this,AboutProgramActivity.class));
                break;
        }
    }

}
