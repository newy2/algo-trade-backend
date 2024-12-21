package com.newy.algotrade.market_account.port.`in`

import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.`in`.model.SetMarketAccountCommand

interface MarketAccountUseCase :
    SetMarketAccountUseCase

fun interface SetMarketAccountUseCase {
    suspend fun setMarketAccount(command: SetMarketAccountCommand): MarketAccount
}