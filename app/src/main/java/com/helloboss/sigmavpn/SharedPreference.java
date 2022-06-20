package com.helloboss.sigmavpn;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helloboss.sigmavpn.model.Server;

import static com.helloboss.sigmavpn.Utils.getImgURL;

public class SharedPreference {

    private static final String APP_PREFS_NAME = "SigmaVPNPreference";

    private SharedPreferences mPreference;
    private SharedPreferences.Editor mPrefEditor;
    private Context context;

    private static final String SERVER_COUNTRY = "server_country";
    private static final String SERVER_FLAG = "server_flag";
    private static final String SERVER_OVPN = "server_ovpn";
    private static final String SERVER_OVPN_USER = "server_ovpn_user";
    private static final String SERVER_OVPN_PASSWORD = "server_ovpn_password";

    public SharedPreference(Context context) {
        this.mPreference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = mPreference.edit();
        this.context = context;
    }

    /**
     * Save server details
     * @param server details of ovpn server
     */
    public void saveServer(Server server){
        mPrefEditor.putString(SERVER_COUNTRY, server.getCountry());
        mPrefEditor.putString(SERVER_FLAG, server.getFlagUrl());
        mPrefEditor.putString(SERVER_OVPN, server.getOvpn());
        mPrefEditor.putString(SERVER_OVPN_USER, server.getOvpnUserName());
        mPrefEditor.putString(SERVER_OVPN_PASSWORD, server.getOvpnUserPassword());
        mPrefEditor.commit();
    }

    /**
     * Get server data from shared preference
     * @return server model object
     */
    public Server getServer() {

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.child("TestData").setValue("Hello, World!");

        Server server = new Server(
                mPreference.getString(SERVER_COUNTRY,"Bangladesh"),
                mPreference.getString(SERVER_FLAG,getImgURL(R.drawable.bangladesh)),
                mPreference.getString(SERVER_OVPN,"Bangladesh"),
                mPreference.getString(SERVER_OVPN_USER,"vpn"),
                mPreference.getString(SERVER_OVPN_PASSWORD,"vpn")
        );

        return server;
    }
}
