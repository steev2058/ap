package com.apps2you.albaraka.viewmodels.transfer.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.SelectableItem;

import java.util.Collections;
import java.util.List;

public abstract class BaseSelectionViewModel<Model extends SelectableItem> extends TransferViewModel {
    private final MediatorLiveData<List<Model>> _modelList = new MediatorLiveData<>();

    private final MediatorLiveData<List<Model>> _filteredList = new MediatorLiveData<>();
    public final LiveData<List<Model>> dataList = _filteredList;

    private final MutableLiveData<Model> _selectedItem = new MutableLiveData<>();

    public final LiveData<Model> selectedItemLiveData = _selectedItem;

    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>("");

    public BaseSelectionViewModel(UserRepository userRepository, TransferRepository transferRepository) {
        super(userRepository, transferRepository);
        initSearchMediator();
        fetchData();
    }

    private void initSearchMediator() {
        _searchQuery.observeForever(this::filter);
    }

    public void applySearchQuery(String searchQuery) {
        String oldSearchQuery = _searchQuery.getValue();

        if (searchQuery == null)
            searchQuery = "";
        if (oldSearchQuery == null)
            oldSearchQuery = "";

        if (!oldSearchQuery.equals(searchQuery))
            _searchQuery.setValue(searchQuery);
    }

    public void fetchData() {
        //todo if data exist else fetch

        if (isContentLoadingValue()) {
            return;
        }
        if (isEmptyData()) {
            _filteredList.addSource(provideDataSource(),
                    resource -> {
                        stopContentLoading();
                        switch (resource.status) {
                            case LOADING:
                                startContentLoading();
                                break;
                            case ERROR:
                                setError(resource.error);
                                break;
                            case SUCCESS:
                                handleData(resource.data);
                                break;
                        }
                    });
        }
    }

    public void handleData(List<Model> data) {
        _modelList.setValue(data);
        checkSelected(selectedItemLiveData.getValue());
        filter(_searchQuery.getValue());
    }

    public void selectItem(Model item) {
        _selectedItem.setValue(item);
        checkSelected(item);
    }

    private void checkSelected(Model selectedItem) {
        if (_modelList.getValue() == null) return;
        _modelList.getValue().stream().forEach(listItem -> listItem.setIsSelected(false));

        if (selectedItem != null) {
            _modelList.getValue()
                    .stream()
                    .filter(
                            listItem -> listItem.getId() == selectedItem.getId()
                    )
                    .forEach(item -> item.setIsSelected(true));
        }
    }

    public Model getSelectedItem() {
        return _selectedItem.getValue();
    }

    private void filter(String searchQuery) {
        if (searchQuery != null && _modelList.getValue() != null) {
            _filteredList.setValue(
                    filter(
                            _modelList.getValue(),
                            searchQuery
                    )
            );
        } else {
            _filteredList.setValue(Collections.emptyList());
        }
    }

    public boolean isEmptyData() {
        return dataList.getValue() == null || dataList.getValue().isEmpty();
    }

    protected abstract List<Model> filter(List<Model> data, String searchQuery);

    protected abstract LiveData<Resource<List<Model>>> provideDataSource();
}
