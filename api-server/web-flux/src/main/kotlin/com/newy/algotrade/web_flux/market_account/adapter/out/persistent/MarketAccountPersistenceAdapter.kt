package com.newy.algotrade.web_flux.market_account.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.market_account.SetMarketAccount
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository.MarketAccountR2dbcEntity
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository.MarketAccountRepository
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository.MarketServerRepository
import org.springframework.stereotype.Component

@Component
class MarketAccountPersistenceAdapter(
    private val marketServerRepository: MarketServerRepository,
    private val marketAccountRepository: MarketAccountRepository
) : MarketAccountPort {

    override suspend fun getMarketServer(market: Market, isProductionServer: Boolean) =
        marketServerRepository.findByMarketCodeAndIsProductionServer(
            marketCode = market.name,
            isProductionServer = isProductionServer
        )?.toDomainEntity()

    override suspend fun hasMarketAccount(domainEntity: SetMarketAccount) =
        marketAccountRepository.existsByMarketServerIdAndAppKeyAndAppSecret(
            marketServerId = domainEntity.marketServer.id,
            appKey = domainEntity.appKey,
            appSecret = domainEntity.appSecret,
        )

    override suspend fun saveMarketAccount(domainEntity: SetMarketAccount) =
        marketAccountRepository.save(MarketAccountR2dbcEntity(domainEntity)).id > 0
}