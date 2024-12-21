package com.newy.algotrade.web_flux.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.domain.mapper.JsonConverter
import com.newy.algotrade.common.domain.mapper.JsonConverterByJackson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JsonConverterConfig {
    @Bean
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()

    @Bean
    open fun jsonConverter(objectMapper: ObjectMapper): JsonConverter = JsonConverterByJackson(objectMapper)
}