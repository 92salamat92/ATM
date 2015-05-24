package com.example.admin.atm;

import com.example.admin.atm.models.Bank;
import com.example.admin.atm.models.Exchange_Rates;
import com.example.admin.atm.models.RouteResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Admin on 06.05.2015.
 */
public class API {
    public interface Data{
        @GET("/points")
        void getBanks(Callback<List<Bank>> callback);

        @GET("/exchange_rates")
        void getExchangeRates(Callback<List<Exchange_Rates>> callback);
    }

    public interface RouteApi{
        @GET("/maps/api/directions/json")
        void getRoute(
                @Query(value = "origin") String start,
                @Query(value = "destination") String finish,
                @Query(value = "mode") String mode,
                @Query("sensor") boolean sensor,
                @Query("language") String language,
                Callback<RouteResponse> callback);
    }
}
