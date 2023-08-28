package com.apps2you.albaraka.ui.transfer.accountsTransfer.myTransfer;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;

public class MyTransferForm extends BaseTransferForm {

    public MyTransferForm() {
        mutableLiveDataFields.remove(reason);
    }
}
