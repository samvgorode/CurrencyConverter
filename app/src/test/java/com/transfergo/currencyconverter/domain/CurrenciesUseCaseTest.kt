package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.local.LocalDataSource
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.DKK
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.DKK_ICON
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.EUR
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.GBP
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrenciesUseCaseTest {

    @Test
    fun `GetExcludingCurrenciesUseCase should return currencies`() {
        val exclude = listOf(EUR, GBP)
        val resultMap = mapOf(DKK to DKK_ICON)
        val localSource = getLocalDataSource(exclude, resultMap)
        val mapperOutput = listOf(Currency(DKK, DKK_ICON))
        val mapper = getCurrenciesMapper(resultMap, mapperOutput)
        val useCase = GetExcludingCurrenciesUseCase(localSource, mapper)
        useCase(exclude).test().assertValue(mapperOutput).dispose()

        verify { localSource.getCurrenciesExcluding(exclude) }
        verify { mapper.apply(resultMap) }
    }

    @Test
    fun `GetAllCurrenciesUseCase should return currencies`() {
        val resultMap = mapOf(DKK to DKK_ICON)
        val localSource = getLocalDataSource(output = resultMap)
        val mapperOutput = listOf(Currency(DKK, DKK_ICON))
        val mapper = getCurrenciesMapper(resultMap, mapperOutput)
        val useCase = GetAllCurrenciesUseCase(localSource, mapper)
        useCase().test().assertValue(mapperOutput).dispose()

        verify { localSource.getAllCurrencies() }
        verify { mapper.apply(resultMap) }
    }

    @Test
    fun `CurrenciesMapper should map`() {
        val input = mapOf(DKK to DKK_ICON)
        val output = listOf(Currency(DKK, DKK_ICON))
        val mapper = CurrenciesMapper()

        assertEquals(mapper.apply(input), output)
    }


    private fun getCurrenciesMapper(
        input: Map<String, Int>,
        output: List<Currency>
    ): CurrenciesMapper = mockk {
        every { apply(input) } returns output
    }

    private fun getLocalDataSource(
        input: List<String> = listOf(),
        output: Map<String, Int>
    ): LocalDataSource =
        mockk {
            every { getCurrenciesExcluding(input) } returns Single.just(output)
            every { getAllCurrencies() } returns Single.just(output)
        }
}