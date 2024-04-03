package com.apps2you.albaraka.ui.quick_services

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.ActivityPersonalizeQuickServicesBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.utils.Constants
import com.apps2you.albaraka.viewmodels.HomeViewModel
import java.util.*
import kotlin.collections.ArrayList

class PersonalizeQuickServicesActivity : BaseActivity<ActivityPersonalizeQuickServicesBinding, HomeViewModel>(), SortableQuickServicesAdapter.ItemClickListener {
    var adapter = SortableQuickServicesAdapter(ArrayList());

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_personalize_quick_services
    }

    override fun setViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(mViewDataBinding.toolbar, getString(R.string.quick_menu))
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(viewDataBinding.recyclerView)

        viewDataBinding.iBtnSubmit.setOnClickListener(View.OnClickListener {
            Log.e("service list2", "sorted")
            Log.e("", "\n")

            for ((index, item) in adapter.getItems().withIndex()) {
                item.priority = index
            }

            for (item in adapter.getItems()) {
                Log.e("service id", item.id.toString())
                Log.e("", "\n")
                Log.e("service id", item.name)
                Log.e("", "\n")
                Log.e("service priority", item.priority.toString())
                Log.e("", "\n")
                Log.e("service enabled", item.enabled.toString())
                Log.e("", "\n")
            }
            getViewModel().personalizeQuickServices(adapter.getItems()).observe(this, {
                when (it.status) {
                    Status.LOADING -> {
                        viewDataBinding.progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        viewDataBinding.progressBar.visibility = View.GONE
                        showToast(it.message)
                        val intent = Intent();
                        intent.putExtra("sorted_quick_services", adapter.getItems())
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    Status.ERROR -> {
                        viewDataBinding.progressBar.visibility = View.GONE

                        showToast(it.message)
                    }
                    else -> {
                    }
                }
            })
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun listenToVariables() {

    }

    override fun fetchData() {
        viewModel.setQuickHomeServices(intent.extras?.get("quick_services") as ArrayList<QuickService>)

        viewDataBinding.recyclerView.adapter = viewModel.homeQuickServices.value.let { items ->
            Log.e("service list1", "first")
            Log.e("", "\n")

            if (items != null) {
                for (item in items) {
                    Log.e("service id", item.id.toString())
                    Log.e("", "\n")
                    Log.e("service id", item.name)
                    Log.e("", "\n")
                    Log.e("service priority", item.priority.toString())
                    Log.e("", "\n")
                    Log.e("service enabled", item.enabled.toString())
                    Log.e("", "\n")
                }
            }
            adapter = SortableQuickServicesAdapter(items as ArrayList<QuickService>)
            adapter.itemClickListener = this
            adapter
        }
    }

    override fun onItemClick(item: QuickService) {
        when (item.id) {
            Constants.TRANSFER -> {
            }
            Constants.ZAKAT -> {
            }
            Constants.SADAKA -> {
            }
            Constants.UNIVERSITIES -> {
            }
            Constants.SCHOOLS -> {
            }
            Constants.MOBILE_PAYMENT -> {
            }
            Constants.ADSL -> {
            }
            Constants.SEP -> {
            }
            Constants.RESTAURANTS -> {
            }
        }
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Specify the directions of movement
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            // Notify your adapter that an item is moved from x position to y position
            val sourcePosition = viewHolder.bindingAdapterPosition
            val targetPosition = target.bindingAdapterPosition

            Collections.swap(adapter.getItems(), sourcePosition, targetPosition)
            adapter.notifyItemMoved(sourcePosition, targetPosition)
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            // true: if you want to start dragging on long press
            // false: if you want to handle it yourself
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        // 1. This callback is called when a ViewHolder is selected.
        //    We highlight the ViewHolder here.
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                       actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)

            if (actionState == ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.5f
            }
        }
        // 2. This callback is called when the ViewHolder is
        //    unselected (dropped). We unhighlight the ViewHolder here.
        override fun clearView(recyclerView: RecyclerView,
                               viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder?.itemView?.alpha = 1.0f
        }
    }
}