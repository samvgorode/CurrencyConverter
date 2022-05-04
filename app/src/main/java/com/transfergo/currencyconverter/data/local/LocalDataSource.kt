package com.transfergo.currencyconverter.data.local

import com.transfergo.currencyconverter.R
import io.reactivex.rxjava3.core.Single

/**
 *  LocalDataSource - stores data about available currencies.
 *  Now we use hardcoded Map<String, Int>
 *  It can be easily refactored to some DB later.
 */
class LocalDataSource {

    fun getCurrenciesExcluding(currencies: List<String>): Single<Map<String, Int>> =
        Single.fromCallable {
            getAvailableCurrencies().apply { currencies.forEach { remove(it) } }
        }

    fun getAllCurrencies(): Single<Map<String, Int>> = Single.fromCallable {
        getAvailableCurrencies()
    }

    private fun getAvailableCurrencies() = mutableMapOf(
        "DKK" to R.drawable.ic_currency_dkk_small,
        "EUR" to R.drawable.ic_currency_eur_small,
        "GBP" to R.drawable.ic_currency_gbp_small,
        "HUF" to R.drawable.ic_currency_huf_small,
        "NOK" to R.drawable.ic_currency_nok_small,
        "PLN" to R.drawable.ic_currency_pln_small,
        "RON" to R.drawable.ic_currency_ron_small,
        "SEK" to R.drawable.ic_currency_sek_small
    )
}

