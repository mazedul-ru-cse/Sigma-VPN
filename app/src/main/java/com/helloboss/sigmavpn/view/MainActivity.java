package com.helloboss.sigmavpn.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import com.helloboss.sigmavpn.R;
import com.helloboss.sigmavpn.adapter.ServerListRVAdapter;
import com.helloboss.sigmavpn.interfaces.ChangeServer;
import com.helloboss.sigmavpn.interfaces.NavItemClickListener;
import com.helloboss.sigmavpn.model.Server;

import java.util.ArrayList;

import com.helloboss.sigmavpn.Utils;


public class MainActivity extends AppCompatActivity implements NavItemClickListener {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private Fragment fragment;
    private RecyclerView serverListRv;
    private ArrayList<Server> serverLists;
    private ServerListRVAdapter serverListRVAdapter;
    private DrawerLayout drawer;
    private ChangeServer changeServer;

    public static final String TAG = "SigmaVpn";
    ImageButton menuLeft, menuRight;
    TextView privacyPolicy, ratingApp, shareApp, sendMess, aboutApp, exitApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all variable
        initializeAll();

        menuRight = findViewById(R.id.navbar_right);
        menuLeft = findViewById(R.id.navbar_left);

        privacyPolicy = findViewById(R.id.privacy_policy);
        ratingApp = findViewById(R.id.rating_app);
        shareApp = findViewById(R.id.share_app);
        sendMess = findViewById(R.id.send_message);
        aboutApp = findViewById(R.id.about_app);
        exitApp = findViewById(R.id.exit_app);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightSide();
            }
        });


        menuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftSide();
                //Toast.makeText(MainActivity.this, "This is working", Toast.LENGTH_LONG).show();
            }
        });
      

        transaction.add(R.id.container, fragment);
        transaction.commit();

        // Server List recycler view initialize
        if (serverLists != null) {
            serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }


        // Exit app
        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                appExit();

            }
        });

        //Rate the app
        ratingApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }
        });


    }

    private void rateApp() {

        Uri uri = Uri.parse("market://details?id=$"+getPackageName().toString());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e)  {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$"+getPackageName().toString())));
        }
    }

    private void appExit() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Sigma VPN?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Initialize all object, listener etc
     */
    private void initializeAll() {
        drawer = findViewById(R.id.drawer_layout);

        fragment = new MainFragment();
        serverListRv = findViewById(R.id.serverListRv);
        serverListRv.setHasFixedSize(true);

        serverListRv.setLayoutManager(new LinearLayoutManager(this));

        serverLists = getServerList();
        changeServer = (ChangeServer) fragment;

    }

    /**
     * Close navigation drawer
     */
    public void rightSide(){
        //leftSide();
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }


    public void leftSide(){
       // rightSide();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Generate server array list
     */
    private ArrayList getServerList() {

        ArrayList<Server> servers = new ArrayList<>();

        servers.add(new Server("Bangladesh",
                "Bangladesh",
                "Bangladesh",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("India",
                "India",
                "India",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Argentina",
                "Argentina",
                "Argentina",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Brazil",
                "Brazil",
                "Brazil",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Denmark",
                "Denmark",
                "Denmark",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Hongkong",
                "Hongkong",
                "Hongkong",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Malaysia",
                "Malaysia",
                "Malaysia",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Nepal",
                "Nepal",
                "Nepal",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("New zealand",
                "New zealand",
                "New zealand",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("North Korea",
                "North Korea",
                "North Korea",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Pakistan",
                "Pakistan",
                "Pakistan",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Portugal",
                "Portugal",
                "Portugal",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("South Korea",
                "South Korea",
                "South Korea",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Spain",
                "Spain",
                "Spain",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Sri lanka",
                "Sri lanka",
                "Sri lanka",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Sudan",
                "Sudan",
                "Sudan",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Syria",
                "Syria",
                "Syria",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Thailand",
                "Thailand",
                "Thailand",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Turkey",
                "Turkey",
                "Turkey",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Ukraine",
                "Ukraine",
                "Ukraine",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("US",
                "US",
                "USA",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Vietnam",
                "Vietnam",
                "Vietnam",
                "vpn",
                "vpn"
        ));


        servers.add(new Server("West Indies",
                "West Indies",
                "West Indies",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Yemen",
                "Yemen",
                "Yemen",
                "vpn",
                "vpn"
        ));

        return servers;
    }


    /**
     * On navigation item click, close drawer and change server
     * @param index: server index
     */
    @Override
    public void clickedItem(int index) {
        rightSide();
        changeServer.newServer(serverLists.get(index));
    }
}
