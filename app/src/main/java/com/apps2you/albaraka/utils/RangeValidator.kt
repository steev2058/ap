package com.apps2you.albaraka.utils

import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize

@Parcelize
class RangeValidator(private val minDate:Long, private val maxDate:Long) : CalendarConstraints.DateValidator{


    override fun isValid(date: Long): Boolean {
        return !(minDate > date || maxDate < date)

    }

}