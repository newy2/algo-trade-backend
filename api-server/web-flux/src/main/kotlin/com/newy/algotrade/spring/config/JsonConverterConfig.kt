package com.newy.algotrade.spring.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.mapper.JsonConverterByJackson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonConverterConfig {
    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper()

    @Bean
    fun jsonConverter(objectMapper: ObjectMapper): JsonConverter = JsonConverterByJackson(objectMapper)
}