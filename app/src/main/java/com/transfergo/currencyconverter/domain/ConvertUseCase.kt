package com.transfergo.currencyconverter.domain

import com.transfergo.currencyconverter.data.api.ApiService
import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import com.transfergo.currencyconverter.data.local.LocalDataSource
import com.transfergo.currencyconverter.presentation.UiState
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import java.math.BigDecimal
import javax.inject.Inject

class ConvertUseCase @Inject constructor(
    private val service: ApiService,
    private val localDataSource: LocalDataSource,
    private val zipper: ConvertZipperUseCase,
) : (ConvertModel) -> Single<UiState> {

    override fun invoke(model: ConvertModel): Single<UiState> =
        service.getFxRates(
            currencyFrom = model.currencyFrom,
            currencyTo = model.currencyTo,
            amount = model.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        ).zipWith(localDataSource.getAllCurrencies(), zipper)
}

class ConvertZipperUseCase @Inject constructor() :
    BiFunction<FxRatesResponse, Map<String, Int>, UiState> {

    override fun apply(response: FxRatesResponse, currencies: Map<String, Int>): UiState {
        val fromIcon = currencies[response.from]
        val toIcon = currencies[response.to]
        return UiState.Success(
            LastKnownState(
                response.from, fromIcon, response.to, toIcon,
                response.rate, response.fromAmount, response.toAmount
            )
        )
    }
}

data class ConvertModel(val currencyFrom: String, val currencyTo: String, val amount: String)

data class LastKnownState(
    val from: String?,
    val fromIcon: Int?,
    val to: String?,
    val toIcon: Int?,
    val rate: BigDecimal?,
    val fromAmount: Float?,
    val toAmount: Float?
)