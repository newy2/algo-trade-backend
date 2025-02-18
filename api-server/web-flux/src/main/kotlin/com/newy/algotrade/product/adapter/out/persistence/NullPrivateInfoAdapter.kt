package com.newy.algotrade.product.adapter.out.persistence

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.product.port.out.FindPrivateApiInfoOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NullPrivateInfoAdapter : FindPrivateApiInfoOutPort {
    override suspend fun findPrivateApiInfos(userId: Long): Map<MarketCode, PrivateApiInfo> {
        TODO("Not yet implemented")
    }
}