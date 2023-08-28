package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.ui.base.BaseForm;

public class AddFavoriteAccountForm extends BaseForm {
    public final MutableLiveData<String> accountName = new MutableLiveData<>("");
    public final MutableLiveData<String>  accountCIF = new MutableLiveData<>("");
    public final MutableLiveData<String>  accountGSM = new MutableLiveData<>("");

    public AddFavoriteAccountForm() {
        addMutableLiveDataStringField(accountName, accountCIF, accountGSM);
    }
}
