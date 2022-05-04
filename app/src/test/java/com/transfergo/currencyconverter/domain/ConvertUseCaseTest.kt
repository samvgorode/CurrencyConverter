package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.api.ApiService
import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import com.transfergo.currencyconverter.data.local.LocalDataSource
import com.transfergo.currencyconverter.presentation.LastKnownState
import com.transfergo.currencyconverter.presentation.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class ConvertUseCaseTest {

    @Test
    fun `invoke method should return Single Error`() {
        val from = "EUR"
        val to = "UAH"
        val model = ConvertModel(from, to, "1")
        val throwable = mockk<Throwable>()
        val single = Single.error<FxRatesResponse>(throwable)
        val localSource = getLocalDataSource(mapOf())
        val apiService = getApiService(model, single)
        val zipper = getZipperUseCase()
        val useCase = ConvertUseCase(apiService, localSource, zipper)
        useCase.invoke(model).test()
            .assertError(throwable)
            .dispose()

        verify { apiService.getFxRates(from, to, BigDecimal.ONE) }
        verify { localSource.getAllCurrencies() }
    }

    @Test
    fun `invoke method should return Single FxRatesResponse with correct rate`() {
        val from = "EUR"
        val fromIcon = 1
        val to = "UAH"
        val toIcon = 2
        val model = ConvertModel(from, to, "1")
        val response = getResponse()
        val single = Single.just(response)
        val apiService = getApiService(model, single)
        val map = mapOf(from to fromIcon, to to toIcon)
        val localSource = getLocalDataSource(map)
        val zipper = getZipperUseCase(from, fromIcon, to, toIcon)
        val useCase = ConvertUseCase(apiService, localSource, zipper)

        useCase.invoke(model).test()
            .assertValue {
                it is UiState.Success &&
                        it.lastKnownState?.from == from &&
                        it.lastKnownState?.fromIcon == fromIcon &&
                        it.lastKnownState?.to == to &&
                        it.lastKnownState?.toIcon == toIcon
            }
            .dispose()

        verify { apiService.getFxRates(from, to, BigDecimal.ONE) }
        verify { localSource.getAllCurrencies() }
        verify { zipper.apply(response, map) }
    }

    private fun getApiService(input: ConvertModel, output: Single<FxRatesResponse>): ApiService =
        mockk {
            every {
                getFxRates(
                    input.currencyFrom, input.currencyTo,
                    input.amount.toBigDecimal()
                )
            } returns output
        }

    private fun getLocalDataSource(map : Map<String, Int>): LocalDataSource = mockk {
        every { getAllCurrencies() } returns Single.just(map)
    }

    private fun getZipperUseCase(from: String? = "", fromIcon: Int? = 1, to: String? = "", toIcon: Int? = 1) : ConvertZipperUseCase {
        val state = LastKnownState(
            from, fromIcon, to, toIcon, BigDecimal.ONE, null, null)

        return mockk {
            every { apply(any(), any()) } returns UiState.Success(state)
        }
    }

    private fun getResponse() = FxRatesResponse("EUR", "UAH", BigDecimal.ONE, 1f, 2f)
}