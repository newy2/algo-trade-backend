package com.newy.algotrade.product.adapter.out.persistence

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.product.adapter.out.persistence.repository.MarketR2dbcEntity

fun List<MarketR2dbcEntity>.getMarketCode(marketId: Long): MarketCode = this
    .find { market -> market.id == marketId }!!
    .let { market -> MarketCode.valueOf(market.code) }

fun List<MarketR2dbcEntity>.getMarketId(marketCode: MarketCode): Long = this
    .find { market -> market.code == marketCode.name }!!.id