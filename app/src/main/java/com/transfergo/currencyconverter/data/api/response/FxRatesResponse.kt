package com.transfergo.currencyconverter.data.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class FxRatesResponse(
    @SerializedName("from") val from: String?,
    @SerializedName("to") val to: String?,
    @SerializedName("rate") val rate: BigDecimal?,
    @SerializedName("fromAmount") val fromAmount: Float?,
    @SerializedName("toAmount") val toAmount: Float?,
) 
