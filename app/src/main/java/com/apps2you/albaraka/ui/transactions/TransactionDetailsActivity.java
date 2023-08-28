package com.apps2you.albaraka.ui.transactions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.databinding.ActivityTransactionDetailsBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.home.HomeActivity;
import com.apps2you.albaraka.utils.StorageUtils;
import com.apps2you.albaraka.viewmodels.TransactionViewModel;

import org.jetbrains.annotations.NotNull;

import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.BRANCH_CODE_EXTRA;
import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.TRANSACTION_ID_EXTRA;


public class TransactionDetailsActivity extends BaseActivity<ActivityTransactionDetailsBinding, TransactionViewModel> {

    public static final String EXTRA_SHOW_DO_ANOTHER_TRANSFER = "show_do_anther_transfer_extra";

    public static final String RESULT_DO_ANOTHER_TRANSFER = "do_anther_transfer_result";

    private final int RC_PERMISSION_WRITE = 123;


    public static Intent getIntent(Context context, Integer transactionId, String branchCode, boolean showDoAnotherTransfer) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.putExtra(TRANSACTION_ID_EXTRA, transactionId);
        intent.putExtra(BRANCH_CODE_EXTRA, branchCode);
        intent.putExtra(EXTRA_SHOW_DO_ANOTHER_TRANSFER, showDoAnotherTransfer);
        return intent;
    }

    public static Intent getIntent(Context context, Integer transactionId, String branchCode) {
        return getIntent(context, transactionId, branchCode, false);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_details;
    }

    @Override
    public Class<TransactionViewModel> setViewModel() {
        return TransactionViewModel.class;
    }

    @Override
    public void setUpView() {

        getViewDataBinding().setShowDoAnotherTransfer(getIntent().getBooleanExtra(EXTRA_SHOW_DO_ANOTHER_TRANSFER, false));

        getViewDataBinding().iBtnPdf.setOnClickListener(v -> createPdf());
        getViewDataBinding().buttonOk.setOnClickListener(v -> redirectToHome());
        getViewDataBinding().buttonDoAnotherTransfer.setOnClickListener(v -> doAnotherTransfer());
    }

    private void redirectToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        activityNavigator.finishAffinity().navigate(intent);
    }

    private void doAnotherTransfer() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_DO_ANOTHER_TRANSFER, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void fetchData() {
        int transactionId = getIntent().getIntExtra(TRANSACTION_ID_EXTRA, -1);
        String branchCode = getIntent().getStringExtra(BRANCH_CODE_EXTRA);

        getViewDataBinding().iBtnPdf.setVisibility(View.INVISIBLE);

        if (transactionId == -1) {
            getViewDataBinding().layoutSuccessfulTransfer.setVisibility(View.VISIBLE);
            getViewDataBinding().view.setVisibility(View.GONE);
        } else {
            getViewModel().getTransactionDetails(transactionId, branchCode).observe(this, resource -> {
                switch (resource.status) {
                    case LOADING:
                        getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                        getViewDataBinding().view.setVisibility(View.GONE);
                        break;

                    case ERROR:
                        getViewDataBinding().progressBar.setVisibility(View.GONE);
                        showToast(resource.message);
                        break;

                    case SUCCESS:
                        getViewDataBinding().progressBar.setVisibility(View.GONE);
                        getViewDataBinding().view.setVisibility(View.VISIBLE);
                        getViewDataBinding().iBtnPdf.setVisibility(View.VISIBLE);

                        showTransaction(resource.data);
                        break;
                }
            });
        }
    }


    private void showTransaction(Transaction transaction) {
        if (transaction == null) return;
        if (transaction.getOriginalId() == null || transaction.getOriginalId() == -1) {
            setToolbarTitle(getViewDataBinding().toolbar, "");
            getViewDataBinding().layoutSuccessfulTransfer.setVisibility(View.VISIBLE);
            getViewDataBinding().view.setVisibility(View.GONE);
            getViewDataBinding().iBtnPdf.setVisibility(View.GONE);
        } else {
            setToolbarTitle(getViewDataBinding().toolbar,
                    getString(R.string.transaction).concat(String.valueOf(transaction.getOriginalId())));

            getViewModel().transaction.set(transaction);
        }
    }

    @Override
    public void listenToVariables() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            createPdf();
    }

    private void createPdf() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            showDialogMessage(
                    getString(R.string.write_external_storage_permission),
                    getString(R.string.msg_write_external_storage_permission),
                    getString(R.string.action_ok),
                    getString(R.string.cancel),
                    (dialog, id) ->
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSION_WRITE),
                    (dialog, id) -> dialog.dismiss(),
                    false
            );

            return;
        }

        ViewGroup container = findViewById(R.id.container);

        // Measures
        final int height = container.getHeight(),
                width = getViewDataBinding().view.getWidth();
        int pageCount = 1;

        // create a new document
        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageCount).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        getViewDataBinding().view.draw(canvas);

        // finish the page
        document.finishPage(page);

        if (!new StorageUtils().saveToFile(this, document, "TransferDetails_"))
            showToast(getString(R.string.error_occurred));
    }
}
