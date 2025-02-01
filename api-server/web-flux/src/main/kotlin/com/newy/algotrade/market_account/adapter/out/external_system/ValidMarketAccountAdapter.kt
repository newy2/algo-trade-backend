package com.newy.algotrade.market_account.adapter.out.external_system

import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.out.ValidMarketAccountOutPort
import com.newy.algotrade.spring.annotation.ExternalSystemAdapter

@ExternalSystemAdapter
class ValidMarketAccountAdapter : ValidMarketAccountOutPort {
    override suspend fun validMarketAccount(privateApiInfo: MarketAccount.PrivateApiInfo): Boolean {
        TODO("Not yet implemented")
    }
}