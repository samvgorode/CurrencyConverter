package com.transfergo.currencyconverter.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.transfergo.currencyconverter.domain.ConvertModel
import com.transfergo.currencyconverter.domain.ConvertUseCase
import com.transfergo.currencyconverter.domain.Currency
import com.transfergo.currencyconverter.domain.GetExcludingCurrenciesUseCase
import com.transfergo.currencyconverter.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val convert: ConvertUseCase,
    private val getExcludingCurrenciesUseCase: GetExcludingCurrenciesUseCase,
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> = _currencies

    fun convert(currencyFrom: String, currencyTo: String, amount: String) {
        convert(ConvertModel(currencyFrom, currencyTo, amount))
            .doOnSubscribe {
                _uiState.postValue(UiState.Progress)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(_uiState::postValue) {
                _uiState.postValue(UiState.Error(it.message.orEmpty()))
            }
    }

    fun getCurrencies(excludeCurrencies: List<String>) {
        getExcludingCurrenciesUseCase(excludeCurrencies)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(_currencies::postValue) {
                _uiState.postValue(UiState.Error(it.message.orEmpty()))
            }
    }
}