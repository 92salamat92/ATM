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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.atm.API;
import com.example.admin.atm.Constants;
import com.example.admin.atm.R;
import com.example.admin.atm.models.Bank;
import com.google.gson.Gson;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DialogProgress extends DialogFragment {
    public SharedPreferences mSharedPreferences;

    private ProgressBar progressBar;
    private TextView updatedText;
    private Button button_ok;
    private SharedPreferences.Editor editor;


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
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onResume() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                if (keyCode ==  android.view.KeyEvent.KEYCODE_BACK) return true; else return false;}
        });
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_dialog,container,false);
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        updatedText = (TextView)view.findViewById(R.id.updated_text);
        button_ok=(Button)view.findViewById(R.id.button_ok);
        progressBar.setVisibility(View.VISIBLE);
        updatedText.setVisibility(View.VISIBLE);
        button_ok.setVisibility(View.GONE);
        getBanks();
        return view;
    }


    public void getBanks() {
        API.Data api = Constants.RestAdapter().create(API.Data.class);
        api.getBanks(new Callback<List<Bank>>() {
            @Override
            public void success(List<Bank> banks, Response response) {
                if(response.getStatus()==200){
                    saveBanks(banks);
                    updatedText.setText(R.string.update_done);
                    progressBar.setVisibility(View.GONE);
                    button_ok.setVisibility(View.VISIBLE);
                    button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(error.isNetworkError()) {
                    updatedText.setText(R.string.error_access_to_internet);
                    progressBar.setVisibility(View.GONE);
                    button_ok.setVisibility(View.VISIBLE);
                    button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(),R.string.another_error,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveBanks(List<Bank> banks) {
        editor = mSharedPreferences.edit();
        editor.clear();

        editor.putInt(Constants.BANKS_COUNT, banks.size());
        editor.putString(Constants.SELECTED_BANK,null);
        editor.putBoolean(Constants.SELECTED_BANK_CHANGED, false);
        editor.putBoolean(Constants.CHECKED_BRANCHES,true);
        editor.putBoolean(Constants.CHECKED_ATMS,true);
        editor.putBoolean(Constants.UPDATE_DONE,true);


        for(int i=0;i<banks.size();i++){
            Gson gson = new Gson();
            String json = gson.toJson(banks.get(i));
            editor.putString("Bank_"+i,json);
        }
        editor.commit();
    }

    private void finish() {
        getDialog().onBackPressed();
    }
}
