package com.apps2you.albaraka.ui.atmCard

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelStoreOwner
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.AtmLimit
import com.apps2you.albaraka.databinding.FragmentAtmLimitsBinding
import com.apps2you.albaraka.viewmodels.AtmViewModel


class AtmLimitsFragment : AtmBaseFragment<FragmentAtmLimitsBinding>() {

    private lateinit var itemSelectionAdapter: LimitSelectionAdapter


    override fun getViewModelOwner(): ViewModelStoreOwner? {
        return activity
    }

    override fun setViewModel(): Class<AtmViewModel> {
        return AtmViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_atm_limits
    }

    override fun getBindingVariable(): Int {
        return 0
    }

    override fun setUpView() {
        itemSelectionAdapter = LimitSelectionAdapter(requireContext()) { item: AtmLimit, position: Int ->
            mViewModel.selectLimit(item.limit)
            showFeeDialog(getString(R.string.update_limit), item.fee) {
                navController.popBackStack()
                parentFragmentManager.setFragmentResult(RC_SELECT_LIMIT, bundleOf(BUNDLE_LIMIT to item.limit))
            }
        }

        viewDataBinding.recyclerViewItems.adapter = itemSelectionAdapter
    }

    override fun fetchData() {
        if (viewModel.atmData != null) {
            itemSelectionAdapter.submitData(viewModel.atmData.limits)
        }
    }
}