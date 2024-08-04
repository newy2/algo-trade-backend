package com.newy.algotrade.coroutine_based_application.market_account.port.`in`

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.market_account.MarketAccount

fun interface SetMarketAccountUseCase {
    suspend fun setMarketAccount(command: SetMarketAccountCommand): MarketAccount
}