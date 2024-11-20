package com.apps2you.albaraka.ui.my_financing.fragments

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.FinancingTransaction
import com.apps2you.albaraka.databinding.FragmentMyFinancingBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.transfer.adsl.ADSLFragmentDirections
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import com.github.mikephil.charting.charts.PieChart
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
        val remainAmt = args.remainAmt
        // Sample data for the PieChart
        val entries = ArrayList<PieEntry>()


        showProgress()


        // Custom Value Formatter to force English numbers
        val englishFormatter = object : ValueFormatter() {
            private val mFormat = DecimalFormat("###,###,###", DecimalFormatSymbols(Locale.ENGLISH))

            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return mFormat.format(value) + "%"
            }
        }



        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true) // Disable the back button
        }

        val MATERIAL_COLORS = intArrayOf(
            ColorTemplate.rgb("#f78f1d"),
            ColorTemplate.rgb("#e11838"),
        )


        mViewDataBinding.remainingCommitment.text = remainAmt
        viewModel.paidInstallments.observe(viewLifecycleOwner, Observer { paidInstallments ->
            mViewDataBinding.PaidInstallments.text = paidInstallments.toString()
            entries.add(PieEntry(paidInstallments.toString().toFloat(), getString(R.string.no_satelment))) // 40% of total
            updatePieChart(pieChart, entries, MATERIAL_COLORS, englishFormatter)
        })

        viewModel.remainingPremiums.observe(viewLifecycleOwner, Observer { remainingPremiums ->
            mViewDataBinding.remainingPremiums.text = remainingPremiums.toString()
            entries.add(PieEntry( remainingPremiums.toString().toFloat(), getString(R.string.no_rest_satelment))) // 60% of total

            updatePieChart(pieChart, entries, MATERIAL_COLORS, englishFormatter)

        })



        viewModel.getAllMyFinancingDetails(dealNo, branchCode).observe(viewLifecycleOwner, Observer { resource ->
            resource.data?.let { details ->
                // Update the ViewModel with details
                viewModel.updateAllDetails(details)

                // Calculate and display the required values
                viewModel.calculateAndDisplayData(details)

                // Set AMT_PER_PAYMENT and REMAIN_AMT for the UI
                if (details.isNotEmpty()) {
                    val firstDetail = details.firstOrNull() // You can use first or specific index as per your logic
                    mViewDataBinding.TheInstallment.text = firstDetail?.SETTLEMENT_AMOUNT

                }
                hideProgress()
            }
        })

                    // Set the remainAmt value in the TextView


        mViewDataBinding.allInstallments.root.setOnClickListener {
            openMyFinancingTableFragment(dealNo,branchCode)
        }


    }

    private fun updatePieChart(
        pieChart: PieChart,
        entries: ArrayList<PieEntry>,
        colors: IntArray,
        valueFormatter: ValueFormatter
    ) {
        val uniqueArr =entries.distinctBy { it.y };
        val dataSet = PieDataSet(uniqueArr, "")
        dataSet.setColors(colors, 255)
        val data = PieData(dataSet)
        data.setValueFormatter(valueFormatter)
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

        // Refresh the chart to apply changes
        pieChart.invalidate()
    }
    private fun openMyFinancingTableFragment(dealNo: String, branchCode: String) {
        val action = MyFinancingFragmentDirections
            .actionMyFinancingFragmentDetailsToMyFinancingFragmentTable(dealNo, branchCode)
        findNavController().navigate(action)
    }



    override fun fetchData() {
    }
}