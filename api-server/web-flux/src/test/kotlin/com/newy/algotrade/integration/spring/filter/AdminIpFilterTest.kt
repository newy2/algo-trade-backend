package com.newy.algotrade.integration.spring.filter

import com.newy.algotrade.spring.annotation.AdminOnly
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import kotlin.test.Test

@RestController
class TestTargetController {
    @GetMapping("/public-api")
    fun publicApi(exchange: ServerWebExchange): String {
        return "a"
    }

    @AdminOnly
    @GetMapping("/admin-user-only-api")
    fun privateApi(exchange: ServerWebExchange): String {
        return "b"
    }
}

@ActiveProfiles("test")
@WebFluxTest(controllers = [TestTargetController::class])
class AdminIpFilterTest(
    @Value("\${app.admin.ip}") private val adminUserIp: String
) {
    private val guestUserId: String = "232.176.138.178"

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `public api 접속`() {
        arrayOf(adminUserIp, guestUserId).forEach {
            assertApiResult(
                path = "/public-api",
                requestIp = it,
                responseStatus = HttpStatus.OK,
                responseBody = "a"
            )
        }
    }

    @Test
    fun `private api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "b"),
            Triple(guestUserId, HttpStatus.FORBIDDEN, "Access denied (Admin access required)"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/admin-user-only-api",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }

    private fun assertApiResult(path: String, requestIp: String, responseStatus: HttpStatusCode, responseBody: String) {
        webTestClient.get().uri(path)
            .header("X-Forwarded-For", requestIp)
            .exchange()
            .expectStatus().isEqualTo(responseStatus)
            .expectBody(String::class.java).isEqualTo(responseBody)
    }
}