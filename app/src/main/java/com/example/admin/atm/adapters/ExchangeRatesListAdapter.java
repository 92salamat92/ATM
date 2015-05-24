package com.example.admin.atm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.atm.R;
import com.example.admin.atm.models.Exchange_Rates;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Admin on 05.05.2015.
 */
public class ExchangeRatesListAdapter extends BaseAdapter {
    private List<Exchange_Rates> banks_exc_rates;
    private Context mContext;

    public ExchangeRatesListAdapter(Context mContext, List<Exchange_Rates> banks_exc_rates) {
        this.mContext = mContext;
        this.banks_exc_rates = banks_exc_rates;
    }

    @Override
    public int getCount() {
        return banks_exc_rates.size();
    }

    @Override
    public Exchange_Rates getItem(int position) {
        return banks_exc_rates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //convertView=inflater.inflate(R.layout.exchange_rates_item,parent,false);
        convertView=inflater.inflate(R.layout.exchange_rates_item,parent,false);

        TextView bankNameExcRates = (TextView)convertView.findViewById(R.id.bank_name_exch_rates);
        TextView dollar_buy=(TextView)convertView.findViewById(R.id.dollar_buy);
        TextView dollar_sell=(TextView)convertView.findViewById(R.id.dollar_sell);
        TextView euro_buy=(TextView)convertView.findViewById(R.id.euro_buy);
        TextView euro_sell=(TextView)convertView.findViewById(R.id.euro_sell);
        TextView ruble_buy=(TextView)convertView.findViewById(R.id.ruble_buy);
        TextView ruble_sell=(TextView)convertView.findViewById(R.id.ruble_sell);
        TextView tenge_buy=(TextView)convertView.findViewById(R.id.tenge_buy);
        TextView tenge_sell=(TextView)convertView.findViewById(R.id.tenge_sell);

        Exchange_Rates exchange_rates=getItem(position);

        bankNameExcRates.setText(exchange_rates.name);
        dollar_buy.setText(new Double(exchange_rates.dollar_buy).toString());
        dollar_sell.setText(new Double(exchange_rates.dollar_sell).toString());
        euro_buy.setText(new Double(exchange_rates.euro_buy).toString());
        euro_sell.setText(new Double(exchange_rates.euro_sell).toString());
        ruble_buy.setText(new Double(exchange_rates.ruble_buy).toString());
        ruble_sell.setText(new Double(exchange_rates.ruble_sell).toString());
        tenge_buy.setText(new Double(exchange_rates.tenge_buy).toString());
        tenge_sell.setText(new Double(exchange_rates.tenge_sell).toString());

        return convertView;
    }
}
