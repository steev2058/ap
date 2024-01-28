package com.apps2you.albaraka.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.apps2you.albaraka.viewmodels.AboutViewModel;
import com.apps2you.albaraka.viewmodels.AccountVM;
import com.apps2you.albaraka.viewmodels.AtmViewModel;
import com.apps2you.albaraka.viewmodels.ComplaintViewModel;
import com.apps2you.albaraka.viewmodels.ConfirmPinViewModel;
import com.apps2you.albaraka.viewmodels.DepositCalculatorVM;
import com.apps2you.albaraka.viewmodels.ExchangeViewModel;
import com.apps2you.albaraka.viewmodels.FinancingCalculatorVM;
import com.apps2you.albaraka.viewmodels.HomeViewModel;
import com.apps2you.albaraka.viewmodels.LocationsVM;
import com.apps2you.albaraka.viewmodels.LoginViewModel;
import com.apps2you.albaraka.viewmodels.NotificationsViewModel;
import com.apps2you.albaraka.viewmodels.PrivacyPolicyVM;
import com.apps2you.albaraka.viewmodels.ProductsViewModel;
import com.apps2you.albaraka.viewmodels.ProfitsCalculatorVM;
import com.apps2you.albaraka.viewmodels.TransactionViewModel;
import com.apps2you.albaraka.viewmodels.TransactionsViewModel;
import com.apps2you.albaraka.viewmodels.transfer.ADSLViewModel;
import com.apps2you.albaraka.viewmodels.transfer.AlBarakaTransferViewModel;
import com.apps2you.albaraka.viewmodels.transfer.AlphaPaymentVM;
import com.apps2you.albaraka.viewmodels.transfer.BillsVM;
import com.apps2you.albaraka.viewmodels.transfer.FavoriteAccountsViewModel;
import com.apps2you.albaraka.viewmodels.transfer.HFViewModel;
import com.apps2you.albaraka.viewmodels.transfer.MyTransferViewModel;
import com.apps2you.albaraka.viewmodels.transfer.QrPaymentVM;
import com.apps2you.albaraka.viewmodels.transfer.SYGSTransferViewModel;
import com.apps2you.albaraka.viewmodels.transfer.SadakaViewModel;
import com.apps2you.albaraka.viewmodels.transfer.ZakatViewModel;
import com.apps2you.albaraka.viewmodels.transfer.payment.MobilePaymentViewModel;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.SchoolsPaymentViewModel;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.UniversitiesPaymentViewModel;
import com.apps2you.albaraka.viewmodels.transfer.payment.restaurants.RestaurantsPaymentViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindsViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindsHomeViewModel(HomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindsRegistrationViewModel(LoginViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccountVM.class)
    abstract ViewModel bindsAccountVM(AccountVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExchangeViewModel.class)
    abstract ViewModel bindsExchangeViewModel(ExchangeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TransactionsViewModel.class)
    abstract ViewModel bindsTransactionsViewModel(TransactionsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel.class)
    abstract ViewModel bindsAboutViewModel(AboutViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PrivacyPolicyVM.class)
    abstract ViewModel bindsPrivacyPolicyVM(PrivacyPolicyVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LocationsVM.class)
    abstract ViewModel bindsLocationsViewModel(LocationsVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ComplaintViewModel.class)
    abstract ViewModel bindsComplaintViewModel(ComplaintViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel.class)
    abstract ViewModel bindsTransactionViewModel(TransactionViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel.class)
    abstract ViewModel bindsNotificationsViewModel(NotificationsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProductsViewModel.class)
    abstract ViewModel bindsProductsViewModel(ProductsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AlBarakaTransferViewModel.class)
    abstract ViewModel bindsAlBarakaTransferViewModel(AlBarakaTransferViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SYGSTransferViewModel.class)
    abstract ViewModel bindsSYGSTransferViewModel(SYGSTransferViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ConfirmPinViewModel.class)
    abstract ViewModel bindsConfirmPinViewModel(ConfirmPinViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MyTransferViewModel.class)
    abstract ViewModel bindsMyTransferViewModel(MyTransferViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ZakatViewModel.class)
    abstract ViewModel bindsZakatViewModel(ZakatViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SadakaViewModel.class)
    abstract ViewModel bindsSadakaViewModel(SadakaViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ADSLViewModel.class)
    abstract ViewModel bindsADSLViewModel(ADSLViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HFViewModel.class)
    abstract ViewModel bindsHFViewModel(HFViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteAccountsViewModel.class)
    abstract ViewModel bindsFavoriteAccountsViewModel(FavoriteAccountsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SchoolsPaymentViewModel.class)
    abstract ViewModel bindsSchoolsPaymentViewModel(SchoolsPaymentViewModel viewModel);


    @Binds
    @IntoMap
    @ViewModelKey(UniversitiesPaymentViewModel.class)
    abstract ViewModel bindsUniversitiesPaymentViewModel(UniversitiesPaymentViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantsPaymentViewModel.class)
    abstract ViewModel bindsRestaurantsPaymentViewModel(RestaurantsPaymentViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MobilePaymentViewModel.class)
    abstract ViewModel bindsMobilePaymentViewModel(MobilePaymentViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BillsVM.class)
    abstract ViewModel bindsBillsViewModel(BillsVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FinancingCalculatorVM.class)
    abstract ViewModel bindsFinancingCalculatorVM(FinancingCalculatorVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfitsCalculatorVM.class)
    abstract ViewModel bindsProfitsCalculatorVM(ProfitsCalculatorVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DepositCalculatorVM.class)
    abstract ViewModel bindsDepositCalculatorVM(DepositCalculatorVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(QrPaymentVM.class)
    abstract ViewModel bindsQrPaymentVM(QrPaymentVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AlphaPaymentVM.class)
    abstract ViewModel bindsAlphaPaymentVM(AlphaPaymentVM viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AtmViewModel.class)
    abstract ViewModel bindsAtmViewModel(AtmViewModel viewModel);
}