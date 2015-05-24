package com.example.admin.atm.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Admin on 08.05.2015.
 */
public class Menu {
    public String name;
    public Drawable image;

    public Menu(String name, Drawable image) {
        this.name = name;
        this.image = image;
    }
}
