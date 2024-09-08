package com.apps2you.albaraka.ui.my_financing.fragments

import android.content.Context
import android.os.Bundle
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


        val dataTable: DataTable = mViewDataBinding.dataTable
        val fieldWeight = 1f
        val header = DataTableHeader.Builder()
            .item("رقم", 0.5.toInt())  // Smaller weight for a short value
            .item("التاريخ", 3)  // Larger weight for date
            .item("القيمة", 2)  // More weight for larger numeric values
            .item("تاريخ التسديد", 3)  // Larger weight for date
            .item("القيمة المسددة", 3)  // More weight for larger numeric values
            .item("", 0.5.toInt())
            .build()

        val rows = ArrayList<DataTableRow>()
        val greenCircle = "\uD83D\uDFE2"  // Green circle emoji
        val redCircle = "\uD83D\uDD34"    // Red circle emoji
        val orangeCircle = "\uD83D\uDFE0" // Orange circle emoji
        viewModel.getAllMyFinancingDetails(dealNo, branchCode).observe(viewLifecycleOwner, Observer { resource ->
            resource.data?.let { details ->
                rows.clear() // Map the details to DataTableRow and add them to the ArrayList
                rows.addAll(details.map { detail ->
                    val statusWithIcon = when (detail.LINE_STATUS) {
                        "مسدد" -> "$greenCircle"
                        "متآخر" -> "$redCircle"
                        "غير مستحق بعد" -> "$orangeCircle"
                        else -> detail.LINE_STATUS
                    }

                    DataTableRow.Builder()
                        .value(detail.LINE_NBR.toString())
                        .value(detail.VALUE_DATE.toString())
                        .value(detail.PAYMENT_AMOUNT.toString())
                        .value(detail.DATE_SETTLED.toString())
                        .value(detail.SETTLEMENT_AMOUNT)
                        .value(statusWithIcon)
                        .build()
                })
                // Set the rows to the dataTable
                dataTable.rows = rows
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.tahoma)
                dataTable.typeface = typeface
                dataTable.header = header
                dataTable.invalidate()
                context?.let { dataTable.inflate(it) }
            }
        })


    }


    override fun fetchData() {

    }
}