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

    private fun getAvailableCurrencies() = mutableMapOf(DKK to DKK_ICON, EUR to EUR_ICON,
        GBP to GBP_ICON, HUF to HUF_ICON, NOK to NOK_ICON, PLN to PLN_ICON, RON to RON_ICON,
        SEK to SEK_ICON
    )

    companion object {
        const val DKK = "DKK"
        const val EUR = "EUR"
        const val GBP = "GBP"
        const val HUF = "HUF"
        const val NOK = "NOK"
        const val PLN = "PLN"
        const val RON = "RON"
        const val SEK = "SEK"
        const val DKK_ICON = R.drawable.ic_currency_dkk_small
        const val EUR_ICON = R.drawable.ic_currency_eur_small
        const val GBP_ICON = R.drawable.ic_currency_gbp_small
        const val HUF_ICON = R.drawable.ic_currency_huf_small
        const val NOK_ICON = R.drawable.ic_currency_nok_small
        const val PLN_ICON = R.drawable.ic_currency_pln_small
        const val RON_ICON = R.drawable.ic_currency_ron_small
        const val SEK_ICON = R.drawable.ic_currency_sek_small
    }
}
