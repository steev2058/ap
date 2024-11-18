package com.apps2you.albaraka.ui.my_financing2.fragments

import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentMyFinancing2Binding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.viewmodels.MyFinancing2ViewModel
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

class MyFinancingFragment2   : BaseFragment<FragmentMyFinancing2Binding, MyFinancing2ViewModel>()  {
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_financing2
    }

    override fun setViewModel(): Class<MyFinancing2ViewModel> {
        return MyFinancing2ViewModel::class.java
    }

    override fun setUpView() {



        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true) // Disable the back button
        }

    }

    override fun fetchData() {
    }
}