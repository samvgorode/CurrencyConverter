package com.transfergo.currencyconverter.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.transfergo.currencyconverter.databinding.SelectCurrencyItemBinding
import com.transfergo.currencyconverter.domain.Currency

class SelectCurrencyAdapter(private val itemClick: (Currency) -> Unit) :
    RecyclerView.Adapter<SelectCurrencyViewHolder>() {

    private var items: List<Currency>? = null

    fun setItems(items: List<Currency>) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCurrencyViewHolder {
        val binding = SelectCurrencyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectCurrencyViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: SelectCurrencyViewHolder, position: Int) {
        items?.getOrNull(position)?.let(holder::bind)
    }

    override fun getItemCount(): Int = items?.size ?: 0

}

class SelectCurrencyViewHolder(
    private val binding: SelectCurrencyItemBinding,
    private val itemClick: (Currency) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(currency: Currency) = binding.run {
        currencyRoot.setOnClickListener { itemClick(currency) }
        currencyName.run {
            text = currency.name
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, currency.icon), null, null, null
            )
        }
    }
}