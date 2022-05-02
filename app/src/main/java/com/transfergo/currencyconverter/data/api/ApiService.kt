package com.transfergo.currencyconverter.data.api

import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

const val API_URL_FX_RATES = "fx-rates"

interface ApiService {

    @GET(API_URL_FX_RATES)
    fun getFxRates(
        @Query("from") currencyFrom: String,
        @Query("to") currencyTo: String,
        @Query("amount") amount: BigDecimal
    ): Single<FxRatesResponse>
}
