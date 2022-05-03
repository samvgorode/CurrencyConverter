package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.local.LocalDataSource
import javax.inject.Inject

class GetCurrenciesUseCase@Inject constructor(private val localDataSource: LocalDataSource):
        (List<String>) -> Map<String, Int> {

    override fun invoke(currenciesToExclude: List<String>): Map<String, Int> =
        localDataSource.getCurrenciesExcluding(currenciesToExclude)
}