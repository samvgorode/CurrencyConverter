package com.transfergo.currencyconverter.domain

import androidx.annotation.DrawableRes
import com.transfergo.currencyconverter.data.local.LocalDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import javax.inject.Inject

class GetExcludingCurrenciesUseCase @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val mapper: CurrenciesMapper,
) : (List<String>) -> Single<List<Currency>> {

    override fun invoke(currenciesToExclude: List<String>): Single<List<Currency>> =
        localDataSource.getCurrenciesExcluding(currenciesToExclude).map(mapper)
}

class GetAllCurrenciesUseCase @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val mapper: CurrenciesMapper,
) : () -> Single<List<Currency>> {

    override fun invoke(): Single<List<Currency>> =
        localDataSource.getAllCurrencies().map(mapper)
}

class CurrenciesMapper @Inject constructor(): Function<Map<String, Int>, List<Currency>> {

    override fun apply(map: Map<String, Int>): List<Currency> = map.map { entry ->
        Currency(name = entry.key, icon = entry.value)
    }
}

data class Currency(
    val name: String,
    @DrawableRes val icon: Int
)