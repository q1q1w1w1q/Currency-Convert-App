package com.allenwang.currency.ui.quotes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.allenwang.currency.R
import com.allenwang.currency.data.unity.CurrencyQuote
import com.allenwang.currency.util.QuoteUtil

class CurrencyQuotesRecyclerViewAdapter(
    var values: List<CurrencyQuote>,
    var amountToConvert: Int
) : RecyclerView.Adapter<CurrencyQuotesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_convert_currency, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.currencyCode
        holder.contentView.text = QuoteUtil.calculate(item.quote, amountToConvert).toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.content)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}