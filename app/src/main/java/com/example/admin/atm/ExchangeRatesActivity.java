package com.example.admin.atm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.atm.adapters.ExchangeRatesListAdapter;
import com.example.admin.atm.models.Exchange_Rates;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 28.04.2015.
 */
public class ExchangeRatesActivity extends Activity {
    private ListView listView_exc_rates;
    private List<Exchange_Rates> list_exc_rates;
    private ProgressBar progressbar_exc_rates;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        listView_exc_rates=(ListView)findViewById(R.id.list_exchange_rates);
        progressbar_exc_rates=(ProgressBar)findViewById(R.id.progressbar_exc_rates);

        list_exc_rates=new ArrayList<>();
        getExcRates();
    }

    private void getExcRates() {
        progressbar_exc_rates.setVisibility(View.VISIBLE);
        API.Data api = Constants.RestAdapter().create(API.Data.class);
        api.getExchangeRates(new Callback<List<Exchange_Rates>>() {
            @Override
            public void success(List<Exchange_Rates> exchange_rates, Response response) {
                if(response.getStatus()==200){
                    ExchangeRatesListAdapter exchangeRatesListAdapter=new ExchangeRatesListAdapter(ExchangeRatesActivity.this,exchange_rates);
                    listView_exc_rates.setAdapter(exchangeRatesListAdapter);
                    progressbar_exc_rates.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.isNetworkError()){
                    Toast.makeText(ExchangeRatesActivity.this,"Нет интернет соединения",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ExchangeRatesActivity.this,R.string.another_error,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

}
