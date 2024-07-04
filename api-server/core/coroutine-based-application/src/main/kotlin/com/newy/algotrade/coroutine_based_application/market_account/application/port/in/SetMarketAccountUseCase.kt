package com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand

interface SetMarketAccountUseCase {
    suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean
}