package com.newy.algotrade.coroutine_based_application.market_account.service

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.MarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.port.out.ExistsMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.port.out.FindMarketServerPort
import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.port.out.SaveMarketAccountPort
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import com.newy.algotrade.domain.common.exception.NotFoundRowException

open class MarketAccountCommandService(
    private val existsMarketAccountPort: ExistsMarketAccountPort,
    private val findMarketServerPort: FindMarketServerPort,
    private val saveMarketAccountPort: SaveMarketAccountPort
) : MarketAccountUseCase {
    constructor(marketAccountPort: MarketAccountPort) : this(
        existsMarketAccountPort = marketAccountPort,
        findMarketServerPort = marketAccountPort,
        saveMarketAccountPort = marketAccountPort,
    )

    override suspend fun setMarketAccount(command: SetMarketAccountCommand) =
        getMarketServer(command)
            .let {
                command.toDomainEntity(it)
            }.also {
                if (existsMarketAccountPort.existsMarketAccount(it)) {
                    throw DuplicateDataException("이미 등록된 appKey, appSecret 입니다.")
                }
            }.let {
                saveMarketAccountPort.saveMarketAccount(it)
            }

    private suspend fun getMarketServer(command: SetMarketAccountCommand) =
        findMarketServerPort.findMarketServer(command.market, command.isProduction)
            ?: throw NotFoundRowException("market_server 를 찾을 수 없습니다 (market: ${command.market.name}, isProduction: ${command.isProduction})")
}