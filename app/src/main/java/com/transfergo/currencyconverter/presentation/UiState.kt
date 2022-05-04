package com.transfergo.currencyconverter.presentation

import com.transfergo.currencyconverter.domain.LastKnownState

sealed class UiState {
    data class Success(val lastKnownState: LastKnownState?) : UiState()
    data class Error(val message: String) : UiState()
    object Progress : UiState()
}