package com.example.admin.atm.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.admin.atm.R;

/**
 * Created by Admin on 22.05.2015.
 */
public class DialogSearch extends DialogFragment {
    private ProgressBar progressbar_search;
    private TextView search_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(500);
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog,container,false);
        progressbar_search=(ProgressBar)view.findViewById(R.id.progressbar_search);
        search_text=(TextView)view.findViewById(R.id.search_text);
        progressbar_search.setVisibility(View.VISIBLE);
        search_text.setVisibility(View.VISIBLE);
        return view;
    }

    private void finish() {
        getDialog().onBackPressed();
    }
}
