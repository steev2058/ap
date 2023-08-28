package com.apps2you.albaraka.data.model

class MonthDuration(private val id: Int,
                    private val name: String
) : SpinnerItem {

    override fun getId(): Int = id

    override fun getName(): String = name
}