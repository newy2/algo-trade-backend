package com.newy.algotrade.spring.auth.config

import com.newy.algotrade.spring.auth.resolver.LoginUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class ArgumentResolverConfig : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(LoginUserArgumentResolver())
        super.configureArgumentResolvers(configurer)
    }
}