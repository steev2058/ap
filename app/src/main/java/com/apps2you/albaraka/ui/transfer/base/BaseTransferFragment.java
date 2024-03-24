package com.apps2you.albaraka.ui.transfer.base;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.model.TransferData;
import com.apps2you.albaraka.databinding.DialogConfirmBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.common.adapters.AccountsRecyclerAdapter;
import com.apps2you.albaraka.ui.common.busEvent.RefreshAccountsEvent;
import com.apps2you.albaraka.ui.common.dialogs.ConfirmPinDialog;
import com.apps2you.albaraka.ui.home.HomeActivity;
import com.apps2you.albaraka.ui.transactions.TransactionDetailsActivity;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.TransferActivity;
import com.apps2you.albaraka.utils.BindingUtils;
import com.apps2you.albaraka.utils.bus.Bus;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.utils.navigation.ActivityResultObserver;
import com.apps2you.albaraka.utils.navigation.NavigationUtil;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static android.Manifest.permission.READ_CONTACTS;

public abstract class BaseTransferFragment<DB extends ViewDataBinding, VM extends TransferViewModel> extends BaseFragment<DB, VM> {
    private static final String KEY_PERMISSION = "key_permission";
    private ActivityResultObserver<String, Boolean> permissionsObserver;

    private AccountsRecyclerAdapter accountsRecyclerAdapter;

    private final OnItemClickListener<Account> onAccountClickListener = (item, position) -> {
        onAccountSelected(item);
        mViewModel.setSelectedAccount(item);
    };

    @Override
    public void onViewCreated(@NonNull @NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permissionsObserver = new ActivityResultObserver<>(requireActivity().getActivityResultRegistry(), new ActivityResultContracts.RequestPermission(), KEY_PERMISSION);
        getLifecycle().addObserver(permissionsObserver);
    }

    @Override
    public void setUpView() {
        setUpAccountsRecycler();
        registerObservers();
    }
    String title = "";
    @Override
    public void fetchData() {
        mViewModel.commissionFetched
                .observe(
                        getViewLifecycleOwner(),
                        new EventObserver<>(result -> {
                            if (result) feeConfirmationDialog(title,mViewModel.commission.toString(),mViewModel.toFullName);
                        })
                );
    }

    @Override
    public void refresh() {
        super.refresh();

        mViewModel.fetchAccounts();
        mViewModel.fetchFees();
    }

    private void setUpAccountsRecycler() {
        RecyclerView accountsRecycler = provideAccountsRecycler();
        accountsRecyclerAdapter = new AccountsRecyclerAdapter(requireContext(), onAccountClickListener);
        accountsRecycler.setAdapter(accountsRecyclerAdapter);
    }

    private void registerObservers() {
        mViewModel.transferStatus.observe(getViewLifecycleOwner(), new EventObserver<>(transaction -> {
            if (transaction != null) {
                Bus.instance().publish(RefreshAccountsEvent.getInstance());
                openTransactionsDetailsActivity(transaction);
            }
        }));

        mViewModel.accountList.observe(getViewLifecycleOwner(), accountList -> {
            if (accountList != null)
                handleAccountsData(accountList);
        });

        mViewModel.fees.observe(getViewLifecycleOwner(), new Observer<List<TransferData>>() {
            @Override
            public void onChanged(List<TransferData> transferData) {
                // it crashes with lambda
            }
        });
    }

    protected void openTransactionsDetailsActivity(Transaction transaction) {
        Intent intent = TransactionDetailsActivity.getIntent(requireContext(), transaction.getCode(), transaction.getBranchCode(), true);

        getActivityNavigation().navigateForResult(intent,
                activityResult -> {
                    if (activityResult.getResultCode() == Activity.RESULT_OK && activityResult.getData() != null) {
                        boolean refresh = activityResult.getData().getBooleanExtra(TransactionDetailsActivity.RESULT_DO_ANOTHER_TRANSFER, false);
                        if (refresh) {
                            // we need to recreate the transfer form
                            // first condition works for MyTransferFragment and AlBarakaTransferFragment
                            // the else works for the rest of the transfer fragments
                            // which extends this fragment
                            if (navController != null
                                    && navController.getCurrentDestination().getId() != navController.getGraph().getStartDestination()) {
                                // get the destination id before we pop it up
                                int currentDest = navController.getCurrentDestination().getId();
                                navController.popBackStack();
                                navController.navigate(currentDest);
                            } else {
                                Intent intent2 = new Intent(requireContext(), getActivity().getClass());
                                startActivity(intent2);
                                requireActivity().finish();
                            }
                        } else {
                            redirectToHome();
                        }
                    } else {
                        redirectToHome();
                    }
                });
    }


