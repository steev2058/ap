package com.apps2you.albaraka.utils.bus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * CreatedBy: Abdulrahman-Tayara
 * Date: 05/2020
 */

public final class Bus {
    private static final Bus INSTANCE = new Bus();

    private final PublishSubject<BusEvent> publisher = PublishSubject.create();

    private final Map<Consumer<?>, Disposable> disposables = new HashMap<>();

    public static Bus instance() {
        return INSTANCE;
    }

    private Bus() {
    }

    public <T extends BusEvent> void publish(T value) {
        publisher.onNext(value);
    }

    public <T extends BusEvent> void register(Class<T> type, EventBusObserver<T> observer, boolean observeOnMainThread) {
        if (disposables.containsKey(observer))
            return;
        Disposable disposable = publisher.ofType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(observeOnMainThread ? AndroidSchedulers.mainThread() : Schedulers.io())
                .subscribe(observer);
        disposables.put(observer, disposable);
    }

    public <T extends BusEvent> void register(Class<T> type, EventBusObserver<T> observer) {
        register(type, observer, false);
    }

    public <T extends BusEvent> void unregister(EventBusObserver<T> observer) {
        Disposable disposable = disposables.get(observer);
        disposables.remove(observer);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
