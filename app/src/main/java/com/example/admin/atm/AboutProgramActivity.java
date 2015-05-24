package com.example.admin.atm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 12.05.2015.
 */
public class AboutProgramActivity extends Activity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_program);
        listView=(ListView)findViewById(R.id.list_about_program);

        String[] items = {
                "Google Maps - это лидер среди современных картографических сервисов, предоставляющих спутниковые интерактивные карты онлайн",
                "Настройки GPS",
                "Dsasfakjb akbfka afa"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.settings_item, items);
        listView.setAdapter(adapter);
    }
}
