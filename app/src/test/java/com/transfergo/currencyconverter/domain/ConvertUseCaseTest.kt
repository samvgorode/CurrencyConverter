package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.api.ApiService
import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
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
        val model = ConvertModel("EUR", "UAH", "1")
        val throwable = mockk<Throwable>()
        val single = Single.error<FxRatesResponse>(throwable)
        val useCase = ConvertUseCase(getApiService(model, single))
        useCase.invoke(model).test()
            .assertError(throwable)
            .dispose()

        verify { useCase.invoke(model) }
    }

    @Test
    fun `invoke method should return Single FxRatesResponse with correct rate`() {
        val model = ConvertModel("EUR", "UAH", "1")
        val response = getResponse()
        val single = Single.just(response)
        val useCase = ConvertUseCase(getApiService(model, single))

        useCase.invoke(model).test()
            .assertValue(response)
            .dispose()

        verify { useCase.invoke(model) }
    }

    private fun getApiService(input: ConvertModel, output: Single<FxRatesResponse>): ApiService =
        mockk {
            every {
                getFxRates(
                    input.currencyFrom, input.currencyTo,
                    input.amount.toBigDecimal().setScale(2, RoundingMode.CEILING)
                )
            } returns output
        }

    private fun getResponse() = FxRatesResponse("EUR", "UAH", BigDecimal.ONE, 1f, 2f)

    private fun BigDecimal.withTwoSigns() = setScale(2, RoundingMode.CEILING)
}