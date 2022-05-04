package com.transfergo.currencyconverter.data.local

import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.EUR
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.GBP
import org.junit.Test

class LocalDataSourceTest {

    @Test
    fun `getAllCurrencies should return non empty map`() {
        val dataSource = LocalDataSource()
        dataSource.getAllCurrencies()
            .test()
            .assertValue { it.isNotEmpty() }
            .dispose()
    }

    @Test
    fun `getCurrenciesExcluding should exclude currency`() {
        val dataSource = LocalDataSource()
        dataSource.getCurrenciesExcluding(listOf(EUR, GBP))
            .test()
            .assertValue { it.containsKey(EUR).not() && it.containsKey(GBP).not() }
            .dispose()
    }
}