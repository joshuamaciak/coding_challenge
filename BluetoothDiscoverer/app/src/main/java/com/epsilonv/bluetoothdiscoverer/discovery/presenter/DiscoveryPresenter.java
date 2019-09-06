package com.epsilonv.bluetoothdiscoverer.discovery.presenter;

import com.epsilonv.bluetoothdiscoverer.discovery.model.DiscoveredDevice;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * This layer handles all the business logic in order to prevent coupling between the view & higher
 * level logic. Ideally the fragment's only concern should be controlling what the user sees &
 * bind data from the presenter to the physical view.
 */
public class DiscoveryPresenter {
    private PublishSubject<DiscoveredDevice> broadcastDevices = PublishSubject.create();
    private CompositeDisposable disposables = new CompositeDisposable();

    public Observable<List<DiscoveredDevice>> discoveredDevices() {
        return this.broadcastDevices.scan(new ArrayList<>(), (devices, device) -> {
            if (!devices.contains(device)) {
                devices.add(device);
            }
            return devices;
        });
    }

    public void observeDiscovery(final Observable<DiscoveredDevice> discoveredDevices) {
        final Disposable d = discoveredDevices.subscribe(dev -> this.broadcastDevices.onNext(dev),
                e -> this.broadcastDevices.onError(e));
        this.disposables.add(d);
    }

    public void cleanup() {
        if(!disposables.isDisposed()) {
            disposables.dispose();
        }
    }


}
