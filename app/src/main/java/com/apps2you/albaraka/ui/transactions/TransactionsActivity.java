package com.apps2you.albaraka.ui.transactions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.ActivityTransactionsBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.common.adapters.AccountsSpinnerAdapter;
import com.apps2you.albaraka.ui.home.TransactionAdapter;
import com.apps2you.albaraka.utils.RangeValidator;
import com.apps2you.albaraka.utils.StorageUtils;
import com.apps2you.albaraka.viewmodels.TransactionsViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TransactionsActivity extends BaseActivity<ActivityTransactionsBinding, TransactionsViewModel> implements TransactionAdapter.ItemClickListener {

    private final int RC_PERMISSION_WRITE = 123;
    boolean isPreSelectedAccount = false;//to check if we should request the API on account selection or not (we must ignore the first selection done by the spinner if there is an account passed to the activity)
    private String fromDate, toDate;
    private Account selectedAccount;
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transactions;
    }

    @Override
    public Class<TransactionsViewModel> setViewModel() {
        return TransactionsViewModel.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.my_transactions));

        getViewDataBinding().transactionsRecyclerView.addItemDecoration(new DividerItemDecoration(TransactionsActivity.this, LinearLayoutManager.VERTICAL));

        getViewDataBinding().iBtnPdf.setOnClickListener(v -> {
//            View headerView = findViewById(R.id.pdf_hear);
//            // fill header data
//            Account account = (Account) getViewDataBinding().spinnerAccount.getSelectedItem();
//            ((TextView) headerView.findViewById(R.id.tv_number)).setText(getString(R.string.pdf_account_number, account.getNumber()));
//
//            ((TextView) headerView.findViewById(R.id.tv_date)).setText(getString(R.string.pdf_date, getDateRangeString()));
//
//            // the header view is drawn to pdf before it's updated with values in UI thread
//            final Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(() -> createPdf(), 50);

            saveFile();
        });

        selectToday();
    }

    private void selectToday() {
        Date todayDate = Calendar.getInstance().getTime();
        fromDate = simpleDateFormat.format(todayDate);
        toDate = simpleDateFormat.format(todayDate);

        String today = getDateString(todayDate.getTime());
        getViewDataBinding().tvFrom.setText(today);
        getViewDataBinding().tvTo.setText(today);
    }

    private MaterialDatePicker<Long> getPickerDialog() {
        Locale locale = new Locale(UserUtils.getInstance(TransactionsActivity.this).getLanguage().equals("ar") ? "ar_SY" : "en");//for months' name > should be in Arabic like: حزيران, تموز
        Locale.setDefault(locale);
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTheme(R.style.Widget_AppTheme_MaterialDatePicker);

        builder.setCalendarConstraints(limitRange().build());

        MaterialDatePicker<Long> picker = builder.build();

        picker.show(getSupportFragmentManager(), picker.toString());
        picker.setCancelable(false);

        return picker;
    }

    private String getDateString(Long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /*
   Limit selectable range
    */
    private CalendarConstraints.Builder limitRange() {

        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -3);
        long minDate = calendarStart.getTimeInMillis();
        long maxDate = calendarEnd.getTimeInMillis();

        constraintsBuilderRange.setStart(minDate);
        constraintsBuilderRange.setEnd(maxDate);

        constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));
        return constraintsBuilderRange;
    }

    @Override
    public void fetchData() {
        //1.getAccounts
        //2.onItemSelected > get related transactions

        getViewDataBinding().iBtnPdf.setVisibility(View.INVISIBLE);
        getViewModel().getAccounts().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    //todo spinner progress and refresh
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);

                    // hide by default while fetching data
                    getViewDataBinding().tvFrom.setVisibility(View.GONE);
                    getViewDataBinding().tvTo.setVisibility(View.GONE);
                    getViewDataBinding().tvLabelFrom.setVisibility(View.GONE);
                    getViewDataBinding().tvLabelTo.setVisibility(View.GONE);
                    getViewDataBinding().iBtnSubmit.setVisibility(View.GONE);
                    break;

                case ERROR:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    showToast(resource.message);
                    break;

                case SUCCESS:
                    assert resource.data != null;

                    if (getIntent().getStringExtra("accountNo") != null)
                        isPreSelectedAccount = true;
                    ArrayList<Account> items = new ArrayList<>(resource.data);
                    AccountsSpinnerAdapter adapter = new AccountsSpinnerAdapter();
                    adapter.addAll(items);
                    getViewDataBinding().spinnerAccount.setAdapter(adapter);
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    getViewDataBinding().tvFrom.setVisibility(View.VISIBLE);
                    getViewDataBinding().tvTo.setVisibility(View.VISIBLE);
                    getViewDataBinding().tvLabelFrom.setVisibility(View.VISIBLE);
                    getViewDataBinding().tvLabelTo.setVisibility(View.VISIBLE);
                    getViewDataBinding().iBtnSubmit.setVisibility(View.VISIBLE);

                    if (isPreSelectedAccount) {
                        Account account = new Account();
                        account.setNumber(getIntent().getStringExtra("accountNo"));
                        if (items.contains(account))
                            getViewDataBinding().spinnerAccount.setSelection(items.indexOf(account));
                    }
                    break;
            }
        });
    }

    @Override
    public void listenToVariables() {
        getViewDataBinding().spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAccount = ((Account) getViewDataBinding().spinnerAccount.getAdapter().getItem(i));
                String accountNumber = selectedAccount.getNumber();
                if (!isPreSelectedAccount || getIntent().getStringExtra("accountNo").equals(accountNumber))//so the first selection done by the system in spinner is ignored iff there is a specific account passed to the activity
                    getTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getViewDataBinding().spinnerAccount.setOnTouchListener((view, motionEvent) -> {
            isPreSelectedAccount = false;
            return false;
        });

        getViewDataBinding().tvFrom.setOnClickListener(view -> {

            MaterialDatePicker<Long> picker = getPickerDialog();
            picker.addOnPositiveButtonClickListener(selection -> {

                fromDate = simpleDateFormat.format(selection);
                mViewDataBinding.tvFrom.setText(getDateString(selection));
            });
        });

        getViewDataBinding().tvTo.setOnClickListener(view -> {

            MaterialDatePicker<Long> picker = getPickerDialog();
            picker.addOnPositiveButtonClickListener(selection -> {
                toDate = simpleDateFormat.format(selection);
                mViewDataBinding.tvTo.setText(getDateString(selection));
            });
        });

        getViewDataBinding().iBtnSubmit.setOnClickListener(v -> {
            try {
                Date from = simpleDateFormat.parse(fromDate);
                Date to = simpleDateFormat.parse(toDate);

                if (to != null) {
                    if (to.before(from))
                        showToast(R.string.invalid_date);
                    else
                        getTransactions();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void getTransactions() {
        if (selectedAccount == null) return;
        getViewModel().getTransactions(selectedAccount.getNumber(), selectedAccount.getName(), fromDate, toDate).observe(TransactionsActivity.this, resource -> {
            switch (resource.status) {
                case LOADING:
                    getViewDataBinding().iBtnPdf.setVisibility(View.INVISIBLE);
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                    getViewDataBinding().transactionsRecyclerView.setVisibility(View.GONE);
                    getViewDataBinding().iBtnSubmit.setEnabled(false);
                    break;

                case ERROR:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    getViewDataBinding().iBtnSubmit.setEnabled(true);

                    showToast(resource.message);
                    break;

                case SUCCESS:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    getViewDataBinding().iBtnPdf.setVisibility(View.VISIBLE);
                    getViewDataBinding().transactionsRecyclerView.setVisibility(View.VISIBLE);
                    getViewDataBinding().iBtnSubmit.setEnabled(true);

                    if (resource.data != null) {
                        getViewModel().getTransactions().clear();
                        getViewModel().getTransactions().addAll(resource.data);

                        TransactionAdapter adapter = new TransactionAdapter(getViewModel().getTransactions());
                        getViewDataBinding().transactionsRecyclerView.setAdapter(adapter);
                        adapter.setItemClickListener(TransactionsActivity.this);

                        isPreSelectedAccount = false;
                    }
                    break;
            }
        });
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
            saveFile();
    }

    @Override
    public void onItemClick(@NotNull Transaction item, @NotNull View view) {
        startActivity(TransactionDetailsActivity.getIntent(this, item.getOriginalId(), item.getBranchCode()));
    }

    private void saveFile() {

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

        showProgress();

        getViewModel().getStatementFile().observe(this, resource -> {
            hideProgress();

            if (resource == null)
                showToast(getString(R.string.error_occurred));
            else {
                if (!new StorageUtils().saveToFile(this, resource, "StatementReport_"))
                    showToast(getString(R.string.error_occurred));
            }
        });
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

        View headerView = findViewById(R.id.pdf_hear);

        ViewGroup content = findViewById(R.id.container);
        // Measures
        final int height = content.getHeight(), width = content.getWidth();

        int heightTranslate; // this variable to trace canvas
        int pageCount = 1;

        // create a new document
        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageCount).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();


        headerView.draw(canvas);
        canvas.translate(0, headerView.getMeasuredHeight());
        heightTranslate = headerView.getMeasuredHeight();


        TransactionPdfAdapter adapter = new TransactionPdfAdapter(getViewModel().getTransactions());
        for (int i = 0; i < adapter.getItemCount(); i++) {

            TransactionPdfAdapter.ViewHolder holder = adapter.createViewHolder(getViewDataBinding().transactionsRecyclerView, 0);
            adapter.bindViewHolder(holder, i);
            holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(content.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            holder.itemView.getDrawingCache();

            heightTranslate += holder.itemView.getMeasuredHeight();

            // before drawing the childView, check if it fits in this page
            if (heightTranslate >= height - 28) {

                // finish the page
                document.finishPage(page);

                // add another page
                pageInfo = new PdfDocument.PageInfo.Builder(width, height, ++pageCount).create(); //12000
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                heightTranslate = 40;
                canvas.translate(0, heightTranslate);

                // draw header of table at each page
                View tableHeader = headerView.findViewById(R.id.table_header);
                tableHeader.draw(canvas);
                canvas.translate(0, tableHeader.getMeasuredHeight());
                heightTranslate += tableHeader.getMeasuredHeight();

                heightTranslate += holder.itemView.getMeasuredHeight();
            }

            holder.itemView.draw(canvas);
            canvas.translate(0, holder.itemView.getMeasuredHeight());
        }

        // finish the page
        document.finishPage(page);

        if (!new StorageUtils().saveToFile(this, document, "StatementReport_"))
            showToast(getString(R.string.error_occurred));
    }
}
