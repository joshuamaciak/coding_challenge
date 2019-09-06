package com.epsilonv.bluetoothdiscoverer.discovery.view;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epsilonv.bluetoothdiscoverer.R;
import com.epsilonv.bluetoothdiscoverer.discovery.model.DiscoveredDevice;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DiscoveredDevicesRecyclerViewAdapter extends RecyclerView.Adapter<DiscoveredDevicesRecyclerViewAdapter.DiscoveredDevicesViewHolder> {
    private List<DiscoveredDevice> devices;

    DiscoveredDevicesRecyclerViewAdapter() {
        this.devices = Collections.emptyList();
    }

    @NonNull
    @Override
    public DiscoveredDevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.discovered_device_item, parent, false);
        return new DiscoveredDevicesViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoveredDevicesViewHolder holder, int position) {
        final DiscoveredDevice device = devices.get(position);
        if (device.getName() == null) {
            holder.nameTextView.setText(R.string.null_discovered_device_name);
            holder.nameTextView.setTypeface(null, Typeface.ITALIC);
        } else {
            holder.nameTextView.setText(device.getName());
        }
        holder.macAddressTextView.setText(device.getMacAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    void setDevices(final List<DiscoveredDevice> devices) {
        this.devices = devices;
        this.notifyDataSetChanged();
    }

    /**
     * The ViewHolder pattern is used to hold the views that are actually being displayed. ViewHolders
     * get recycled automatically & rebound when different data becomes visible
     */
     static class DiscoveredDevicesViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView macAddressTextView;
        DiscoveredDevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.nameTextView);
            this.macAddressTextView = itemView.findViewById(R.id.macAddressTextView);

        }
    }
}
