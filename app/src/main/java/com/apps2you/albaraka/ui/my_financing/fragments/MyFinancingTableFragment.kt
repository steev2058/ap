package com.apps2you.albaraka.ui.my_financing.fragments

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentMyFinancingCardBinding
import com.apps2you.albaraka.databinding.FragmentMyFinancingTableBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import dagger.android.support.AndroidSupportInjection
import ir.androidexception.datatable.DataTable
import ir.androidexception.datatable.model.DataTableHeader
import ir.androidexception.datatable.model.DataTableRow
import javax.inject.Inject
import kotlin.random.Random

class MyFinancingTableFragment : BaseFragment<FragmentMyFinancingTableBinding, MyFinancingViewModel>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MyFinancingViewModel


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

    override fun setUpView() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyFinancingViewModel::class.java)
        val dataTable: DataTable = mViewDataBinding.dataTable
        val fieldWeight = 1f
        val header = DataTableHeader.Builder()
            .item("عدد الأقساط", fieldWeight.toInt())
            .item("تاريخ القسط", fieldWeight.toInt())
            .item("قيمة القسط", fieldWeight.toInt())
            .item("تاريخ التسديد", fieldWeight.toInt())
            .item("القيمة المسددة", fieldWeight.toInt())
            .item("الحالة", fieldWeight.toInt())
            .build()

        val rows = ArrayList<DataTableRow>()

        for (i in 0 until 200) {
            val randomValue = Random.nextInt(i + 1)
            val randomDiscount = Random.nextInt(20)
            val row = DataTableRow.Builder()
                .value("#$i")
                .value("2020-8-$randomValue")
                .value("${randomValue * 1000}\$")
                .value("2020-8-$randomValue")
                .value("${randomValue * 1000}\$")
                .value("مسدد")
                .build()
            rows.add(row)
        }

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.tahoma)
        dataTable.typeface = typeface

        dataTable.header = header
        dataTable.rows = rows
        context?.let { dataTable.inflate(it) }
    }

    override fun fetchData() {

    }
}