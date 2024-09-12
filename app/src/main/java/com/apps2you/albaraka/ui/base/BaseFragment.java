package com.apps2you.albaraka.ui.base;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.base.alert.IDialogAlert;
import com.apps2you.albaraka.ui.common.dialogs.DataRetrievalErrorDialog;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.utils.navigation.ActivityNavigation;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends DaggerFragment implements IBaseView {

    protected BaseActivity mActivity;
    protected T mViewDataBinding;
    protected V mViewModel;
    private SweetAlertDialog progressDialog;

    protected NavController navController;


    @Inject
    ViewModelProvider.Factory factory;

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    @LayoutRes
    public abstract int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract Class<V> setViewModel();

    /**
     * set up view and any necessary
     * binding or setting any views
     * for fetching the data.
     */
    public abstract void setUpView();

    /**
     * fetch data
     */
    public abstract void fetchData();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //performDependencyInjection();
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);

        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
    //    progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        progressDialog.setContentText(getString(R.string.loading));
        progressDialog.setCancelable(false);

    }

    public void createViewModel() {
        mViewModel = new ViewModelProvider(getViewModelOwner(), factory).get(setViewModel());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);

        setBackButtonAction(mViewDataBinding.getRoot());

        return mViewDataBinding.getRoot();
    }

    private void setBackButtonAction(View view) {
        try {
            view.findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            navController = NavHostFragment.findNavController(this);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createViewModel();
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.setLifecycleOwner(getViewLifecycleOwner());
        mViewDataBinding.executePendingBindings();

        setupBaseObservers();
        setUpView();
        fetchData();
    }

    protected void setupBaseObservers() {
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.toastMessage.observe(owner, new EventObserver<>(this::showToast));
        mViewModel.toastMessageResource.observe(owner, new EventObserver<>(stringRes -> showToast(getString(stringRes))));
        mViewModel.hideKeyboard.observe(owner, new EventObserver<>(ignored -> hideKeyboard()));
        mViewModel.error().observe(owner, this::showErrorDialog);
        mViewModel.isLoading().observe(owner, isLoading -> {
            if (isLoading) {
                hideKeyboard();
                showProgress();
            } else {
                hideProgress();
            }
        });
    }

    @Override
    public IDialogAlert provideDialogAlert() {
        if (requireActivity() instanceof BaseActivity)
            return ((BaseActivity) requireActivity()).provideDialogAlert();
        return null;
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    public V getViewModel() {
        return mViewModel;
    }

    protected ViewModelStoreOwner getViewModelOwner() {
        return this;
    }

    protected void setInputError(TextInputLayout inputLayout, String message) {
        inputLayout.setError(message);
        inputLayout.requestFocus();
    }

    public ActivityNavigation getActivityNavigation(){
        if (mActivity != null){
            return mActivity.getActivityNavigator();
        }
        return ActivityNavigation.create(requireContext());
    }

    protected void onBackPressed() {
        requireActivity().onBackPressed();
    }

    public void showToast(final String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes final int stringRes) {
        showToast(getString(stringRes));
    }

    public void hideKeyboard() {
        if (requireActivity() instanceof BaseActivity)
            ((BaseActivity) requireActivity()).hideKeyboard();
    }

    public void showErrorDialog(Exception error) {
        if (error != null)
            DataRetrievalErrorDialog.show(error, view -> refresh(), requireActivity().getSupportFragmentManager());
    }

    public void refresh() {
        fetchData();
    }

    protected void showProgress() {
        progressDialog.show();
    }

    protected void hideProgress() {
        progressDialog.dismiss();
    }
}
