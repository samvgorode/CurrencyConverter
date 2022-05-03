package com.transfergo.currencyconverter.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.transfergo.currencyconverter.databinding.SelectCurrencyItemBinding

class SelectCurrencyAdapter(private val itemClick: (Pair<String, Int>) -> Unit) :
    RecyclerView.Adapter<SelectCurrencyViewHolder>() {

    private var items: Map<String, Int>? = null

    fun setItems(items: Map<String, Int>) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCurrencyViewHolder {
        val binding = SelectCurrencyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectCurrencyViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: SelectCurrencyViewHolder, position: Int) {
        items?.entries
            ?.map { Pair(it.key, it.value) }
            ?.getOrNull(position)?.let(holder::bind)
    }

    override fun getItemCount(): Int = items?.size ?: 0

}

class SelectCurrencyViewHolder(
    private val binding: SelectCurrencyItemBinding,
    private val itemClick: (Pair<String, Int>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(pair: Pair<String, Int>) = binding.run {
        currencyRoot.setOnClickListener { itemClick(pair) }
        currency.run {
            text = pair.first
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, pair.second), null, null, null
            )
        }
    }
}