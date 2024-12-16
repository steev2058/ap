package com.apps2you.albaraka.ui.my_financing.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentMyFinancingTableBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import dagger.android.support.AndroidSupportInjection
import ir.androidexception.datatable.DataTable
import ir.androidexception.datatable.model.DataTableHeader
import ir.androidexception.datatable.model.DataTableRow
import javax.inject.Inject

class MyFinancingTableFragment : BaseFragment<FragmentMyFinancingTableBinding, MyFinancingViewModel>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MyFinancingViewModel
    private lateinit var dealNo: String
     lateinit var branchCode: String
    private lateinit var selectedLayout: LinearLayout
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_financing_table
    }

    override fun setViewModel(): Class<MyFinancingViewModel> {
        return MyFinancingViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = MyFinancingTableFragmentArgs.fromBundle(requireArguments())
        dealNo = args.dealNo
        branchCode = args.branchCode
    }


    override fun setUpView() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyFinancingViewModel::class.java)
        showProgress()
        // Find the layouts for each status
        val paidLayout = mViewDataBinding.paidLayout
        val checkPaid = paidLayout.findViewById<ImageView>(R.id.check_paid)

        val delayedLayout = mViewDataBinding.delayedLayout
        val checkDelayed = delayedLayout.findViewById<ImageView>(R.id.check_delayed)

        val notDueLayout = mViewDataBinding.notDueLayout
        val checkNotDue = notDueLayout.findViewById<ImageView>(R.id.check_not_due)

        val allLayout = mViewDataBinding.allLayout
        val checkAll = allLayout.findViewById<ImageView>(R.id.check_all)

        val partPaidLayout = mViewDataBinding.partPaidLayout
        val checkPartPaid = partPaidLayout.findViewById<ImageView>(R.id.check_part_paid)

        // Set click listeners to handle selection and filtering
        paidLayout.setOnClickListener {
            handleSelection(paidLayout, checkPaid, "مسدد")
        }

        delayedLayout.setOnClickListener {
            handleSelection(delayedLayout, checkDelayed, "متآخر")
        }

        notDueLayout.setOnClickListener {
            handleSelection(notDueLayout, checkNotDue, "غير مستحق بعد")
        }
        allLayout.setOnClickListener {
            handleSelection(allLayout, checkAll, "الجميع")
        }

        partPaidLayout.setOnClickListener {
            handleSelection(partPaidLayout, checkPartPaid, "مسدد جزئياً")
        }

        val dataTable: DataTable = mViewDataBinding.dataTable
        val fieldWeight = 1f

        viewModel.getAllMyFinancingDetails(dealNo, branchCode).observe(viewLifecycleOwner, Observer { resource ->

            resource.data?.let { details ->
                // Update the details in the ViewModel
                viewModel.updateAllDetails(details)
            }
            hideProgress()
        })
        val header = DataTableHeader.Builder()
            .item(getString(R.string.number),1)  // Smaller weight for a short value
            .item(getString(R.string.date), 2)  // Larger weight for date
            .item(getString(R.string.payment_date), 3)  // Larger weight for date
            .item(getString(R.string.value), 2)  // More weight for larger numeric values
            .item(getString(R.string.paid_value), 3)  // More weight for larger numeric values
            .item("", 1)
            .build()


        val rows = ArrayList<DataTableRow>()
        val greenCircle = "\uD83D\uDFE2"  // Green circle emoji
        val redCircle = "\uD83D\uDD34"    // Red circle emoji
        val orangeCircle = "\uD83D\uDFE0" // Orange circle emoji



        viewModel.filteredDetails.observe(viewLifecycleOwner, Observer { resource ->

            val rows = resource.map { detail ->
                    val statusWithIcon = when (detail.LINE_STATUS) {
                        "مسدد" -> "$greenCircle"
                        "متآخر" -> "$redCircle"
                        "غير مستحق بعد" -> "$orangeCircle"
                        else -> detail.LINE_STATUS
                    }

                    DataTableRow.Builder()
                        .value(detail.LINE_NBR.toString())
                        .value(detail.VALUE_DATE.toString())
                        .value(detail.DATE_SETTLED.toString())
                        .value(detail.PAYMENT_AMOUNT.toString())
                        .value(detail.SETTLEMENT_AMOUNT)
                        .value(statusWithIcon)
                        .build()
                }
                // Set the rows to the dataTable
                dataTable.rows = ArrayList(rows)
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.tahoma)
                dataTable.typeface = typeface
                dataTable.header = header
           // dataTable.headerHorizontalPadding
                dataTable.invalidate()
                context?.let { dataTable.inflate(it) }
            hideProgress()

        })


    }

    private fun handleSelection(selected: LinearLayout, checkIcon: ImageView, status: String) {
        // Deselect the previous layout if it's not the same
        if (this::selectedLayout.isInitialized && selectedLayout != selected) {
            selectedLayout.isSelected = false
            selectedLayout.findViewById<ImageView>(R.id.check_paid)?.visibility = View.GONE
            selectedLayout.findViewById<ImageView>(R.id.check_delayed)?.visibility = View.GONE
            selectedLayout.findViewById<ImageView>(R.id.check_not_due)?.visibility = View.GONE
            selectedLayout.findViewById<ImageView>(R.id.check_all)?.visibility = View.GONE
            selectedLayout.findViewById<ImageView>(R.id.check_part_paid)?.visibility = View.GONE

            selectedLayout.setBackgroundResource(0)
        }

        // Select the new layout
        selected.isSelected = true
        checkIcon.visibility = View.VISIBLE
        selectedLayout = selected
        selectedLayout.setBackgroundResource(R.drawable.bg_focused_edittext_red)
        // Filter the table based on the status
        viewModel.onStatusFilterClicked(status)
    }

    override fun fetchData() {

    }
}