package com.allenwang.currency.data.remote

import com.allenwang.currency.BuildConfig
import com.allenwang.currency.data.unity.SupportedCurrency
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SupportedCurrencyApi @Inject constructor(
    private val service: CurrencyRetrofitService
) {
    fun getSupportedCurrencies(): Observable<List<SupportedCurrency>> {
        return service.getSupportedCurrencies(BuildConfig.CURRENCY_LAYER_KEY)
            .map {
                val list = ArrayList<SupportedCurrency>()
                for ((key, value) in it.currencies) {
                    val c = SupportedCurrency(key, value)
                    list.add(c)
                }
                list
            }.flatMap {
                Observable.just(it)
            }
    }
}
