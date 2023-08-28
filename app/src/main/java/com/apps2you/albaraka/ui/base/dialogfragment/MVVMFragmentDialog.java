package com.apps2you.albaraka.ui.base.dialogfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;

import javax.inject.Inject;

public abstract class MVVMFragmentDialog <VM extends BaseViewModel, DB extends ViewDataBinding> extends DaggerFragmentDialog<DB>  {
    protected VM viewModel;

    abstract protected Class<VM> getViewModelClass();

    abstract protected int getViewModelId();

    protected ViewModelStoreOwner getViewModelOwner() {
        return this;
    }

    @Inject
    ViewModelProvider.Factory factory;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = provideViewModel();
    }

    protected VM provideViewModel() {
        return new ViewModelProvider(getViewModelOwner(), factory).get(getViewModelClass());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupBaseObservers();
        return rootView;
    }


    protected void setupBaseObservers() {
        if (getViewModelId() > 0)
            binding.setVariable(getViewModelId(), viewModel);
        LifecycleOwner owner = getViewLifecycleOwner();
        viewModel.toastMessageResource.observe(owner, new EventObserver<>(this::showToast));
        viewModel.toastMessage.observe(owner, new EventObserver<>(this::showToast));
        viewModel.hideKeyboard.observe(owner, new EventObserver<>(ignored -> hideKeyboard()));
    }

}
