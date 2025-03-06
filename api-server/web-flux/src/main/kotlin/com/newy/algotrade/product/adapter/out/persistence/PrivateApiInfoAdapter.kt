package com.newy.algotrade.product.adapter.out.persistence

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.product.adapter.out.persistence.repository.MarketAccountR2dbcRepository
import com.newy.algotrade.product.adapter.out.persistence.repository.MarketR2dbcRepository
import com.newy.algotrade.product.port.out.FindPrivateApiInfoOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class PrivateApiInfoAdapter(
    private val marketAccountR2dbcRepository: MarketAccountR2dbcRepository,
    private val marketR2dbcRepository: MarketR2dbcRepository
) : FindPrivateApiInfoOutPort {
    override suspend fun findPrivateApiInfos(userId: Long): Map<MarketCode, PrivateApiInfo> {
        val marketAccounts = marketAccountR2dbcRepository.findByUserIdAndUseYnOrderByIdAsc(userId)
        if (marketAccounts.isEmpty()) {
            return emptyMap()
        }
        val markets = marketR2dbcRepository.findByIdIn(marketAccounts.map { it.marketId }.toSet())

        return marketAccounts.associate {
            val marketCode = markets.getMarketCode(it.marketId)
            val privateApiInfo = PrivateApiInfo(
                appKey = it.appKey,
                appSecret = it.appSecret
            )

            marketCode to privateApiInfo
        }
    }
}