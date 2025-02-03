package com.newy.algotrade.spring.auth.resolver

import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import com.newy.algotrade.spring.auth.model.UserRole
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class LoginUserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUserInfo::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<in Any> {
        return Mono.justOrEmpty(
            LoginUser(
                id = exchange.attributes["loginUserId"]!!.toString().toLong(),
                role = UserRole.valueOf(exchange.attributes["loginUserRole"]!!.toString())
            )
        )
    }
}