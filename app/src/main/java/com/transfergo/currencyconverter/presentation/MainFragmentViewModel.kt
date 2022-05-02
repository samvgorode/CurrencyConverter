package com.transfergo.currencyconverter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.transfergo.currencyconverter.data.api.ApiService
import com.transfergo.currencyconverter.domain.ConvertModel
import com.transfergo.currencyconverter.domain.ConvertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(private val convert: ConvertUseCase): ViewModel() {

    private val _uiResponse = MutableLiveData<UiResponse>()
    val uiResponse : LiveData<UiResponse> = _uiResponse

    fun convert(currencyFrom: String, currencyTo: String, amount: String) {
        convert(ConvertModel(currencyFrom, currencyTo, amount))
            .doOnSubscribe {
                _uiResponse.postValue(UiResponse.Progress)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                _uiResponse.postValue(UiResponse.Success(response.rate))
            }, {
                _uiResponse.postValue(UiResponse.Error(it.message.orEmpty()))
            })
    }
}