package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.api.ApiService
import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ConvertUseCase @Inject constructor(private val service: ApiService) :
        (ConvertModel) -> Single<FxRatesResponse> {

    override fun invoke(model: ConvertModel): Single<FxRatesResponse> =
        service.getFxRates(
            currencyFrom = model.currencyFrom,
            currencyTo = model.currencyTo,
            amount = model.amount.toBigDecimalOrNull()?.withTwoSigns() ?: BigDecimal.ZERO
        ).map {
            val fixedRate = it.rate?.withTwoSigns()
            it.copy(rate = fixedRate)
        }

    private fun BigDecimal.withTwoSigns() = setScale(2, RoundingMode.CEILING)
}

data class ConvertModel(val currencyFrom: String, val currencyTo: String, val amount: String)