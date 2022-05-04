package com.transfergo.currencyconverter.data.local

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
        val exclude = "EUR"
        dataSource.getCurrenciesExcluding(listOf(exclude))
            .test()
            .assertValue { it.containsKey(exclude).not() }
            .dispose()
    }
}