    protected void handleAccountsData(List<Account> accountList) {
        List<Account> accountsCopy =
                accountList
                        .stream().filter(Account::isAllowFrom).collect(Collectors.toList());

        accountsRecyclerAdapter.submitData(accountsCopy);
        selectAccount();
    }


    private void selectAccount() {
        String preSelectedAccountNumber = requireActivity().getIntent().getStringExtra(TransferActivity.EXTRA_SELECTED_ACCOUNT_ID);

        if (accountsRecyclerAdapter.getItemCount() > 0) {
            if (preSelectedAccountNumber == null && mViewModel.selectedAccount.getValue() == null && mViewModel.getLastSelectedAccountId() == null) {
                onAccountClickListener.onClick(accountsRecyclerAdapter.getData().get(0), 0);
            } else {
                Stream<Account> selectedAccountStream = accountsRecyclerAdapter.getData().stream();
                Account selectedAccount;

                if (mViewModel.selectedAccount.getValue() != null) {
                    selectedAccount = selectedAccountStream.filter(account -> account.getNumber().equals(mViewModel.selectedAccount.getValue().getNumber()))
                            .findFirst().orElse(accountsRecyclerAdapter.getData().get(0));
                    mViewModel.setSelectedAccount(null);
                } else if (mViewModel.getLastSelectedAccountId() != null) {
                    selectedAccount = selectedAccountStream.filter(account -> account.getNumber().equals(mViewModel.getLastSelectedAccountId()))
                            .findFirst().orElse(accountsRecyclerAdapter.getData().get(0));
                } else {
                    selectedAccount = selectedAccountStream.filter(account -> account.getNumber().equals(preSelectedAccountNumber))
                            .findFirst().orElse(accountsRecyclerAdapter.getData().get(0));
                }

                onAccountClickListener.onClick(selectedAccount, accountsRecyclerAdapter.getData().indexOf(selectedAccount));
            }
        }
    }

    protected void pickContact() {
        if (ContextCompat.checkSelfPermission(requireContext(), READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            showDialogMessage(
                    getString(R.string.contacts_permission),
                    getString(R.string.msg_contacts_permission),
                    getString(R.string.action_ok),
                    getString(R.string.cancel),
                    (dialog, id) ->
                            permissionsObserver.launch(READ_CONTACTS, result -> {
                                if (result) navigateToContacts();
                            }),
                    (dialog, id) -> dialog.dismiss(),
                    false
            );
        } else {
            navigateToContacts();
        }
    }

    private void navigateToContacts() {
        MyApplication.skipQuit = true;

        getActivityNavigation().navigateForResult(
                NavigationUtil.pickContactIntent(),
                this::handlePickContactActivityResult
        );
    }

    private void redirectToHome() {
        getActivityNavigation().finishAffinity().navigate(new Intent(getContext(), HomeActivity.class));
    }

    protected void handlePickContactActivityResult(ActivityResult activityResult) {
        Intent intent = activityResult.getData();
        MyApplication.skipQuit = false;
        if (activityResult.getResultCode() == Activity.RESULT_OK && intent != null) {
            try {
                Uri contactUri = intent.getData();
                long contactId = getContactIdFromUri(contactUri);
                List<String> phones = getPhonesFromContactId(contactId);

                if (phones.isEmpty()) {
                    showToast(R.string.failed_reading_selected_phone_number);
                } else if (phones.size() == 1) {
                    onContactSelected(phones.get(0));
                } else {
                    showPhonesDialog(phones);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.failed_reading_selected_phone_number);
            }
        }
    }

    private long getContactIdFromUri(Uri contactUri) {
        Cursor cur = requireContext()
                .getContentResolver()
                .query(
                        contactUri,
                        new String[]{ContactsContract.Contacts._ID},
                        null, null, null
                );
        long id = -1;
        if (cur.moveToFirst()) {
            id = cur.getLong(0);
        }
        cur.close();
        return id;
    }

    private List<String> getPhonesFromContactId(long contactId) {
        Cursor cur = requireContext()
                .getContentResolver()
                .query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{String.valueOf(contactId)}, null
                );
        List<String> phones = new ArrayList<>();
        while (cur.moveToNext()) {
            String phone = cur.getString(0);
            phones.add(phone);
        }
        cur.close();
        return phones;
    }

