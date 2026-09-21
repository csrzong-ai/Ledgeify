package com.example.ui.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val standardDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun formatCurrency(amount: Double, symbol: String = "Rs."): String {
        return "$symbol ${decimalFormat.format(amount)}"
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return standardDateFormat.format(Date(timestamp))
    }

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp))
    }

    fun formatDateGroup(timestamp: Long): String {
        val entryCal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val nowCal = Calendar.getInstance()

        val isToday = entryCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                entryCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)

        if (isToday) return "Today, ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))}"

        val yesterdayCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val isYesterday = entryCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
                entryCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR)

        if (isYesterday) return "Yesterday, ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))}"

        return standardDateFormat.format(Date(timestamp))
    }
}
