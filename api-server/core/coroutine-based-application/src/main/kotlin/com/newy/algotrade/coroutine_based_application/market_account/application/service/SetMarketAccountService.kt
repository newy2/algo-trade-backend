package com.newy.algotrade.coroutine_based_application.market_account.application.service

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.HasMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.SetMarketAccountPort
import com.newy.algotrade.domain.common.exception.DuplicateDataException

open class SetMarketAccountService(
    private val hasMarketAccountPort: HasMarketAccountPort,
    private val setMarketAccountPort: SetMarketAccountPort
) : SetMarketAccountUseCase {
    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        if (hasMarketAccountPort.hasMarketAccount(marketAccount)) {
            throw DuplicateDataException("이미 등록된 appKey, appSecret 입니다.")
        }

        return setMarketAccountPort.setMarketAccount(marketAccount)
    }
}