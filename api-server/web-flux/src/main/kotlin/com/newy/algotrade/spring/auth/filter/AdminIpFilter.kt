package com.newy.algotrade.spring.auth.filter

import com.newy.algotrade.spring.auth.annotation.AdminOnly
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AdminIpFilter(
    @Value("\${app.admin.ip}") private val adminIp: String,
    private val handlerMapping: RequestMappingHandlerMapping
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // TODO OPTIONS 메소드에 대한 테스트 코드 추가할 것
        if (exchange.request.method == HttpMethod.OPTIONS) {
            return chain.filter(exchange)
        }

        val isAdminIp = this.isAdminIp(exchange)
        if (isAdminOnlyHandler(exchange) && !isAdminIp) {
            return responseAccessDenied(exchange)
        }

        exchange.attributes["loginUserId"] = 1
        if (isAdminIp) {
            exchange.attributes["loginUserRole"] = "ADMIN"
        } else {
            exchange.attributes["loginUserRole"] = "GUEST"
        }

        return chain.filter(exchange)
    }

    private fun isAdminOnlyHandler(exchange: ServerWebExchange): Boolean = getHandlerMethod(exchange).let {
        val hasMethodAnnotation = it.method.getAnnotation(AdminOnly::class.java) != null
        val hasClassAnnotation = it.beanType.getAnnotation(AdminOnly::class.java) != null

        return hasMethodAnnotation || hasClassAnnotation
    }

    private fun getHandlerMethod(exchange: ServerWebExchange) =
        handlerMapping.getHandler(exchange).toFuture().getNow(null) as HandlerMethod

    private fun isAdminIp(exchange: ServerWebExchange) =
        arrayOf(getClientIp(exchange), getClientHostName(exchange)).contains(adminIp)

    private fun getClientIp(exchange: ServerWebExchange) =
        exchange.request.headers["X-Forwarded-For"]?.firstOrNull() ?: "unknown"

    private fun getClientHostName(exchange: ServerWebExchange) =
        exchange.request.remoteAddress?.address?.hostName ?: "unknown"

    private fun responseAccessDenied(exchange: ServerWebExchange): Mono<Void> {
        return exchange.response.also {
            it.statusCode = HttpStatus.FORBIDDEN
        }.let {
            val errorMessage = "Access denied (Admin access required)"

            println(errorMessage)
            println("adminIp: $adminIp")
            println("clientIp: ${getClientIp(exchange)}")
            println("clientHostName: ${getClientHostName(exchange)}")

            val buffer = it.bufferFactory().wrap(errorMessage.toByteArray())
            it.writeWith(Mono.just(buffer))
        }
    }
}