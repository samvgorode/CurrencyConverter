package com.transfergo.currencyconverter.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.transfergo.currencyconverter.data.api.response.FxRatesResponse
import com.transfergo.currencyconverter.domain.*
import com.transfergo.currencyconverter.presentation.LastKnownState
import com.transfergo.currencyconverter.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val convert: ConvertUseCase,
    private val getExcludingCurrenciesUseCase: GetExcludingCurrenciesUseCase,
    private val getAllCurrenciesUseCase: GetAllCurrenciesUseCase,
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> = _currencies

    private val responseMapper = ResponseMapper()

    fun convert(currencyFrom: String, currencyTo: String, amount: String) {
        convert(ConvertModel(currencyFrom, currencyTo, amount))
            .doOnSubscribe {
                _uiState.postValue(UiState.Progress)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                _uiState.postValue(
                    responseMapper.toSuccessUiState(response, getAllCurrenciesUseCase)
                )
            }, {
                _uiState.postValue(
                    responseMapper.toErrorUiState(it)
                )
            })
    }

    fun getCurrencies(excludeCurrencies: List<String>) {
        val currencies = getExcludingCurrenciesUseCase(excludeCurrencies)
        _currencies.postValue(currencies)
    }
}

class ResponseMapper {

    fun toSuccessUiState(
        response: FxRatesResponse,
        getCurrenciesUseCase: GetAllCurrenciesUseCase
    ) = response.run {
        val allCurrencies = getCurrenciesUseCase()
        val fromIcon = allCurrencies.find { it.name == from }?.icon
        val toIcon = allCurrencies.find { it.name == to }?.icon
        UiState.Success(LastKnownState(from, fromIcon, to, toIcon, rate, fromAmount, toAmount))
    }

    fun toErrorUiState(response: Throwable) = response.run {
        UiState.Error(message.orEmpty())
    }
}