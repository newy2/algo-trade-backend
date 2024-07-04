package com.newy.algotrade.coroutine_based_application.market_account.application.port.out

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand

interface SetMarketAccountPort {
    suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean
}