package com.newy.algotrade.market_account.adapter.out.external_system

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.web.helper.ApiHelperFactory
import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.out.ValidMarketAccountOutPort
import com.newy.algotrade.spring.annotation.ExternalSystemAdapter
import org.springframework.beans.factory.annotation.Autowired

@ExternalSystemAdapter
class ValidMarketAccountAdapter(
    @Autowired val factory: ApiHelperFactory
) : ValidMarketAccountOutPort {
    override suspend fun validMarketAccount(privateApiInfo: MarketAccount.PrivateApiInfo): Boolean {
        return factory.getInstance(privateApiInfo.marketCode).isValidPrivateApiInfo(
            PrivateApiInfo(
                key = privateApiInfo.appKey,
                secret = privateApiInfo.appSecret,
            )
        )
    }
}