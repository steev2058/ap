package com.apps2you.albaraka.di.builder;

import com.apps2you.albaraka.ui.PrivacyPolicyActivity;
import com.apps2you.albaraka.ui.SplashActivity;
import com.apps2you.albaraka.ui.about.AboutActivity;
import com.apps2you.albaraka.ui.about.AboutProvider;
import com.apps2you.albaraka.ui.account.AccountActivity;
import com.apps2you.albaraka.ui.account.AccountProvider;
import com.apps2you.albaraka.ui.atmCard.AtmCardsActivity;
import com.apps2you.albaraka.ui.atmCard.AtmCardsProvider;
import com.apps2you.albaraka.ui.atmForm.ATMFormActivity;
import com.apps2you.albaraka.ui.atmForm.ATMFormProvider;
import com.apps2you.albaraka.ui.calculator.CalculatorActivity;
import com.apps2you.albaraka.ui.calculator.CalculatorProvider;
import com.apps2you.albaraka.ui.complaints.ComplaintActivity;
import com.apps2you.albaraka.ui.complaints.ComplaintProvider;
import com.apps2you.albaraka.ui.exchange.ExchangeActivity;
import com.apps2you.albaraka.ui.exchange.ExchangeProvider;
import com.apps2you.albaraka.ui.home.GuestHomeActivity;
import com.apps2you.albaraka.ui.home.HomeActivity;
import com.apps2you.albaraka.ui.home.HomeProvider;
import com.apps2you.albaraka.ui.kyc.KycActivity;
import com.apps2you.albaraka.ui.kyc.KycProvider;
import com.apps2you.albaraka.ui.locations.LocationsActivity;
import com.apps2you.albaraka.ui.mobForm.MobFormActivity;
import com.apps2you.albaraka.ui.mobForm.MobFormProvider;
import com.apps2you.albaraka.ui.notifications.NotificationsActivity;
import com.apps2you.albaraka.ui.notifications.NotificationsProvider;
import com.apps2you.albaraka.ui.products.ProductDetailsActivity;
import com.apps2you.albaraka.ui.products.ProductDetailsProvider;
import com.apps2you.albaraka.ui.products.ServicesActivity;
import com.apps2you.albaraka.ui.products.ServicesProvider;
import com.apps2you.albaraka.ui.quick_services.PersonalizeQuickServicesActivity;
import com.apps2you.albaraka.ui.quick_services.QuickServicesProvider;
import com.apps2you.albaraka.ui.registration.LoginActivity;
import com.apps2you.albaraka.ui.registration.LoginProvider;
import com.apps2you.albaraka.ui.reset_pass_form.ResetPassFormActivity;
import com.apps2you.albaraka.ui.reset_pass_form.ResetPassFormProvider;
import com.apps2you.albaraka.ui.settings.SettingsActivity;
import com.apps2you.albaraka.ui.settings.SettingsActivityTwo;
import com.apps2you.albaraka.ui.settings.SettingsProvider;
import com.apps2you.albaraka.ui.transactions.TransactionDetailsActivity;
import com.apps2you.albaraka.ui.transactions.TransactionsActivity;
import com.apps2you.albaraka.ui.transactions.TransactionsProvider;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.TransferActivity;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSActivity;
import com.apps2you.albaraka.ui.transfer.adsl.ADSLActivity;
import com.apps2you.albaraka.ui.transfer.alphaCapital.AlphaPaymentActivity;
import com.apps2you.albaraka.ui.transfer.bills.BillsActivity;
import com.apps2you.albaraka.ui.transfer.di.TransferProvider;
import com.apps2you.albaraka.ui.transfer.hf.HFActivity;
import com.apps2you.albaraka.ui.transfer.payment.education.schools.SchoolsPaymentActivity;
import com.apps2you.albaraka.ui.transfer.payment.education.universities.UniversitiesPaymentActivity;
import com.apps2you.albaraka.ui.transfer.payment.mobile.MobilePaymentActivity;
import com.apps2you.albaraka.ui.transfer.payment.restaurants.RestaurantsPaymentActivity;
import com.apps2you.albaraka.ui.transfer.qrPayment.QrPaymentActivity;
import com.apps2you.albaraka.ui.transfer.sadaka.SadakaActivity;
import com.apps2you.albaraka.ui.transfer.zakat.ZakatActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector()
    abstract SplashActivity splashActivity();

    @ContributesAndroidInjector(modules = {HomeProvider.class})
    abstract HomeActivity homeActivity();

    @ContributesAndroidInjector(modules = {HomeProvider.class})
    abstract GuestHomeActivity guestHomeActivity();

    @ContributesAndroidInjector(modules = {LoginProvider.class})
    abstract LoginActivity registrationActivity();

    @ContributesAndroidInjector(modules = {AccountProvider.class})
    abstract AccountActivity accountActivity();

    @ContributesAndroidInjector(modules = {ExchangeProvider.class})
    abstract ExchangeActivity exchangeActivity();

    @ContributesAndroidInjector(modules = {TransactionsProvider.class})
    abstract TransactionsActivity transactionsActivity();

    @ContributesAndroidInjector(modules = {AboutProvider.class})
    abstract AboutActivity aboutActivity();

    @ContributesAndroidInjector
    abstract PrivacyPolicyActivity privacyPolicyActivity();

    @ContributesAndroidInjector
    abstract LocationsActivity locationsActivity();

    @ContributesAndroidInjector(modules = {ComplaintProvider.class})
    abstract ComplaintActivity complaintActivity();

    @ContributesAndroidInjector(modules = {KycProvider.class})
    abstract KycActivity kycActivity();
    @ContributesAndroidInjector(modules = {MobFormProvider.class})
    abstract MobFormActivity mobFormActivity();

    @ContributesAndroidInjector(modules = {ATMFormProvider.class})
    abstract ATMFormActivity atmFormActivity();
    @ContributesAndroidInjector(modules = {ResetPassFormProvider.class})
    abstract ResetPassFormActivity resetPassFormActivity();
    @ContributesAndroidInjector(modules = {TransactionsProvider.class})
    abstract TransactionDetailsActivity transactionDetailsActivity();


    @ContributesAndroidInjector(modules = {SettingsProvider.class})
    abstract SettingsActivity settingsActivity();

    @ContributesAndroidInjector(modules = {SettingsProvider.class})
    abstract SettingsActivityTwo settingsActivityTwo();

    @ContributesAndroidInjector(modules = {NotificationsProvider.class})
    abstract NotificationsActivity notificationsActivity();

    @ContributesAndroidInjector(modules = {ProductDetailsProvider.class})
    abstract ProductDetailsActivity productDetailsActivity();

    @ContributesAndroidInjector(modules = {ServicesProvider.class})
    abstract ServicesActivity servicesActivity();

    @ContributesAndroidInjector(modules = {QuickServicesProvider.class})
    abstract PersonalizeQuickServicesActivity personalizeQuickServicesActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract TransferActivity transferActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract ZakatActivity zakatActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract SadakaActivity sadakaActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract SYGSActivity sygsActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract ADSLActivity adslActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract HFActivity hfActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract UniversitiesPaymentActivity universitiesPaymentActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract SchoolsPaymentActivity schoolsPaymentActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract RestaurantsPaymentActivity restaurantsPaymentActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract MobilePaymentActivity mobilePaymentActivity();

    @ContributesAndroidInjector
    abstract BillsActivity billsActivity();

    @ContributesAndroidInjector(modules = {CalculatorProvider.class})
    abstract CalculatorActivity calculatorActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract QrPaymentActivity qrPaymentActivity();

    @ContributesAndroidInjector(modules = {TransferProvider.class})
    abstract AlphaPaymentActivity alphaPaymentActivity();

    @ContributesAndroidInjector(modules = {AtmCardsProvider.class})
    abstract AtmCardsActivity atmCardsActivity();
}