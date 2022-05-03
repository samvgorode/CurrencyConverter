package com.transfergo.currencyconverter.data.api

import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Test
import java.math.BigDecimal

class ApiServiceTest {

    @Test
    fun `getFxRates method should return Single Error`() {
        val throwable = mockk<Throwable>()
        val single = Single.error<FxRatesResponse>(throwable)
        val apiService = getApiService(single)

        apiService.getFxRates("", "", BigDecimal.ONE).test()
            .assertError(throwable)
            .dispose()

        verify { apiService.getFxRates("", "", BigDecimal.ONE) }
    }

    @Test
    fun `getFxRates method should return Single FxRatesResponse with correct rate`() {
        val response = mockk<FxRatesResponse>()
        val single = Single.just(response)
        val apiService = getApiService(single)

        apiService.getFxRates("", "", BigDecimal.ONE).test()
            .assertValue(response)
            .dispose()

        verify { apiService.getFxRates("", "", BigDecimal.ONE) }
    }

    private fun getApiService(output: Single<FxRatesResponse>): ApiService = mockk {
        every { getFxRates(any(), any(), any()) } returns output
    }
}