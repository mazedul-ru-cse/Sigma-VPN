package com.helloboss.sigmavpn.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;

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

    public static final String TAG = "CakeVPN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all variable
        initializeAll();

        ImageButton menuRight = findViewById(R.id.navbar_right);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        transaction.add(R.id.container, fragment);
        transaction.commit();

        // Server List recycler view initialize
        if (serverLists != null) {
            serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }

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
    public void closeDrawer(){
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    /**
     * Generate server array list
     */
    private ArrayList getServerList() {

        ArrayList<Server> servers = new ArrayList<>();

        servers.add(new Server("Bangladesh",
                Utils.getImgURL(R.drawable.bangladesh),
                "Bangladesh",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("India",
                Utils.getImgURL(R.drawable.india),
                "India",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Argentina",
                Utils.getImgURL(R.drawable.argentina),
                "Argentina",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Brazil",
                Utils.getImgURL(R.drawable.brazil),
                "Brazil",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Denmark",
                Utils.getImgURL(R.drawable.denmark),
                "Denmark",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Hongkong",
                Utils.getImgURL(R.drawable.hongkong),
                "Hongkong",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Malaysia",
                Utils.getImgURL(R.drawable.malaysia),
                "Malaysia",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Nepal",
                Utils.getImgURL(R.drawable.nepal),
                "Nepal",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("New zealand",
                Utils.getImgURL(R.drawable.newzealand),
                "New zealand",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("North Korea",
                Utils.getImgURL(R.drawable.northkorea),
                "North Korea",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Pakistan",
                Utils.getImgURL(R.drawable.pakistan),
                "Pakistan",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Portugal",
                Utils.getImgURL(R.drawable.portugal),
                "Portugal",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("South Korea",
                Utils.getImgURL(R.drawable.southkorea),
                "South Korea",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Spain",
                Utils.getImgURL(R.drawable.spain),
                "Spain",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Sri lanka",
                Utils.getImgURL(R.drawable.srilanka),
                "Sri lanka",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Sudan",
                Utils.getImgURL(R.drawable.sudan),
                "Sudan",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Syria",
                Utils.getImgURL(R.drawable.syria),
                "Syria",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Thailand",
                Utils.getImgURL(R.drawable.thailand),
                "Thailand",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Turkey",
                Utils.getImgURL(R.drawable.turkey),
                "Turkey",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Ukraine",
                Utils.getImgURL(R.drawable.ukraine),
                "Ukraine",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("US",
                Utils.getImgURL(R.drawable.usa),
                "USA",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Vietnam",
                Utils.getImgURL(R.drawable.vietnam),
                "Vietnam",
                "vpn",
                "vpn"
        ));


        servers.add(new Server("West Indies",
                Utils.getImgURL(R.drawable.westindies),
                "West Indies",
                "vpn",
                "vpn"
        ));

        servers.add(new Server("Yemen",
                Utils.getImgURL(R.drawable.yemen),
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
        closeDrawer();
        changeServer.newServer(serverLists.get(index));
    }
}
