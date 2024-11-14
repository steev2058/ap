package com.apps2you.albaraka.ui.my_financing.fragments

import android.content.Context
import android.view.View
import android.widget.Toast
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



//    override fun setUpView() {
//        viewModel = ViewModelProvider(this, viewModelFactory).get(MyFinancingViewModel::class.java)
//
//        showProgress()
//
//        // Observe the API data
//        viewModel.getAllMyFinancing().observe(viewLifecycleOwner, Observer { resource ->
//            hideProgress()
//
//            if (resource.data.isNullOrEmpty()) {
//
//                mViewDataBinding.listView.visibility = View.GONE
//                mViewDataBinding.noDealsTextView.visibility = View.VISIBLE
//            } else {
//
//                mViewDataBinding.noDealsTextView.visibility = View.GONE
//                mViewDataBinding.listView.visibility = View.VISIBLE
//
//                val transactions = resource.data
//                val adapter = FinancingTransactionAdapter(this, transactions) { transaction ->
//                    // Pass deal_no and branch_code to the details fragment
//                    val action = MyFinancingListFragmentDirections
//                        .actionMyFinancingFragmentCardToMyFinancingFragmentDetails(
//                            transaction.DEAL_NO,
//                            transaction.BRANCH_CODE,
//                            transaction.REMAIN_AMT
//                        )
//                    navController.navigate(action)
//                }
//                mViewDataBinding.listView.adapter = adapter
//            }
//
//            // Handle errors and hide progress in case of failure
//            if (resource.status == Status.ERROR) {
//                hideProgress()
//
//            }
//        })
//
//        // Get NavController using Navigation.findNavController(view)
//        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//    }

    override fun setUpView() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyFinancingViewModel::class.java)

        showProgress()


        viewModel.getAllMyFinancing().observe(viewLifecycleOwner, Observer { resource ->


            if (resource.data.isNullOrEmpty()) {

                mViewDataBinding.listView.visibility = View.GONE
                mViewDataBinding.emptyStateLayout.visibility = View.VISIBLE
            } else {

                mViewDataBinding.emptyStateLayout.visibility = View.GONE
                mViewDataBinding.listView.visibility = View.VISIBLE

                val transactions = resource.data
                val adapter = FinancingTransactionAdapter(this, transactions) { transaction ->
                    val action = MyFinancingListFragmentDirections
                        .actionMyFinancingFragmentCardToMyFinancingFragmentDetails(
                            transaction.DEAL_NO,
                            transaction.BRANCH_CODE,
                            transaction.REMAIN_AMT
                        )
                    navController.navigate(action)
                }
                mViewDataBinding.listView.adapter = adapter
                hideProgress()
            }


            if (resource.status == Status.ERROR) {
                hideProgress()
                showToast(resource.message)

            }
        })

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

//    fun openMyFinancingDetailsFragment() {
//        navController.navigate(MyFinancingListFragmentDirections.actionMyFinancingFragmentCardToMyFinancingFragmentDetails())
//    }

    override fun fetchData() {
    }
}

