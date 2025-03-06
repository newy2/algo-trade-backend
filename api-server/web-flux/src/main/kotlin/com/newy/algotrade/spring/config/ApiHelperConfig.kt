package com.newy.algotrade.spring.config

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.common.web.helper.ApiHelperFactory
import com.newy.algotrade.common.web.helper.ByBitApiHelper
import com.newy.algotrade.common.web.helper.LsSecApiHelper
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiHelperConfig(
    @Value("\${app.bybit.webserver.host}") private val byBitWebServerHost: String,
    @Value("\${app.lssec.webserver.host}") private val lsSecWebServerHost: String,
) {
    @Bean
    fun apiHelperFactory(
        okHttpClient: OkHttpClient,
        jsonConverter: JsonConverter,
    ) = ApiHelperFactory(
        cache = mapOf(
            MarketCode.BY_BIT to ByBitApiHelper.getInstance(
                DefaultHttpApiClient(
                    client = okHttpClient,
                    host = byBitWebServerHost,
                    jsonConverter = jsonConverter,
                )
            ),
            MarketCode.LS_SEC to LsSecApiHelper.getInstance(
                DefaultHttpApiClient(
                    client = okHttpClient,
                    host = lsSecWebServerHost,
                    jsonConverter = jsonConverter,
                )
            )
        )
    )
}