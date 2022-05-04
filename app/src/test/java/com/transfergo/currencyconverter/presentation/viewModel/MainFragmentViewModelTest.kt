package com.transfergo.currencyconverter.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.transfergo.currencyconverter.RxImmediateSchedulerRule
import com.transfergo.currencyconverter.TestExtensions.getOrAwaitValue
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.EUR
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.EUR_ICON
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.GBP
import com.transfergo.currencyconverter.domain.*
import com.transfergo.currencyconverter.presentation.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainFragmentViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var rxExecutorRule = RxImmediateSchedulerRule()

    @Test
    fun `convert should provide proper value via livedata`() {
        val currencyFrom = EUR
        val currencyTo = GBP
        val amount = "1"
        val input = ConvertModel(currencyFrom, currencyTo, amount)
        val lastKnownState = mockk<LastKnownState>()
        val output = UiState.Success(lastKnownState)
        val convertUseCase = getConvertUseCase(input, Single.just(output))
        val vm = MainFragmentViewModel(convertUseCase, getExcludingCurrenciesUseCase())
        vm.convert(currencyFrom, currencyTo, amount)
        verify { convertUseCase(input) }
        assertEquals(vm.uiState.getOrAwaitValue(), output)
    }

    @Test
    fun `convert should provide Error via livedata`() {
        val currencyFrom = EUR
        val currencyTo = GBP
        val amount = "1"
        val input = ConvertModel(currencyFrom, currencyTo, amount)
        val errorMassage = "errorMassage"
        val output = mockk<Throwable> {
            every { message } returns errorMassage
        }
        val convertUseCase = getConvertUseCase(input, Single.error(output))
        val vm = MainFragmentViewModel(convertUseCase, getExcludingCurrenciesUseCase())
        vm.convert(currencyFrom, currencyTo, amount)
        verify { convertUseCase(input) }
        assertEquals((vm.uiState.getOrAwaitValue() as? UiState.Error)?.message, errorMassage)
    }

    @Test
    fun `getCurrencies should provide proper value via livedata`() {
        val input = listOf(GBP)
        val output = listOf(Currency(EUR, EUR_ICON))
        val getExcludingCurrenciesUseCase = getExcludingCurrenciesUseCase(input, Single.just(output))
        val vm = MainFragmentViewModel(getConvertUseCase(), getExcludingCurrenciesUseCase)
        vm.getCurrencies(input)
        verify { getExcludingCurrenciesUseCase(input) }
        assertEquals(vm.currencies.getOrAwaitValue(), output)
    }

    @Test
    fun `getCurrencies should provide Error via livedata`() {
        val input = listOf(GBP)
        val errorMassage = "errorMassage"
        val output = mockk<Throwable> {
            every { message } returns errorMassage
        }
        val getExcludingCurrenciesUseCase = getExcludingCurrenciesUseCase(input, Single.error(output))
        val vm = MainFragmentViewModel(getConvertUseCase(), getExcludingCurrenciesUseCase)
        vm.getCurrencies(input)
        verify { getExcludingCurrenciesUseCase(input) }
        assertEquals((vm.uiState.getOrAwaitValue() as? UiState.Error)?.message, errorMassage)
    }

    private fun getConvertUseCase(
        input: ConvertModel = mockk(),
        output: Single<UiState> = Single.just(mockk())
    ): ConvertUseCase {
        val useCase = mockk<ConvertUseCase>()
        every { useCase(input) } returns output
        return useCase
    }

    private fun getExcludingCurrenciesUseCase(
        input: List<String> = listOf(),
        output: Single<List<Currency>> = Single.just(mockk())
    ): GetExcludingCurrenciesUseCase {
        val useCase = mockk<GetExcludingCurrenciesUseCase>()
        every { useCase(input) } returns output
        return useCase
    }
}