package com.transfergo.currencyconverter.presentation

import java.math.BigDecimal

sealed class UiState {
    data class Success(val lastKnownState: LastKnownState?) : UiState()
    data class Error(val message: String) : UiState()
    object Progress : UiState()
}

data class LastKnownState(
    val from: String?,
    val fromIcon: Int?,
    val to: String?,
    val toIcon: Int?,
    val rate: BigDecimal?,
    val fromAmount: Float?,
    val toAmount: Float?
)