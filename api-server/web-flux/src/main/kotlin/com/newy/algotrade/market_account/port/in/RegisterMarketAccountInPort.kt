package com.newy.algotrade.market_account.port.`in`

import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand

fun interface RegisterMarketAccountInPort {
    suspend fun registerMarketAccount(command: RegisterMarketAccountCommand)
}