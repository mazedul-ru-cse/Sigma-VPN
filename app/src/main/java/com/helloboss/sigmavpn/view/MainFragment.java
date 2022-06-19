package com.helloboss.sigmavpn.view;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helloboss.sigmavpn.CheckInternetConnection;
import com.helloboss.sigmavpn.R;
import com.helloboss.sigmavpn.SharedPreference;
import com.helloboss.sigmavpn.databinding.FragmentMainBinding;
import com.helloboss.sigmavpn.interfaces.ChangeServer;
import com.helloboss.sigmavpn.model.Server;
import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener, ChangeServer {

    private Server server;
    private CheckInternetConnection connection;

    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;

    private FragmentMainBinding binding;
    public static boolean connStatus = false;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        View view = binding.getRoot();
        initializeAll();



        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });


        //Show interstitial ads
        setAds();

        //Show banner ads
        //showBannerAds();

        return view;
    }

    // Banner Ads
    private void showBannerAds() {

       // AdRequest adRequest1 = new AdRequest.Builder().build();
        //binding.bannerAdsView1.loadAd(new AdRequest.Builder().build());

       // AdRequest adRequest2 = new AdRequest.Builder().build();
        binding.bannerAdsView2.loadAd(new AdRequest.Builder().build());
    }

    //Check ads loaded or not

    private void adsLoad(){

        if(mInterstitialAd != null){

            mInterstitialAd.show(getActivity());
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {

                    if(vpnStart){

                        binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_on);
                        binding.connectionIndicator.setText("Connected");
                        binding.logTv.setText("");

                    }else{

                        binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
                        binding.connectionIndicator.setText("Disconnected");
                        binding.logTv.setText("");
                    }
                    super.onAdDismissedFullScreenContent();
                }
            });

            mInterstitialAd = null;
            setAds();
        }
    }

    private void setAds(){

        InterstitialAd.load(getContext(),"ca-app-pub-3940256099942544/8691691433", new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        mInterstitialAd = null;
                        //setAds();
                    }
                });

    }


    /**
     * Initialize all variable and object
     */
    private void initializeAll() {
        preference = new SharedPreference(getContext());
        server = preference.getServer();

        // Update current selected server icon
        updateCurrentServerIcon(server.getFlagUrl());

        connection = new CheckInternetConnection();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.vpnBtn.setOnClickListener(this);

        // Checking is vpn already running or not
        isServiceRunning();
        VpnStatus.initLogCache(getActivity().getCacheDir());
    }

    /**
     * @param v: click listener view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vpnBtn:
                // Vpn is running, user would like to disconnect current connection.
                if (vpnStart) {

                    confirmDisconnect();
                }else {
                    prepareVpn();
                }
        }
    }

    /**
     * Show show disconnect confirm dialog
     */
    public void confirmDisconnect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.connection_close_confirm));

        builder.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Prepare for vpn connect with required permission
     */
    private void prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {

                // Checking permission for network monitor
                Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();//have already permission

                // Update confection status
                status("connecting");

            } else {

                // No internet connection available
                showToast("you have no internet connection !!");
            }

        } else if (stopVpn()) {

            // VPN is stopped, show a Toast message.
            showToast("Disconnect Successfully");
        }
    }

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    public boolean stopVpn() {
        try {
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Disconnected");
            adsLoad();
            vpnThread.stop();

            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Taking permission for network access
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Permission granted, start the VPN
            startVpn();
        } else {
            showToast("Permission Deny !! ");
        }
    }

    /**
     * Internet connection status.
     */
    public boolean getInternetStatus() {
        return connection.netCheck(getContext());
    }

    /**
     * Get service status
     */
    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }

    /**
     * Start the VPN
     */
    private void startVpn() {
        try {
            // .ovpn file

            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference(
                    "VPNServer").child(server.getOvpn());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                       String configFile = data.getValue().toString().replace("$$$$","\n");

                       setConfigFile(configFile);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });


        } catch (Exception  e) {
            binding.logTv.setText(e.getMessage());
            e.printStackTrace();

        }
    }

    private void setConfigFile(String replace) {

        try {
            OpenVpnApi.startVpn(getContext(), replace, server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword());
        } catch (RemoteException e) {
            e.printStackTrace();
            binding.logTv.setText(e.getMessage());
        }
        binding.logTv.setText("Connecting...");
        vpnStart = true;

    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    public void setStatus(String connectionState) {
        if (connectionState!= null)
        switch (connectionState) {
            case "DISCONNECTED":
                status("connect");
                vpnStart = false;
                vpnService.setDefaultStatus();
                binding.logTv.setText("");
                break;
            case "CONNECTED":
                connStatus = true;
                vpnStart = true;// it will use after restart this activity
                status("connected");
                binding.logTv.setText("");
                break;
            case "WAIT":
                binding.logTv.setText("waiting for server connection!!");
                break;
            case "AUTH":
                binding.logTv.setText("server authenticating!!");
                break;
            case "RECONNECTING":
                status("connecting");
                binding.logTv.setText("Reconnecting...");
                break;
            case "NONETWORK":
                binding.logTv.setText("No network connection");
                break;
        }

    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    public void status(String status) {

        if (status.equals("connect")) {
            //binding.vpnBtn.setText(getContext().getString(R.string.connect));
            connStatus = false;
        } else if (status.equals("connecting")) {
            connStatus = false;
            //binding.vpnBtn.setText(getContext().getString(R.string.connecting));
        } else if (status.equals("connected")) {
            connStatus = true;
            adsLoad();
            binding.connectionIndicator.setText("Connected");
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_on);


        } else if (status.equals("tryDifferentServer")) {
            connStatus = false;

            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Try again");
        }
        else if (status.equals("loading")) {
            connStatus = false;
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Disconnected");
        }
        else if (status.equals("invalidDevice")) {
            connStatus = false;
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Disconnected");
        }
        else if (status.equals("authenticationCheck")) {
            connStatus = false;
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Disconnected");
        }

    }

    /**
     * Receive broadcast message
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Update status UI
     * @param duration: running time
     * @param lastPacketReceive: last packet receive time
     * @param byteIn: incoming data
     * @param byteOut: outgoing data
     */
    public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
        binding.durationTv.setText("Duration: " + duration);
        binding.lastPacketReceiveTv.setText("Packet Received: " + lastPacketReceive + " second ago");
        binding.byteInTv.setText("Bytes In: " + byteIn);
        binding.byteOutTv.setText("Bytes Out: " + byteOut);
    }

    /**
     * Show toast message
     * @param message: toast message
     */
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * VPN server country icon change
     * @param serverIcon: icon URL
     */
    public void updateCurrentServerIcon(String serverIcon) {
//        Glide.with(getContext())
//                .load(serverIcon)
//                .into(binding.selectedServerIcon);
        binding.selectedServerIcon.setBackgroundResource(getFlagId(serverIcon));
    }

    private int getFlagId(String flag) {

        switch (flag){

            case "Bangladesh":
                return R.drawable.bangladesh;

            case "Argentina":
                return R.drawable.argentina;

            case "Brazil":
                return R.drawable.brazil;

            case "Hongkong":
                return R.drawable.hongkong;

            case "South Korea":
                return R.drawable.southkorea;

            case "Denmark":
                return R.drawable.denmark;

            case "India":
                return R.drawable.india;

            case "Malaysia":
                return R.drawable.malaysia;

            case "Nepal":
                return R.drawable.nepal;

            case "New zealand":
                return R.drawable.newzealand;

            case "North Korea":
                return R.drawable.northkorea;

            case "Pakistan":
                return R.drawable.pakistan;

            case "Portugal":
                return R.drawable.portugal;

            case "Sri lanka":
                return R.drawable.srilanka;

            case "Sudan":
                return R.drawable.sudan;

            case "Syria":
                return R.drawable.syria;

            case "Thailand":
                return R.drawable.thailand;

            case "Turkey":
                return R.drawable.turkey;

            case "Ukraine":
                return R.drawable.ukraine;

            case "US":
                return R.drawable.usa;

            case "Vietnam":
                return R.drawable.vietnam;

            case "Spain":
                return R.drawable.spain;

            case "West Indies":
                return R.drawable.westindies;

            case "Yemen":
                return R.drawable.yemen;

        }

        return R.drawable.bangladesh;
    }

    /**
     * Change server when user select new server
     * @param server ovpn server details
     */
    @Override
    public void newServer(Server server) {
        this.server = server;
        updateCurrentServerIcon(server.getFlagUrl());


        // Stop previous connection
        if (vpnStart) {
            stopVpn();
        }
        
        binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
        binding.connectionIndicator.setText("Disconnected");
        prepareVpn();
    }

    @Override
    public void onResume() {

        if(connStatus){
            //adsLoad();
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_on);
            binding.connectionIndicator.setText("Connected");
        }else{

            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Disconnected");

        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        if (server == null) {
            server = preference.getServer();
        }
        super.onResume();
    }

    @Override
    public void onPause() {

//        binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
//        binding.connectionIndicator.setText("Disconnected");
        setAds();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /**
     * Save current selected server on local shared preference
     */
    @Override
    public void onStop() {

        adsLoad();

        binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
        binding.connectionIndicator.setText("Disconnected");

        if (server != null) {
            preference.saveServer(server);
        }

        super.onStop();
    }

    @Override
    public void onStart() {


        if(connStatus){

           // adsLoad();
            setAds();
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_on);
            binding.connectionIndicator.setText("Connected");

        }else{
            binding.vpnBtn.setBackgroundResource(R.drawable.vpn_btn_back_off);
            binding.connectionIndicator.setText("Disconnected");

        }
        super.onStart();
    }
}
