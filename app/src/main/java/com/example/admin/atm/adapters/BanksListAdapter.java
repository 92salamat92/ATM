package com.example.admin.atm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.atm.R;
import com.example.admin.atm.models.Bank;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Admin on 04.05.2015.
 */
public class BanksListAdapter extends BaseAdapter {
    private List<Bank> banks;
    private Context mContext;

    public BanksListAdapter(Context mContext, List<Bank> banks) {
        this.mContext = mContext;
        this.banks = banks;
    }

    @Override
    public int getCount() {
        return banks.size();
    }

    @Override
    public Bank getItem(int position) {
        return banks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView=inflater.inflate(R.layout.bank_item,parent,false);

        ImageView bankImage = (ImageView)convertView.findViewById(R.id.bank_image);
        TextView bankName = (TextView)convertView.findViewById(R.id.bank_name);

        Bank bank = getItem(position);
        bankName.setText(bank.name);

        if(bank.imageUrl!=null || bank.imageUrl!="none"){
            Picasso.with(mContext).load(bank.imageUrl).into(bankImage);
        }

        return convertView;
    }
}
