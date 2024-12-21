package com.newy.algotrade.web_flux.product_price.adapter.out.external_system

import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.domain.auth.PrivateApiInfo
import com.newy.algotrade.domain.common.consts.GlobalEnv
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.product_price.adapter.out.external_system.FetchByBitProductPrice
import com.newy.algotrade.product_price.adapter.out.external_system.FetchLsSecProductPrice
import com.newy.algotrade.product_price.adapter.out.external_system.FetchProductPriceProxyAdapter
import com.newy.algotrade.web_flux.common.annotation.ExternalSystemAdapter
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
            com.newy.algotrade.auth.adpter.out.external_system.LsSecAccessTokenHttpApi(lsSecHttpApiClient),
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