package com.apps2you.albaraka.ui.my_financing.fragments

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentMyFinancingBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.transfer.adsl.ADSLFragmentDirections
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MyFinancingFragment   : BaseFragment<FragmentMyFinancingBinding, MyFinancingViewModel>()  {
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_financing
    }

    override fun setViewModel(): Class<MyFinancingViewModel> {
        return MyFinancingViewModel::class.java
    }

    override fun setUpView() {

        val pieChart = mViewDataBinding.pieChart
        val args = MyFinancingFragmentArgs.fromBundle(requireArguments())
        val dealNo = args.dealNo
        val branchCode = args.branchCode
        // Sample data for the PieChart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(4000000f, getString(R.string.no_satelment))) // 40% of total
        entries.add(PieEntry(6000000f, getString(R.string.no_rest_satelment))) // 60% of total

        val dataSet = PieDataSet(entries, "")

        val MATERIAL_COLORS = intArrayOf(
            ColorTemplate.rgb("#e11838"),
            ColorTemplate.rgb("#f78f1d"),
        )
        dataSet.setColors(MATERIAL_COLORS, 255)

        // Custom Value Formatter to force English numbers
        val englishFormatter = object : ValueFormatter() {
            private val mFormat = DecimalFormat("###,###,###", DecimalFormatSymbols(Locale.ENGLISH))

            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return mFormat.format(value) + "%"
            }
        }

        val data = PieData(dataSet)
        data.setValueFormatter(englishFormatter) // Apply the formatter
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.WHITE)


        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)


        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.WHITE)

        pieChart.setCenterTextSize(24f)
        pieChart.animateY(1000)

        pieChart.legend.isEnabled = false

        pieChart.setTouchEnabled(false)

        // Refresh the chart
        pieChart.invalidate()

        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true) // Disable the back button
        }

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val label = (e as PieEntry).label
                Toast.makeText(context, "Selected: $label", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected() {
                // Handle no selection
            }
        })


        mViewDataBinding.allInstallments.root.setOnClickListener {
            openMyFinancingTableFragment(dealNo,branchCode)
        }

        mViewDataBinding.unpaidInstallment.root.setOnClickListener {
            openMyFinancingTableFragment(dealNo,branchCode)
        }
    }


    private fun openMyFinancingTableFragment(dealNo: String, branchCode: String) {
        val action = MyFinancingFragmentDirections
            .actionMyFinancingFragmentDetailsToMyFinancingFragmentTable(dealNo, branchCode)
        findNavController().navigate(action)
    }



    override fun fetchData() {
    }
}