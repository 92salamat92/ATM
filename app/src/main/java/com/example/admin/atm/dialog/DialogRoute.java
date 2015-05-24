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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import com.example.admin.atm.Constants;
import com.example.admin.atm.MainActivity;
import com.example.admin.atm.R;


public class DialogRoute extends DialogFragment {
    public SharedPreferences mSharedPreferences;
    private ListView list_route;
    private CheckBox checkbox_branches;
    private CheckBox checkbox_atms;
    private boolean check_branches;
    private boolean check_atms;
    private boolean check_branches_first;
    private boolean check_atms_first;

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
        View view = inflater.inflate(R.layout.add_menu_dialog,container,false);
        getChecked();
        check_branches_first=check_branches;
        check_atms_first=check_atms;

        checkbox_branches=(CheckBox)view.findViewById(R.id.checkbox_branches);
        checkbox_branches.setChecked(check_branches);
        checkbox_branches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_branches=isChecked;
            }
        });

        checkbox_atms=(CheckBox)view.findViewById(R.id.checkbox_atms);
        checkbox_atms.setChecked(check_atms);
        checkbox_atms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_atms=isChecked;
            }
        });

        list_route=(ListView)view.findViewById(R.id.list_add_menu);
        list_route.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        String[] items = {
                "найти ближайший",
                "очистить маршрут",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.add_menu_item, items);
        list_route.setAdapter(adapter);

        Button button=(Button)view.findViewById(R.id.button_add_menu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstChecked(check_branches,check_atms);
                setChecked(check_branches,check_atms);
                ((MainActivity)getActivity()).setUpMap();
                finish();
            }
        });
        return view;
    }

    private void isFirstChecked(boolean isCheckedBranches,boolean isCheckedAtms){
        if(check_branches_first!=isCheckedBranches || check_atms_first!=isCheckedAtms)mSharedPreferences.edit().putBoolean(Constants.SELECTED_BANK_CHANGED,true).commit();
    }

    private void getChecked(){
        check_branches=mSharedPreferences.getBoolean(Constants.CHECKED_BRANCHES,false);
        check_atms=mSharedPreferences.getBoolean(Constants.CHECKED_ATMS,false);
    }

    private void setChecked(boolean isCheckedBranches,boolean isCheckedAtms){
        mSharedPreferences.edit().putBoolean(Constants.CHECKED_BRANCHES, isCheckedBranches).commit();
        mSharedPreferences.edit().putBoolean(Constants.CHECKED_ATMS,isCheckedAtms).commit();
    }


    private void selectItem(int position){
        switch(position){
            case 0:
                isFirstChecked(check_branches,check_atms);
                setChecked(check_branches,check_atms);
                ((MainActivity) getActivity()).setUpMap();
                if(((MainActivity)getActivity()).locationIsFounded()) {
                    ((MainActivity) getActivity()).foundNearestPoint(((MainActivity) getActivity()).getTop5(((MainActivity) getActivity()).filterPoint()));
                }
                finish();
            case 1:
                ((MainActivity)getActivity()).clearRoute();
                finish();
        }
    }

    private void finish() {
        getDialog().dismiss();
    }
}
