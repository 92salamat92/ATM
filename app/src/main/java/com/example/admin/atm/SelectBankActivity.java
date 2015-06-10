package com.example.admin.atm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.admin.atm.adapters.BanksListAdapter;
import com.example.admin.atm.models.Bank;
import com.google.gson.Gson;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.GrowEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 03.04.2015.
 */
public class SelectBankActivity extends Activity {
    private List<Bank> allBanks;
    private JazzyListView list_atm;
    private SharedPreferences mSharedPreferences;
    private ProgressBar progressBar_select_bank;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bank);

        list_atm=(JazzyListView)findViewById(R.id.list_banks);
        list_atm.setTransitionEffect(new GrowEffect());

        allBanks = new ArrayList<>();
        getBanks();

        final BanksListAdapter banksListAdapter = new BanksListAdapter(this,allBanks);
        list_atm.setAdapter(banksListAdapter);

        list_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                quit();
                Gson gson = new Gson();
                String json = gson.toJson(banksListAdapter.getItem(position));
                mSharedPreferences.edit().putString(Constants.SELECTED_BANK,json).commit();
                mSharedPreferences.edit().putBoolean(Constants.SELECTED_BANK_CHANGED,true).commit();
            }
        });
    }

    private void quit() {
        finish();
    }

    private void getBanks() {
        mSharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        int banksCount = mSharedPreferences.getInt(Constants.BANKS_COUNT,0);
        if(banksCount>0){
            for(int i=0;i<banksCount;i++) {
                Gson gson = new Gson();
                String json = mSharedPreferences.getString("Bank_"+i,"");
                allBanks.add(gson.fromJson(json, Bank.class));
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}
