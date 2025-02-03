package com.newy.algotrade.market_account.service

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
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
    suspend fun checkDuplicateMarketAccount(marketAccount: MarketAccount) {
        if (existsMarketAccountOutPort.existsMarketAccount(marketAccount)) {
            throw DuplicateDataException("이름 또는 appKey, appSecret 이 중복되었습니다.")
        }
    }

    suspend fun validMarketAccount(marketCode: MarketCode, privateApiInfo: PrivateApiInfo) {
        if (!validMarketAccountOutPort.validMarketAccount(marketCode, privateApiInfo)) {
            throw HttpResponseException("거래소의 계정 정보를 조회할 수 없습니다.")
        }
    }

    @Transactional
    suspend fun saveMarketAccount(marketAccount: MarketAccount) {
        saveMarketAccountOutPort.saveMarketAccount(marketAccount)
    }
}