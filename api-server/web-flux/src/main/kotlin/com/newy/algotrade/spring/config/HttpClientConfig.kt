package com.newy.algotrade.spring.config

import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.notification.domain.Slack
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpClientConfig {
    @Bean
    fun okHttpClient(): OkHttpClient = OkHttpClient()

    @Bean
    @Qualifier("slackHttpApiClient")
    fun slackHttpApiClient(
        okHttpClient: OkHttpClient,
        jsonConverter: JsonConverter,
    ): HttpApiClient = DefaultHttpApiClient(
        client = okHttpClient,
        host = Slack.HOST,
        jsonConverter = jsonConverter,
    )
}