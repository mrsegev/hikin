package com.yossisegev.hikin.kin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yossisegev.hikin.R

/**
 * Created by Yossi Segev on 16/11/2018.
 */
class PaymentsAdapter: RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {

    lateinit var payments: List<PaymentRecord>


    fun add(payments: List<PaymentRecord>) {
        this.payments = payments
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): PaymentViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_payment, viewGroup, false)
        return PaymentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return payments.size
    }

    override fun onBindViewHolder(vh: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        vh.amount.text = payment.amount
    }


    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount = itemView.findViewById<TextView>(R.id.amount)
    }
}