    private void showPhonesDialog(List<String> phones) {
        String[] phonesArr = phones.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.select_phone_number));

        builder.setItems(phonesArr, (dialog, which) -> onContactSelected(phonesArr[which]));

        builder.show();
    }

    protected void onContactSelected(String number) {

    }

    protected void openConfirmPinDialog() {
        ConfirmPinDialog.show(getChildFragmentManager(), (pinCode) -> {
            mViewModel.setPinCode(pinCode);
            mViewModel.transfer();
        });
    }

    protected abstract RecyclerView provideAccountsRecycler();

    protected void onAccountSelected(Account account) {
        accountsRecyclerAdapter.removeItem(account);

        if (mViewModel.selectedAccount.getValue() != null) {
            accountsRecyclerAdapter.insertItem(mViewModel.selectedAccount.getValue());
        }
    }

    protected boolean thereIsEnoughBalance(String amount) {
        Account account = mViewModel.selectedAccount.getValue();
        if (account == null)
            return false;
        return thereIsEnoughBalance(amount, account.getBalance());
    }

    protected boolean thereIsEnoughBalance(String amount, BigDecimal balance) {
        if (TextUtils.isEmpty(amount) || balance == null) {
            return false;
        }

        return balance.compareTo(getAmountValue(amount)) >= 0;
    }

    protected boolean thereIsEnoughBalance(String... amounts) {
        BigDecimal totalAmount = new BigDecimal("0.0");

        for (String amount : amounts) {
            BigDecimal amountValue = getAmountValue(amount);
            totalAmount = totalAmount.add(amountValue);
        }

        return thereIsEnoughBalance(totalAmount.toString());
    }

    protected BigDecimal getAmountValue(String amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.ENGLISH);
        try {
            Number number = formatter.parse(amount);
            return BigDecimal.valueOf(Objects.requireNonNull(number).doubleValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new BigDecimal("0.0");
    }

    protected void nextStep(String title1) {
        title = title1;
        if (getViewModel().getTransferFee() > 0) // fee might be 0 or -1
            feeConfirmationDialog(title);
        else if (getViewModel().getTransferFee() == -1) // fee might be 0 or -1
            mViewModel.calculateCommission();
        else
            openConfirmPinDialog();
    }

    private void feeConfirmationDialog(String title,String fee) {
        Dialog dialog = new Dialog(requireContext());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
           // dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        DialogConfirmBinding dialogDataBinding = DialogConfirmBinding.inflate(LayoutInflater.from(requireContext()),
                null,
                false);

        dialog.setContentView(dialogDataBinding.getRoot());
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.textView, title);
        dialogDataBinding.textView.setText(title);
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.text, getString(R.string.commission_question, fee));//String.valueOf(getViewModel().getTransferFee())
        dialogDataBinding.text.setText(getString(R.string.commission_question, fee));//String.valueOf(getViewModel().getTransferFee())

        dialogDataBinding.btnOk.setVisibility(View.GONE);

        dialog.show();

        dialogDataBinding.buttonSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            openConfirmPinDialog();
        });

        dialogDataBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void feeConfirmationDialog(String title,String fee,String name) {
        Dialog dialog = new Dialog(requireContext());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
           // dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        DialogConfirmBinding dialogDataBinding = DialogConfirmBinding.inflate(LayoutInflater.from(requireContext()),
                null,
                false);

        dialog.setContentView(dialogDataBinding.getRoot());
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.textView, title);
        dialogDataBinding.textView.setText(title);
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.text, getString(R.string.commission_question_baraka_trans, fee,name));//String.valueOf(getViewModel().getTransferFee())
        dialogDataBinding.text.setText(getString(R.string.commission_question_baraka_trans, fee,name));//String.valueOf(getViewModel().getTransferFee())

        dialogDataBinding.btnOk.setVisibility(View.GONE);

        dialog.show();

        dialogDataBinding.buttonSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            openConfirmPinDialog();
        });

        dialogDataBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void feeConfirmationDialog(String title) {
        Dialog dialog = new Dialog(requireContext());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        DialogConfirmBinding dialogDataBinding = DialogConfirmBinding.inflate(LayoutInflater.from(requireContext()),
                null,
                false);

        dialog.setContentView(dialogDataBinding.getRoot());
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.textView, title);
        dialogDataBinding.textView.setText(title);
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.text, getString(R.string.commission_question, String.valueOf(getViewModel().getTransferFee())));//
        dialogDataBinding.text.setText(getString(R.string.commission_question, String.valueOf(getViewModel().getTransferFee())));//

        dialogDataBinding.btnOk.setVisibility(View.GONE);

        dialog.show();

        dialogDataBinding.buttonSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            openConfirmPinDialog();
        });

        dialogDataBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

}
