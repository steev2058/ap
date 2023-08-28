package com.apps2you.albaraka.utils

import android.content.Context
import android.util.AttributeSet

class CustomSpinner(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatSpinner(
        context,
        attrs
)
{
    var listener: OnItemSelectedListener? = null

    override fun setSelection(position: Int)
    {
        super.setSelection(position)
        if (position == selectedItemPosition)
        {
            listener!!.onItemSelected(this, selectedView, position, selectedItemId)
        }
    }

    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?)
    {
        this.listener = listener
    }
}