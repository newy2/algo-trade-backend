package com.newy.algotrade.market_account.adapter.out.persistence

import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.out.ExistsMarketAccountOutPort
import com.newy.algotrade.market_account.port.out.SaveMarketAccountOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class MarketAccountAdapter : ExistsMarketAccountOutPort, SaveMarketAccountOutPort {
    override suspend fun existsMarketAccount(key: MarketAccount.Key): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun saveMarketAccount(marketAccount: MarketAccount) {
        TODO("Not yet implemented")
    }
}