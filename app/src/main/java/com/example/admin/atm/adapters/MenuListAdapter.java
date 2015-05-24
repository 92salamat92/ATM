package com.example.admin.atm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.atm.R;
import com.example.admin.atm.models.Menu;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 07.05.2015.
 */
public class MenuListAdapter extends BaseAdapter {
    private List<Menu> menu;
    private Context mContext;

    public MenuListAdapter(Context mContext, List<Menu> menu) {
        this.mContext = mContext;
        this.menu = menu;
    }

    @Override
    public int getCount() {
        return menu.size();
    }

    @Override
    public Object getItem(int position) {
        return menu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.menu_item,parent,false);
        TextView textView=(TextView)convertView.findViewById(R.id.menu_title);
        ImageView imageView=(ImageView)convertView.findViewById(R.id.menu_image_list) ;
        textView.setText(menu.get(position).name);
        imageView.setImageDrawable(menu.get(position).image);
        return convertView;
    }
}
