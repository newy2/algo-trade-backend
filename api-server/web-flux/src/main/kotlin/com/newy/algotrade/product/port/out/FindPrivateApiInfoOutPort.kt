package com.newy.algotrade.product.port.out

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode

fun interface FindPrivateApiInfoOutPort {
    suspend fun findPrivateApiInfos(userId: Long): Map<MarketCode, PrivateApiInfo>
}