package com.newy.algotrade.web_flux.market_account.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.web_flux.market_account.adapter.out.persistence.repository.MarketAccountR2dbcEntity
import com.newy.algotrade.web_flux.market_account.adapter.out.persistence.repository.MarketAccountRepository
import com.newy.algotrade.web_flux.market_account.adapter.out.persistence.repository.MarketServerRepository
import org.springframework.stereotype.Component

@Component
class MarketAccountPersistenceAdapter(
    private val marketServerRepository: MarketServerRepository,
    private val marketAccountRepository: MarketAccountRepository
) : MarketAccountPort {

    override suspend fun findMarketServer(market: Market, isProductionServer: Boolean) =
        marketServerRepository.findByMarketCodeAndIsProductionServer(
            marketCode = market.name,
            isProductionServer = isProductionServer
        )?.toDomainEntity()

    override suspend fun existsMarketAccount(domainEntity: MarketAccount) =
        marketAccountRepository.existsByMarketServerIdAndAppKeyAndAppSecret(
            marketServerId = domainEntity.marketServer.id,
            appKey = domainEntity.appKey,
            appSecret = domainEntity.appSecret,
        )

    override suspend fun saveMarketAccount(domainEntity: MarketAccount) =
        marketAccountRepository
            .save(MarketAccountR2dbcEntity(domainEntity))
            .toDomainEntity(domainEntity.marketServer)
}