package com.apps2you.albaraka.ui.atmCard

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.DialogConfirmBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.utils.BindingUtils
import com.apps2you.albaraka.viewmodels.AtmViewModel

abstract class AtmBaseFragment<T : ViewDataBinding> :BaseFragment<T, AtmViewModel>() {

    open fun refreshList() {

    }

    protected fun showFeeDialog(title: String, fee: String, listener: AtmCardsFragment.DialogClickListener) {
        showDialog(title, getString(R.string.commission_question, fee), showOk = false, cancelable = true, listener = listener)
    }

    protected fun showDialog(title: String, message: String, showOk: Boolean, cancelable: Boolean, listener: AtmCardsFragment.DialogClickListener) {
        val dialog = Dialog(requireContext())

        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
         //  dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        val dialogDataBinding: DialogConfirmBinding = DialogConfirmBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        dialog.setCancelable(cancelable)

        dialog.setContentView(dialogDataBinding.root)
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.textView, title)
        dialogDataBinding.textView.text = title
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.text, message)
        dialogDataBinding.text.text = message

        dialogDataBinding.buttonSubmit.visibility = if (showOk) View.GONE else View.VISIBLE
        dialogDataBinding.cancelButton.visibility = if (showOk) View.GONE else View.VISIBLE
        dialogDataBinding.btnOk.visibility = if (showOk) View.VISIBLE else View.GONE


        dialogDataBinding.buttonSubmit.setOnClickListener {
            dialog.dismiss()
            listener.onYesClicked()
        }

        dialogDataBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogDataBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            refreshList() // refresh list after any service
        }

        dialog.show()
    }
}