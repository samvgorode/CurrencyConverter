package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.api.ApiService
import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal
import javax.inject.Inject

class ConvertUseCase @Inject constructor(private val service: ApiService) :
        (ConvertModel) -> Single<FxRatesResponse> {

    override fun invoke(model: ConvertModel): Single<FxRatesResponse> =
        service.getFxRates(
            currencyFrom = model.currencyFrom,
            currencyTo = model.currencyTo,
            amount = model.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        )
}

data class ConvertModel(val currencyFrom: String, val currencyTo: String, val amount: String)