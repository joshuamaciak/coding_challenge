package com.epsilonv.bluetoothdiscoverer.discovery;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.epsilonv.bluetoothdiscoverer.discovery.model.DiscoveredDevice;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class BluetoothDiscoveryBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothDiscoveryBroadcastReceiver.class.getName();
    private ReplaySubject<DiscoveredDevice> devicesSubject = ReplaySubject.create();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            final DiscoveredDevice d = new DiscoveredDevice(bluetoothDevice.getName(),bluetoothDevice.getAddress());
            devicesSubject.onNext(d);
        }
    }

    public Observable<DiscoveredDevice> discoveredDevices() {
        return this.devicesSubject;
    }
}
