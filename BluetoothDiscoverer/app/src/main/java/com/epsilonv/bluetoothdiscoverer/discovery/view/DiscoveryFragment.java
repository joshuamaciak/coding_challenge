package com.epsilonv.bluetoothdiscoverer.discovery.view;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.epsilonv.bluetoothdiscoverer.R;
import com.epsilonv.bluetoothdiscoverer.discovery.BluetoothDiscoveryBroadcastReceiver;
import com.epsilonv.bluetoothdiscoverer.discovery.BluetoothTurnedOffBroadcastReceiver;
import com.epsilonv.bluetoothdiscoverer.discovery.presenter.DiscoveryPresenter;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.core.content.ContextCompat.checkSelfPermission;


/**
 * This is the main view for viewing bluetooth devices while in discovery mode.
 */
public class DiscoveryFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 1597;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1596;

    private final DiscoveryPresenter presenter = new DiscoveryPresenter();

    private final BluetoothDiscoveryBroadcastReceiver bluetoothDiscoveryBroadcastReceiver = new BluetoothDiscoveryBroadcastReceiver();
    private final BluetoothTurnedOffBroadcastReceiver bluetoothTurnedOffBroadcastReceiver = new BluetoothTurnedOffBroadcastReceiver();

    private final DiscoveredDevicesRecyclerViewAdapter discoveredDevicesRecyclerViewAdapter = new DiscoveredDevicesRecyclerViewAdapter();

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.observeDiscovery(bluetoothDiscoveryBroadcastReceiver.discoveredDevices());

        final Disposable d = presenter.discoveredDevices().subscribe(discoveredDevicesRecyclerViewAdapter::setDevices);
        disposables.add(d);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        final Context context = requireContext();

        final RecyclerView discoveredDevicesRecyclerView = view.findViewById(R.id.discoveredDevicesRecyclerView);
        discoveredDevicesRecyclerView.setAdapter(discoveredDevicesRecyclerViewAdapter);
        discoveredDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // run broadcast receivers
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(bluetoothDiscoveryBroadcastReceiver, filter);
        final IntentFilter stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothTurnedOffBroadcastReceiver, stateChangedFilter);


        final Disposable d = bluetoothTurnedOffBroadcastReceiver.bluetoothStateChange().subscribe(off -> {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        });
        disposables.add(d);

        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
        } else {
            startBluetooth();
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION: {
                if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
                    startBluetooth();
                } else {
                    // never do this in production but for now we'll go into a dialog loop
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ENABLE_BLUETOOTH: {
                if (resultCode == RESULT_OK) {
                    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    bluetoothAdapter.startDiscovery();
                } else {
                    Toast.makeText(requireContext(), R.string.denied_bluetooth_enable_message, Toast.LENGTH_SHORT).show();
                    showBluetoothEnableDialog();
                }
                break;
            }
        }
    }

    private void showBluetoothEnableDialog() {
        final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    void startBluetooth() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth is unsupported", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!bluetoothAdapter.isEnabled()) {
            showBluetoothEnableDialog();
        } else {
            bluetoothAdapter.startDiscovery();
        }

    }

    @Override
     public void onDestroy() {
        super.onDestroy();
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
        presenter.cleanup();
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
        requireContext().unregisterReceiver(bluetoothDiscoveryBroadcastReceiver);
        requireContext().unregisterReceiver(bluetoothTurnedOffBroadcastReceiver);

    }
}
