package com.newy.algotrade.coroutine_based_application.market_account.application.port.out

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand

interface HasMarketAccountPort {
    suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean
}