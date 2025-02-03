package com.newy.algotrade.setting.adapter.out.persistence

import com.newy.algotrade.setting.domain.MarketAccount
import com.newy.algotrade.setting.port.out.GetMarketAccountsOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NullMarketAccountsAdapter : GetMarketAccountsOutPort {
    override fun getMarketAccounts(userId: Long): List<MarketAccount> {
        TODO("Not yet implemented")
    }
}