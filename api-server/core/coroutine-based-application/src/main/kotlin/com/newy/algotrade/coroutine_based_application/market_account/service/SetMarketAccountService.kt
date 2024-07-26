package com.newy.algotrade.coroutine_based_application.market_account.service

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.domain.common.exception.DuplicateDataException

open class SetMarketAccountService(
    private val marketAccountPort: MarketAccountPort,
) : SetMarketAccountUseCase {
    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        if (marketAccountPort.hasMarketAccount(marketAccount)) {
            throw DuplicateDataException("이미 등록된 appKey, appSecret 입니다.")
        }

        return marketAccountPort.setMarketAccount(marketAccount)
    }
}