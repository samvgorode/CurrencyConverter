package com.transfergo.currencyconverter.presentation

import java.math.BigDecimal

sealed class UiResponse {
    data class Success(val rate: BigDecimal?) : UiResponse()
    data class Error(val message: String) : UiResponse()
    object Progress : UiResponse()
}