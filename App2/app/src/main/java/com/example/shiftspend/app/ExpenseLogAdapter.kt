package com.shiftspend.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shiftspend.app.data.ExpenseLog
import java.util.Locale

class ExpenseLogAdapter(private val logs: List<ExpenseLog>) :
    RecyclerView.Adapter<ExpenseLogAdapter.LogViewHolder>() {

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon    : ImageView = itemView.findViewById(R.id.iv_log_icon)
        val tvDate    : TextView  = itemView.findViewById(R.id.tv_log_date)
        val tvTitle   : TextView  = itemView.findViewById(R.id.tv_log_title)
        val tvVehicle : TextView  = itemView.findViewById(R.id.tv_log_vehicle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]

        holder.tvDate.text    = log.date
        holder.tvTitle.text   = "${log.type} – ${String.format(Locale.US, "%.0f", log.amount)} ${log.currency}"
        holder.tvVehicle.text = log.vehicle.uppercase()

        when (log.type) {
            "Fuel" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_fuel)
                holder.ivIcon.setBackgroundResource(R.drawable.bg_wear_ok)
            }
            "Oil Change" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_oil)
                holder.ivIcon.setBackgroundResource(R.drawable.bg_wear_alert)
            }
            "Parts" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_engine)
                holder.ivIcon.setBackgroundResource(R.drawable.bg_wear_ok)
            }
            "Repairs" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_car_wireframe)
                holder.ivIcon.setBackgroundResource(R.drawable.bg_wear_alert)
            }
        }
    }

    override fun getItemCount() = logs.size
}