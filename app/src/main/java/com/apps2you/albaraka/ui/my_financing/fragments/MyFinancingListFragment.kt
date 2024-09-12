package com.apps2you.albaraka.ui.my_financing.fragments

import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentMyFinancingCardBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.my_financing.FinancingTransactionAdapter
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MyFinancingListFragment : BaseFragment<FragmentMyFinancingCardBinding, MyFinancingViewModel>() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MyFinancingViewModel
    private lateinit var navController: NavController


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_financing_card
    }

    override fun setViewModel(): Class<MyFinancingViewModel> {
        return MyFinancingViewModel::class.java
    }

    override fun setUpView() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyFinancingViewModel::class.java)


        showProgress()
        // Observe the API data
        viewModel.getAllMyFinancing().observe(viewLifecycleOwner, Observer { resource ->

            resource.data?.let { transactions ->
                hideProgress()
                val adapter = FinancingTransactionAdapter(this, transactions) { transaction ->
                    // Pass deal_no and branch_code to the details fragment
                    val action = MyFinancingListFragmentDirections
                        .actionMyFinancingFragmentCardToMyFinancingFragmentDetails(
                            transaction.DEAL_NO,
                            transaction.BRANCH_CODE,transaction.REMAIN_AMT)

                    navController.navigate(action)
                }
                mViewDataBinding.listView.adapter = adapter
            }
            // Handle errors and hide progress in case of failure
            if (resource.status == Status.ERROR) {
                hideProgress()
                // Optionally show an error message or a toast
            }

        })

        // Get NavController using Navigation.findNavController(view)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }



//    fun openMyFinancingDetailsFragment() {
//        navController.navigate(MyFinancingListFragmentDirections.actionMyFinancingFragmentCardToMyFinancingFragmentDetails())
//    }

    override fun fetchData() {
    }
}

