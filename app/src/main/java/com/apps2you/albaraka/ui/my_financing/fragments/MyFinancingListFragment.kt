package com.apps2you.albaraka.ui.my_financing.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.FinancingTransaction
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.my_financing.FinancingTransactionAdapter
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.databinding.FragmentMyFinancingCardBinding
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

        // Observe the API data
        viewModel.getAllMyFinancing().observe(viewLifecycleOwner, Observer { resource ->
            resource.data?.let { transactions ->
                val adapter = FinancingTransactionAdapter(this, transactions) { transaction ->
                    // Pass deal_no and branch_code to the details fragment
                    val action = MyFinancingListFragmentDirections
                        .actionMyFinancingFragmentCardToMyFinancingFragmentDetails(
                            transaction.DEAL_NO,
                            transaction.BRANCH_CODE)

                    navController.navigate(action)
                }
                mViewDataBinding.listView.adapter = adapter
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

