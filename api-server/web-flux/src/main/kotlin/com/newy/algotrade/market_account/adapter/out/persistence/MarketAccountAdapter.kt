package com.newy.algotrade.market_account.adapter.out.persistence

import com.newy.algotrade.market_account.adapter.out.persistence.repository.MarketAccountR2dbcEntity
import com.newy.algotrade.market_account.adapter.out.persistence.repository.MarketAccountR2dbcRepository
import com.newy.algotrade.market_account.adapter.out.persistence.repository.MarketR2dbcRepository
import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.out.ExistsMarketAccountOutPort
import com.newy.algotrade.market_account.port.out.SaveMarketAccountOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter
import org.springframework.beans.factory.annotation.Autowired

@PersistenceAdapter
class MarketAccountAdapter(
    @Autowired private val marketRepository: MarketR2dbcRepository,
    @Autowired private val marketAccountRepository: MarketAccountR2dbcRepository,
) : ExistsMarketAccountOutPort, SaveMarketAccountOutPort {
    override suspend fun existsMarketAccount(marketAccount: MarketAccount): Boolean {
        return marketAccountRepository.existsByUserIdAndDisplayNameAndUseYn(
            userId = marketAccount.userId,
            displayName = marketAccount.displayName,
        ) || marketAccountRepository.existsByUserIdAndAppKeyAndAppSecretAndUseYn(
            userId = marketAccount.userId,
            appKey = marketAccount.privateApiInfo.appKey,
            appSecret = marketAccount.privateApiInfo.appSecret,
        )
    }

    override suspend fun saveMarketAccount(marketAccount: MarketAccount) {
        val market = marketRepository.findByCode(marketAccount.marketCode.toString())
        marketAccountRepository.save(
            MarketAccountR2dbcEntity(
                domainModel = marketAccount,
                marketId = market!!.id,
            )
        )
    }
}