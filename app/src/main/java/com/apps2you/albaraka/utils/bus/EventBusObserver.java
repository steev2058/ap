package com.apps2you.albaraka.utils.bus;

import io.reactivex.functions.Consumer;

public interface EventBusObserver<T extends BusEvent> extends Consumer<T> {
}
