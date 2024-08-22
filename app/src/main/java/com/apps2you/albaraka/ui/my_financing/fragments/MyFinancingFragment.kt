package com.apps2you.albaraka.ui.my_financing.fragments

import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentMyFinancingBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.viewmodels.FinanceFormViewModel
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
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
        super.setUpView()

        val pieChart = binding.pieChart

        // Sample data for the PieChart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(10f, "Remaining Premiums"))
        entries.add(PieEntry(20f, "Paid Installments"))
        entries.add(PieEntry(8000f, "Remaining Commitment"))
        entries.add(PieEntry(500000f, "Installment"))

        val dataSet = PieDataSet(entries, "Financial Data")
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        val data = PieData(dataSet)

        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)

        // Optional customizations
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "My Financing"
        pieChart.setCenterTextSize(24f)
        pieChart.animateY(1000)

        // Legend customizations
        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.textSize = 12f

        // Refresh the chart
        pieChart.invalidate()


        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val label = (e as PieEntry).label
                Toast.makeText(context, "Selected: $label", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected() {
                // Handle no selection
            }
        })
    }
    override fun fetchData() {
    }
}