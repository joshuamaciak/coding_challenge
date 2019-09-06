package com.epsilonv.bluetoothdiscoverer.discovery;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public class BluetoothTurnedOffBroadcastReceiver extends BroadcastReceiver {

    private final PublishSubject<Boolean> bluetoothTurnedOffSubject = PublishSubject.create();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                bluetoothTurnedOffSubject.onNext(true);
            }
        }
    }

    public Observable<Boolean> bluetoothStateChange() {
        return this.bluetoothTurnedOffSubject;
    }
}
