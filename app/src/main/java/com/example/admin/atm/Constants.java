package com.example.admin.atm;

import retrofit.RestAdapter;

/**
 * Created by Admin on 07.05.2015.
 */
public final class Constants {
    public static final String SETTINGS = "DATA_BASE";
    public static final String WEB_URL = "http://private-314e-atm1.apiary-mock.com";
    public static final String WEB_URL_GOOGLE = "https://maps.googleapis.com";
    public static final String BANKS_COUNT = "banksCount";
    public static final String SELECTED_BANK = "selected bank";
    public static final String SELECTED_BANK_CHANGED = "selected_bank_changed";
    public static final String CHECKED_BRANCHES = "checkhed_branches";
    public static final String CHECKED_ATMS = "checkhed_atms";
    public static final String UPDATE_DONE = "update_done";

    public static final RestAdapter RestAdapter(){
        return new RestAdapter.Builder().setEndpoint(WEB_URL).build();
    }

    public static final RestAdapter RestAdapterForGoogleMap(){
        return new RestAdapter.Builder().setEndpoint(WEB_URL_GOOGLE).build();
    }
}
