package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.auth.adpter.out.external_system.LsSecAccessTokenHttpApi
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.GlobalEnv
import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.spring.annotation.ExternalSystemAdapter
import com.newy.algotrade.common.web.http.HttpApiClient
import org.springframework.beans.factory.annotation.Qualifier

@ExternalSystemAdapter
class SpringFetchProductPriceProxyAdapter(
    @Qualifier("lsSecHttpApiClient") lsSecHttpApiClient: HttpApiClient,
    @Qualifier("byBitHttpApiClient") byBitHttpApiClient: HttpApiClient,
    globalEnv: GlobalEnv,
) : FetchProductPriceProxyAdapter(
    mapOf(
        Market.LS_SEC to FetchLsSecProductPrice(
            lsSecHttpApiClient,
            LsSecAccessTokenHttpApi(lsSecHttpApiClient),
            PrivateApiInfo(
                key = globalEnv.LS_SEC_API_KEY,
                secret = globalEnv.LS_SEC_API_SECRET,
            )
        ),
        Market.BY_BIT to FetchByBitProductPrice(
            byBitHttpApiClient
        )
    )
)