package com.newy.algotrade.web_flux.market_account.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.HasMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.SetMarketAccountPort
import org.springframework.stereotype.Component

@Component
class MarketAccountPersistenceAdapter(
    private val repository: MarketAccountRepository
) : HasMarketAccountPort, SetMarketAccountPort {
    override suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        return repository.getMarketAccountId(
            isProductionServer = marketAccount.isProduction,
            code = marketAccount.market.name,
            appKey = marketAccount.appKey,
            appSecret = marketAccount.appSecret,
        ) != null
    }

    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        return repository.setMarketAccount(
            isProductionServer = marketAccount.isProduction,
            code = marketAccount.market.name,
            appKey = marketAccount.appKey,
            appSecret = marketAccount.appSecret,
            displayName = marketAccount.displayName,
        )
    }
}