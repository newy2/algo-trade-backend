package com.newy.algotrade.web_flux.product.adapter.out.web

import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchByBitProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchLsSecProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchProductPriceProxy
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.common.consts.GlobalEnv
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.web_flux.common.annotation.ExternalSystemAdapter
import org.springframework.beans.factory.annotation.Qualifier

@ExternalSystemAdapter
class FetchProductPriceSpringAdapterProxy(
    @Qualifier("lsSecHttpApiClient") lsSecHttpApiClient: HttpApiClient,
    @Qualifier("byBitHttpApiClient") byBitHttpApiClient: HttpApiClient,
    globalEnv: GlobalEnv,
) : FetchProductPriceProxy(
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