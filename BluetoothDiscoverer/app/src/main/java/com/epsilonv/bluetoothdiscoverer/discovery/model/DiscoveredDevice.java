package com.epsilonv.bluetoothdiscoverer.discovery.model;

import androidx.annotation.Nullable;

public class DiscoveredDevice {
    private final String name;
    private final String macAddress;

    public DiscoveredDevice(final String name, final String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
    }

    public String getName() {
        return this.name;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof DiscoveredDevice)) {
            return false;
        }
        final DiscoveredDevice that = (DiscoveredDevice) obj;


        return (this.name == that.name || (this.name != null && this.name.equals(that.name)))
                && (this.macAddress == that.macAddress || (this.macAddress != null && this.macAddress.equals(that.macAddress)));
    }
}
