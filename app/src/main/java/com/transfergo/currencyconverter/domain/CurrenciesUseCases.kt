package com.transfergo.currencyconverter.domain

import androidx.annotation.DrawableRes
import com.transfergo.currencyconverter.data.local.LocalDataSource
import javax.inject.Inject

class GetExcludingCurrenciesUseCase @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val mapper: CurrencyMapper,
) : (List<String>) -> List<Currency> {

    override fun invoke(currenciesToExclude: List<String>): List<Currency> =
        localDataSource.getCurrenciesExcluding(currenciesToExclude)
            .map(mapper::fromMapEntryToCurrency)
}

class GetAllCurrenciesUseCase @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val mapper: CurrencyMapper,
) : () -> List<Currency> {

    override fun invoke(): List<Currency> =
        localDataSource.getAllCurrencies()
            .map(mapper::fromMapEntryToCurrency)
}

class CurrencyMapper @Inject constructor() {
    fun fromMapEntryToCurrency(entry: Map.Entry<String, Int>): Currency = entry.run {
        Currency(name = key, icon = value)
    }
}

data class Currency(
    val name: String,
    @DrawableRes val icon: Int
)