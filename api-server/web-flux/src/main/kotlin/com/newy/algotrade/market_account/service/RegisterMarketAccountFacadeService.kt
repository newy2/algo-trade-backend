package com.newy.algotrade.market_account.service

import com.newy.algotrade.market_account.port.`in`.RegisterMarketAccountInPort
import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand
import org.springframework.stereotype.Service

@Service
class RegisterMarketAccountFacadeService(
    private val service: RegisterMarketAccountCommandService
) : RegisterMarketAccountInPort {
    override suspend fun registerMarketAccount(command: RegisterMarketAccountCommand) {
        val marketAccount = command.toDomainModel()

        service.checkDuplicateMarketAccount(marketAccount)
        service.validMarketAccount(marketAccount.marketCode, marketAccount.privateApiInfo)
        service.saveMarketAccount(marketAccount)
    }
}