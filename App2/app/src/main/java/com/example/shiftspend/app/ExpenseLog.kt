package com.shiftspend.app.data

data class ExpenseLog(
    val id: Long = System.currentTimeMillis(),
    val type: String,           // "Fuel", "Parts", "Repairs", "Oil Change"
    val amount: Double,
    val currency: String,       // "AED" or "USD"
    val vehicle: String,
    val notes: String,
    val date: String            // formatted display string
)