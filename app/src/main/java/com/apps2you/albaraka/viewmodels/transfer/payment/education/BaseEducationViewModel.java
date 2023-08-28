package com.apps2you.albaraka.viewmodels.transfer.payment.education;

import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.SelectableItem;
import com.apps2you.albaraka.ui.transfer.payment.education.EducationForm;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

public abstract class BaseEducationViewModel<Model extends SelectableItem> extends BaseSelectionViewModel<Model> {
    public final EducationForm educationForm = new EducationForm();

    public BaseEducationViewModel(UserRepository userRepository,
                                  TransferRepository transferRepository) {
        super(userRepository, transferRepository);
    }
}
