package com.newy.algotrade.common.web.helper

import com.newy.algotrade.common.consts.MarketCode

class ApiHelperFactory(private val cache: Map<MarketCode, ApiHelper>) {
    fun getInstance(marketCode: MarketCode): ApiHelper = cache.getValue(marketCode)
}