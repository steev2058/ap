package com.apps2you.albaraka.utils

import android.text.InputFilter
import android.text.Spanned
import com.apps2you.albaraka.R

class EnglishLettersAndDigitsFilter(
    private val inputListener: InputListener? = null
) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (!source.toString().matches("[a-zA-Z0-9]*".toRegex())) {
            inputListener?.onInvalidInput()
            return ""
        }
        return null
    }

    fun interface InputListener {
        fun onInvalidInput()
    }
}


