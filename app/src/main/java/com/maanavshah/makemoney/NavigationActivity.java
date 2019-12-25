package com.maanavshah.makemoney;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.maanavshah.makemoney.Helper.HttpGetRequest;
import com.maanavshah.makemoney.Helper.SharedConfig;
import com.maanavshah.makemoney.ui.HomeFragment;
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem;
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NavigationActivity extends AppCompatActivity {

    public static Fragment fragment;
    SNavigationDrawer sNavigationDrawer;
    Class fragmentClass;
    private static final String GET_CONFIG = "http://10.0.2.2:3000/api/users/get_config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        StringBuilder stringBuilder = new StringBuilder(GET_CONFIG);
        stringBuilder.append("?name=add_coins");
        HttpGetRequest getRequest = new HttpGetRequest(getApplicationContext());
        String add_coins = null;
        try {
            add_coins = getRequest.execute(stringBuilder.toString()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (add_coins != null) {
            SharedConfig.setConfig(getApplicationContext(), "add_coins", add_coins);
        } else {
            Toast.makeText(getApplicationContext(), "Error loading coins!", Toast.LENGTH_LONG).show();
            SharedConfig.setConfig(getApplicationContext(), "add_coins", "0");
        }

        sNavigationDrawer = findViewById(R.id.navigationDrawer);

        //Creating a list of menu Items

        List<MenuItem> menuItems = new ArrayList<>();

        //Use the MenuItem given by this library and not the default one.
        //First parameter is the title of the menu item and then the second parameter is the image which will be the background of the menu item.

        menuItems.add(new MenuItem("Home", R.drawable.news_bg));
        menuItems.add(new MenuItem("Redeem Wallet", R.drawable.music_bg));
        menuItems.add(new MenuItem("Policy Page", R.drawable.message_bg));
        menuItems.add(new MenuItem("Contact Us", R.drawable.feed_bg));

        sNavigationDrawer.setMenuItemList(menuItems);
        fragmentClass = HomeFragment.class;
        sNavigationDrawer.setAppbarTitleTV("Home");

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();

        }

        //Listener to handle the menu item click. It returns the position of the menu item clicked. Based on that you can switch between the fragments.
        sNavigationDrawer.setOnMenuItemClickListener(new SNavigationDrawer.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(int position) {
                System.out.println("Position " + position);

                switch (position) {
                    case 0: {
                        fragmentClass = HomeFragment.class;
                        sNavigationDrawer.setAppbarTitleTV("Home");
                        break;
                    }
                }

                //Listener for drawer events such as opening and closing.
                sNavigationDrawer.setDrawerListener(new SNavigationDrawer.DrawerListener() {

                    @Override
                    public void onDrawerOpened() {
                    }

                    @Override
                    public void onDrawerOpening() {
                    }

                    @Override
                    public void onDrawerClosing() {
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
                        }
                    }

                    @Override
                    public void onDrawerClosed() {
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        System.out.println("State " + newState);
                    }
                });
            }
        });

    }

}
