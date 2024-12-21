package com.newy.algotrade.web_flux.config

import com.newy.algotrade.common.domain.consts.GlobalEnv
import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.common.domain.mapper.JsonConverter
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.common.web.http.HttpApiClient
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WebConfig {
    @Bean
    open fun okHttpClient(): OkHttpClient = OkHttpClient()

    @Bean
    open fun lsSecHttpApiClient(
        okHttpClient: OkHttpClient,
        globalEnv: GlobalEnv,
        jsonConverter: JsonConverter,
    ): HttpApiClient = DefaultHttpApiClient(
        client = okHttpClient,
        host = globalEnv.LS_SEC_WEB_URL,
        jsonConverter = jsonConverter,
    )

    @Bean
    open fun byBitHttpApiClient(
        okHttpClient: OkHttpClient,
        globalEnv: GlobalEnv,
        jsonConverter: JsonConverter,
    ): HttpApiClient = DefaultHttpApiClient(
        client = okHttpClient,
        host = globalEnv.BY_BIT_WEB_URL,
        jsonConverter = jsonConverter,
    )

    @Bean
    open fun slackHttpApiClient(
        okHttpClient: OkHttpClient,
        jsonConverter: JsonConverter,
    ): HttpApiClient = DefaultHttpApiClient(
        client = okHttpClient,
        host = NotificationAppType.SLACK.host,
        jsonConverter = jsonConverter,
    )
}
