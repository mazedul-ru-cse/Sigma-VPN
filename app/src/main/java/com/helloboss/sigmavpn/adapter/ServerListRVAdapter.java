package com.helloboss.sigmavpn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.helloboss.sigmavpn.R;
import com.helloboss.sigmavpn.interfaces.NavItemClickListener;
import com.helloboss.sigmavpn.model.Server;

import java.util.ArrayList;

public class ServerListRVAdapter extends RecyclerView.Adapter<ServerListRVAdapter.MyViewHolder> {

    private ArrayList<Server> serverLists;
    private Context mContext;
    private NavItemClickListener listener;

    public ServerListRVAdapter(ArrayList<Server> serverLists, Context context) {
        this.serverLists = serverLists;
        this.mContext = context;
        listener = (NavItemClickListener) context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.server_list_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.serverCountry.setText(serverLists.get(position).getCountry());

        holder.serverIcon.setBackgroundResource(getFlagId(serverLists.get(position).getFlagUrl()));

        holder.serverItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Select Country name : ",serverLists.get(position).getFlagUrl());
                listener.clickedItem(position);

            }
        });
    }

    private int getFlagId(String flag) {

        switch (flag){

            case "Bangladesh":
                return R.drawable.bangladesh;

            case "Argentina":
                return R.drawable.argentina;

            case "Brazil":
                return R.drawable.brazil;

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

    @Override
    public int getItemCount() {
        return serverLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout serverItemLayout;
        ImageView serverIcon;
        TextView serverCountry;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serverItemLayout = itemView.findViewById(R.id.serverItemLayout);
            serverIcon = itemView.findViewById(R.id.iconImg);
            serverCountry = itemView.findViewById(R.id.countryTv);
        }
    }
}
