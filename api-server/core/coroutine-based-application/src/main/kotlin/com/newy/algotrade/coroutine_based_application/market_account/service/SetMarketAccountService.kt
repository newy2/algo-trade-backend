package com.newy.algotrade.coroutine_based_application.market_account.service

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import com.newy.algotrade.domain.common.exception.NotFoundRowException

open class SetMarketAccountService(
    private val marketAccountPort: MarketAccountPort,
) : SetMarketAccountUseCase {
    override suspend fun setMarketAccount(command: SetMarketAccountCommand) =
        getMarketServer(command)
            .let {
                command.toDomainEntity(it)
            }.also {
                if (marketAccountPort.hasMarketAccount(it)) {
                    throw DuplicateDataException("이미 등록된 appKey, appSecret 입니다.")
                }
            }.let {
                marketAccountPort.saveMarketAccount(it)
            }

    private suspend fun getMarketServer(command: SetMarketAccountCommand) =
        marketAccountPort.getMarketServer(command.market, command.isProduction)
            ?: throw NotFoundRowException("market_server 를 찾을 수 없습니다 (market: ${command.market.name}, isProduction: ${command.isProduction})")
}