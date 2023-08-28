package com.apps2you.albaraka.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apps2you.albaraka.data.exception.NoInternetException;
import com.apps2you.albaraka.data.exception.ServerException;
import com.apps2you.albaraka.ui.exception.ExceptionMessageFactory;
import com.apps2you.albaraka.utils.lifecyle.Event;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public abstract class BaseViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final MutableLiveData<Boolean> _isContentLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isContentLoading = _isContentLoading;


    private final MutableLiveData<Exception> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>(false);

    // Exception types will be emitted in the error live-data
    private final Class<? extends Exception>[] emittedExceptions;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    private final MutableLiveData<Event<Integer>> _toastMessageResource = new MutableLiveData<>();
    public final LiveData<Event<Integer>> toastMessageResource = _toastMessageResource;

    private final MutableLiveData<Event<Boolean>> _hideKeyboard = new MutableLiveData<>();
    public final LiveData<Event<Boolean>> hideKeyboard = _hideKeyboard;


    public BaseViewModel() {
        initMediators();

        emittedExceptions = provideEmittedExceptionTypes();
    }

    private void initMediators() {
        error.observeForever(e -> { // Change isError value
            isError.setValue(e != null);
        });

        isLoading.observeForever(loading -> { // Remove current error while loading
            if (loading)
                clearError();
        });

        isContentLoading.observeForever(loading -> { // Remove current error while loading
            if (loading)
                clearError();
        });
    }

    public void clearError() {
        error.setValue(null);
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Exception>[] provideEmittedExceptionTypes() {
        return (Class<? extends Exception>[]) Arrays.asList(
                NoInternetException.class,
                TimeoutException.class,
                ServerException.class
        ).toArray();
    }

    protected void showMessage(String message) {
        _toastMessage.setValue(new Event<>(message));
    }

    protected void showMessage(int stringId) {
        if (stringId > 0)
            _toastMessageResource.setValue(new Event<>(stringId));
    }

    protected void showMessage(Throwable error) {
        if (error.getMessage() != null)
            showMessage(error.getMessage());
        else
            showMessage(ExceptionMessageFactory.getStringResOf((Exception) error));
    }

    protected void hideKeyboard() {
        _hideKeyboard.setValue(new Event<>(true));
    }

    public boolean isLoadingValue() {
        return isLoading.getValue() != null && isLoading.getValue();
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void startLoading() {
        isLoading.setValue(true);
    }

    public void stopLoading() {
        isLoading.setValue(false);
    }

    public void startContentLoading(){
        _isContentLoading.setValue(true);
    }

    public void stopContentLoading() {
        _isContentLoading.setValue(false);
    }

    public boolean isContentLoadingValue() {
        return _isContentLoading.getValue() != null && _isContentLoading.getValue();
    }


    public boolean isErrorValue() {
        return isError.getValue() != null && isError.getValue();
    }

    public LiveData<Boolean> isError() {
        return isError;
    }

    public LiveData<Exception> error() {
        return error;
    }

    public void setError(Exception e) {
        if (isExceptionForEmit(e))
            error.setValue(e);
        else
            showMessage(e);
    }

    private boolean isExceptionForEmit(Exception e) {
        if (emittedExceptions == null) // No types available.
            return false;

        // Check if the passed exception is in emittedExceptions.
        return Arrays.stream(emittedExceptions).anyMatch(type -> type.isInstance(e));
    }

}