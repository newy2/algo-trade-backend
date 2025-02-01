package com.newy.algotrade.market_account.service

import com.newy.algotrade.common.exception.DuplicateDataException
import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.out.ExistsMarketAccountOutPort
import com.newy.algotrade.market_account.port.out.SaveMarketAccountOutPort
import com.newy.algotrade.market_account.port.out.ValidMarketAccountOutPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterMarketAccountCommandService(
    private val existsMarketAccountOutPort: ExistsMarketAccountOutPort,
    private val validMarketAccountOutPort: ValidMarketAccountOutPort,
    private val saveMarketAccountOutPort: SaveMarketAccountOutPort,
) {
    @Transactional(readOnly = true)
    suspend fun checkDuplicateMarketAccount(key: MarketAccount.Key) {
        if (existsMarketAccountOutPort.existsMarketAccount(key)) {
            throw DuplicateDataException("같은 이름의 계정을 등록할 수 없습니다. (displayName: ${key.displayName})")
        }
    }

    suspend fun validMarketAccount(privateApiInfo: MarketAccount.PrivateApiInfo) {
        if (!validMarketAccountOutPort.validMarketAccount(privateApiInfo)) {
            throw HttpResponseException("거래소의 계정 정보를 조회할 수 없습니다.")
        }
    }

    @Transactional
    suspend fun saveMarketAccount(marketAccount: MarketAccount) {
        saveMarketAccountOutPort.saveMarketAccount(marketAccount)
    }
}