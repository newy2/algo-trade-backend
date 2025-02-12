package com.newy.algotrade.spring.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class InternalSystemAdapter(
    @get:AliasFor(annotation = Component::class, attribute = "value")
    val value: String = ""
)