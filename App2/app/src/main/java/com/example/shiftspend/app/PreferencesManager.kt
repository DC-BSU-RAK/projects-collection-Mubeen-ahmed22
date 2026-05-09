package com.shiftspend.app.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("shift_spend_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_VEHICLE    = "pref_vehicle"
        const val KEY_CURRENCY   = "pref_currency_usd"
        const val KEY_UNITS      = "pref_units_miles"
        const val KEY_FUEL_ALERT = "pref_fuel_alert"
        const val KEY_SVC_ALERT  = "pref_service_alert"
        const val KEY_LOGGED_IN  = "pref_logged_in"
        const val KEY_EMAIL      = "pref_email"
        const val KEY_FIRST_LAUNCH = "pref_first_launch"

        // Maintenance tracking keys
        const val KEY_CURRENT_MILEAGE = "pref_current_mileage"
        const val KEY_SERVICE_TARGET  = "pref_service_target"
    }

    var vehicle: String
        get() = prefs.getString(KEY_VEHICLE, "Daily Sedan") ?: "Daily Sedan"
        set(v) = prefs.edit().putString(KEY_VEHICLE, v).apply()

    var isUsd: Boolean
        get() = prefs.getBoolean(KEY_CURRENCY, false)
        set(v) = prefs.edit().putBoolean(KEY_CURRENCY, v).apply()

    var isMiles: Boolean
        get() = prefs.getBoolean(KEY_UNITS, false)
        set(v) = prefs.edit().putBoolean(KEY_UNITS, v).apply()

    var currentMileage: Int
        get() = prefs.getInt(KEY_CURRENT_MILEAGE, 0)
        set(v) = prefs.edit().putInt(KEY_CURRENT_MILEAGE, v).apply()

    var serviceTarget: Int
        get() = prefs.getInt(KEY_SERVICE_TARGET, 5000)
        set(v) = prefs.edit().putInt(KEY_SERVICE_TARGET, v).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_LOGGED_IN, false)
        set(v) = prefs.edit().putBoolean(KEY_LOGGED_IN, v).apply()

    var savedEmail: String
        get() = prefs.getString(KEY_EMAIL, "") ?: ""
        set(v) = prefs.edit().putString(KEY_EMAIL, v).apply()

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(v) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, v).apply()

    fun getCurrencySymbol() = if (isUsd) "USD" else "AED"
    fun getDistanceUnit() = if (isMiles) "Miles" else "KM"
}