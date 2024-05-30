package com.apps2you.albaraka.ui.transfer.di;

import com.apps2you.albaraka.ui.common.dialogs.ConfirmPinDialog;
import com.apps2you.albaraka.ui.sep.SEPFragment;
import com.apps2you.albaraka.ui.sep.SEPProvidersFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.TransferOptionsFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.AlBarakaTransferFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.FavoriteAccountsFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs.AddFavoriteAccountDialog;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs.UpdateFavoriteAccountDialog;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.myTransfer.MyTransferFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.BanksFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSTransferFragment;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSTransferPreviewDialog;
import com.apps2you.albaraka.ui.transfer.adsl.ADSLFragment;
import com.apps2you.albaraka.ui.transfer.adsl.ADSLProvidersFragment;
import com.apps2you.albaraka.ui.transfer.alphaCapital.AlphaPaymentFragment;
import com.apps2you.albaraka.ui.transfer.payment.education.schools.SchoolSelectionFragment;
import com.apps2you.albaraka.ui.transfer.payment.education.schools.SchoolsPaymentFragment;
import com.apps2you.albaraka.ui.transfer.payment.education.universities.UniversitiesPaymentFragment;
import com.apps2you.albaraka.ui.transfer.payment.education.universities.UniversitySelectionFragment;
import com.apps2you.albaraka.ui.transfer.payment.mobile.MobilePaymentFragment;
import com.apps2you.albaraka.ui.transfer.payment.mobile.TransferPreviewDialog;
import com.apps2you.albaraka.ui.transfer.payment.restaurants.RestaurantSelectionFragment;
import com.apps2you.albaraka.ui.transfer.payment.restaurants.RestaurantsPaymentFragment;
import com.apps2you.albaraka.ui.transfer.qrPayment.QrFormFragment;
import com.apps2you.albaraka.ui.transfer.qrPayment.QrScanFragment;
import com.apps2you.albaraka.ui.transfer.sadaka.SadakaFragment;
import com.apps2you.albaraka.ui.transfer.sadaka.charities.CharitiesFragment;
import com.apps2you.albaraka.ui.transfer.zakat.ZakatFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class TransferProvider {

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract TransferOptionsFragment provideTransferOptionsFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract AlBarakaTransferFragment provideAlBarakaTransferFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract MyTransferFragment provideMyFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract ZakatFragment provideZakatFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SadakaFragment provideSadakaFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract ADSLFragment provideADSLFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SEPFragment provideSEPFragment();



    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract CharitiesFragment provideCharitiesFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract ADSLProvidersFragment provideADSLProvidersFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SEPProvidersFragment provideSEPProvidersFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract FavoriteAccountsFragment provideFavoriteAccountsFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract UniversitiesPaymentFragment provideUniversitiesPaymentFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SchoolsPaymentFragment provideSchoolPaymentFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract UniversitySelectionFragment provideUniversitiesSelectionFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SchoolSelectionFragment provideSchoolSelectionFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract RestaurantsPaymentFragment provideRestaurantsPaymentFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract RestaurantSelectionFragment provideRestaurantSelectionFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract MobilePaymentFragment provideMobilePaymentFragmentFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SYGSTransferFragment provideSYGSTransferFragment();


    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract BanksFragment provideBanksFragment();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract ConfirmPinDialog provideConfirmPinDialog();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract AddFavoriteAccountDialog provideAddFavoriteAccountDialog();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract UpdateFavoriteAccountDialog provideUpdateFavoriteAccountDialog();

    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract TransferPreviewDialog provideTransferPreviewDialog();


    @ContributesAndroidInjector(modules = {TransferModule.class})
    abstract SYGSTransferPreviewDialog provideSYGSTransferPreviewDialog();

    @ContributesAndroidInjector
    abstract QrScanFragment qrScanFragment();

    @ContributesAndroidInjector
    abstract QrFormFragment qrFormFragment();
    
    @ContributesAndroidInjector
    abstract AlphaPaymentFragment alphaPaymentFragment();
}
