package com.example.admin.atm.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admin.atm.ExchangeRatesActivity;
import com.example.admin.atm.R;
import com.example.admin.atm.SelectBankActivity;
import com.example.admin.atm.SettingsActivity;
import com.example.admin.atm.adapters.MenuListAdapter;
import com.example.admin.atm.models.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 18.04.2015.
 */
public class MenuFragment extends Fragment {

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private int mCurrentSelectedPosition = 0;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private ListView mMenuListView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu,container,false);

        mMenuListView=(ListView)view.findViewById(R.id.menu_list);

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        List<Menu> menu_list_title=new ArrayList<>();
        menu_list_title.add(new Menu(getString(R.string.menu_select_bank),getResources().getDrawable(R.drawable.bank)));
        menu_list_title.add(new Menu(getString(R.string.menu_select_exchange_rates),getResources().getDrawable(R.drawable.exc_rates)));
        menu_list_title.add(new Menu(getString(R.string.menu_select_settings),getResources().getDrawable(R.drawable.settings_icon1)));

        MenuListAdapter menuListAdapterListAdapter = new MenuListAdapter(getActivity(),menu_list_title);
        mMenuListView.setAdapter(menuListAdapterListAdapter);

        return view;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_launcher,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        )
        {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }



    private void selectItem(int position) {
        switch (position){
            case 0:
                Intent intent = new Intent(getActivity(),SelectBankActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(getActivity(),ExchangeRatesActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);
                break;
        }
        if (mMenuListView != null) {
            mMenuListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

}
