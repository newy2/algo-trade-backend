package com.newy.algotrade.web_flux.config

import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.SendNotificationLogReadingConverter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.UserStrategyKeyReadingConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
@EnableR2dbcAuditing
open class R2dbcConfig {
    @Bean
    open fun conversions(databaseClient: DatabaseClient): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(databaseClient.connectionFactory)
        val converters = dialect.converters + R2dbcCustomConversions.STORE_CONVERTERS

        return R2dbcCustomConversions(
            CustomConversions.StoreConversions.of(dialect.simpleTypeHolder, converters),
            customConverters()
        )
    }

    private fun customConverters() = listOf(
        SendNotificationLogReadingConverter(),
        UserStrategyKeyReadingConverter(),
    )
}