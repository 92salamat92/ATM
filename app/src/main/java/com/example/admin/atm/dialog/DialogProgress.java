package com.example.admin.atm.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.example.admin.atm.MainActivity;
import com.example.admin.atm.R;

/**
 * Created by Admin on 01.06.2015.
 */
public class DialogProgress extends DialogFragment {
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(500);
        //dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

   /* @Override
    public void onResume() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                if (keyCode ==  android.view.KeyEvent.KEYCODE_BACK) return true; else return false;}
        });
        super.onResume();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_dialog,container,false);
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

}
