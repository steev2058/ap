package com.apps2you.albaraka.ui.transfer.base;

import androidx.appcompat.widget.SearchView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.FragmentItemSelectionBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.common.model.SelectableItem;
import com.apps2you.albaraka.ui.transfer.sadaka.charities.ItemSelectionListAdapter;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

public abstract class BaseSelectionFragment<Model extends SelectableItem, VM extends BaseSelectionViewModel<Model>> extends BaseFragment<FragmentItemSelectionBinding, VM> {
    protected ItemSelectionListAdapter<Model> itemSelectionAdapter;

    @Override
    public void setUpView() {
        mViewDataBinding.setTitle(provideTitle());
        setUpSearchView();
        setUpRecycler();
    }

    private void setUpSearchView() {
        mViewModel.applySearchQuery("");
        mViewDataBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mViewModel.applySearchQuery(query);
                return true;
            }
        });
    }

    private final OnItemClickListener<Model> onItemSelected = (item, position) -> {
        mViewModel.selectItem(item);
        navController.popBackStack();
    };

    private void setUpRecycler() {
        itemSelectionAdapter = new ItemSelectionListAdapter<>(requireContext(), onItemSelected);
        mViewDataBinding.recyclerViewItems.setAdapter(itemSelectionAdapter);
    }

    @Override
    public void fetchData() {
        if (mViewModel.isEmptyData()){
            mViewModel.fetchData();
        }

        mViewModel.dataList.observe(getViewLifecycleOwner(), data -> {
            itemSelectionAdapter.submitData(data);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewModel.stopContentLoading();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_item_selection;
    }

    protected abstract String provideTitle();
}
