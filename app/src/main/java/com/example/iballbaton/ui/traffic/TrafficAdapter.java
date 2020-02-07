package com.example.iballbaton.ui.traffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iballbaton.R;

import java.util.ArrayList;

import host.stjin.expandablecardview.ExpandableCardView;

public class TrafficAdapter extends RecyclerView.Adapter<TrafficAdapter.TrafficViewHolder> {
    private Context context;
    private ArrayList<TrafficModel> trafficModels;

    public TrafficAdapter(Context context, ArrayList<TrafficModel> trafficModels) {
        this.context = context;
        this.trafficModels = trafficModels;
    }

    @NonNull
    @Override
    public TrafficViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_traffic,parent,false);

        return new TrafficViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrafficViewHolder holder, int position) {
        TrafficModel traffic = trafficModels.get(position);
        holder.evc_traffic.setTitle(-1,traffic.getIp_addr());
        holder.tv_uplink.setText(traffic.getUplink());
        holder.tv_downlink.setText(traffic.getDownlink());
        holder.tv_sentMsg.setText(traffic.getSentMsg());
        holder.tv_sendBytes.setText(traffic.getSentBytes());
        holder.tv_receivedMsg.setText(traffic.getReceivedMsg());
        holder.tv_receivedBytes.setText(traffic.getReceivedBytes());
    }

    @Override
    public int getItemCount() {
        return trafficModels.size();
    }

    public class TrafficViewHolder extends RecyclerView.ViewHolder{
        TextView tv_uplink, tv_downlink, tv_sentMsg, tv_sendBytes, tv_receivedMsg, tv_receivedBytes;
        ExpandableCardView evc_traffic;
        public TrafficViewHolder(@NonNull View itemView) {
            super(itemView);

            evc_traffic = itemView.findViewById(R.id.ecv_traffic);
            tv_uplink = itemView.findViewById(R.id.tv_uplink);
            tv_downlink = itemView.findViewById(R.id.tv_downlink);
            tv_sentMsg = itemView.findViewById(R.id.tv_sentMessage);
            tv_sendBytes = itemView.findViewById(R.id.tv_sentBytes);
            tv_receivedMsg = itemView.findViewById(R.id.tv_receivedMessage);
            tv_receivedBytes = itemView.findViewById(R.id.tv_receivedBytes);
        }
    }
}
