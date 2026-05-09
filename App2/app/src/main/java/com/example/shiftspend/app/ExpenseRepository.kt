package com.shiftspend.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ExpenseRepository(context: Context, userEmail: String) {

    // Unique storage per user based on email
    private val prefs: SharedPreferences =
        context.getSharedPreferences("shift_spend_$userEmail", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_EXPENSES = "expenses"
    }

    fun saveExpense(log: ExpenseLog) {
        val list = getAll().toMutableList()
        list.add(0, log)
        prefs.edit().putString(KEY_EXPENSES, gson.toJson(list)).apply()
    }

    fun getAll(): List<ExpenseLog> {
        val json = prefs.getString(KEY_EXPENSES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<ExpenseLog>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLogsForVehicle(vehicleName: String): List<ExpenseLog> {
        return getAll().filter { it.vehicle == vehicleName }
    }

    fun getTotalForVehicle(vehicleName: String): Double {
        return getLogsForVehicle(vehicleName).sumOf { it.amount }
    }

    fun getMonthlyTotalsForVehicle(vehicleName: String): Map<String, Float> {
        return getLogsForVehicle(vehicleName)
            .groupBy { it.date.split(" ")[0] }
            .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }
    }

    fun clearAll() {
        prefs.edit().remove(KEY_EXPENSES).apply()
    }
}