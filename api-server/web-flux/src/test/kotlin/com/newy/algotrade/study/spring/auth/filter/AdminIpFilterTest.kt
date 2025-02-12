package com.newy.algotrade.study.spring.auth.filter

import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.test.Test

@RestController
class MixedController {
    @GetMapping("/public/v1")
    fun publicApi(): String {
        return "Public API response"
    }

    @AdminOnly
    @GetMapping("/private/v1")
    fun privateApi(): String {
        return "Private API response"
    }
}

@WebFluxTest(controllers = [MixedController::class])
@DisplayName("핸들러 메서드에 @AdminOnly 애너테이션을 사용한 경우")
class MixedControllerTest : BaseAdminIpFilterTest() {
    @Test
    fun `public api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "Public API response"),
            Triple(guestUserIp, HttpStatus.OK, "Public API response"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/public/v1",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }

    @Test
    fun `private api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "Private API response"),
            Triple(guestUserIp, HttpStatus.FORBIDDEN, "Access denied (Admin access required)"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/private/v1",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }
}


@AdminOnly
@RestController
class AdminOnlyController {
    @GetMapping("/private/v2/first")
    fun privateApi1(): String {
        return "Private1 API response"
    }

    @GetMapping("/private/v2/second")
    fun privateApi2(): String {
        return "Private2 API response"
    }
}

@WebFluxTest(controllers = [AdminOnlyController::class])
@DisplayName("Controller 에 @AdminOnly 애너테이션을 사용한 경우")
class AdminOnlyControllerTest : BaseAdminIpFilterTest() {
    @Test
    fun `private1 api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "Private1 API response"),
            Triple(guestUserIp, HttpStatus.FORBIDDEN, "Access denied (Admin access required)"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/private/v2/first",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }

    @Test
    fun `private2 api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "Private2 API response"),
            Triple(guestUserIp, HttpStatus.FORBIDDEN, "Access denied (Admin access required)"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/private/v2/second",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }
}


@RestController
class AccessAdminUserController {
    @GetMapping("/public/v3")
    fun publicApi(@LoginUserInfo loginUser: LoginUser): String {
        return loginUser.toString()
    }

    @AdminOnly
    @GetMapping("/private/v3")
    fun privateApi(@LoginUserInfo loginUser: LoginUser): String {
        return loginUser.toString()
    }
}

@WebFluxTest(controllers = [AccessAdminUserController::class])
@DisplayName("@LoginUserInfo 로 어드민 사용자 여부를 조회하기")
class LoginUserControllerTest : BaseAdminIpFilterTest() {
    @Test
    fun `public api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "LoginUser(id=1, role=ADMIN)"),
            Triple(guestUserIp, HttpStatus.OK, "LoginUser(id=1, role=GUEST)"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/public/v3",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }

    @Test
    fun `private api 접속`() {
        arrayOf(
            Triple(adminUserIp, HttpStatus.OK, "LoginUser(id=1, role=ADMIN)"),
            Triple(guestUserIp, HttpStatus.FORBIDDEN, "Access denied (Admin access required)"),
        ).forEach { (requestIp, responseStatus, responseBody) ->
            assertApiResult(
                path = "/private/v3",
                requestIp = requestIp,
                responseStatus = responseStatus,
                responseBody = responseBody,
            )
        }
    }
}

@WebFluxTest
open class BaseAdminIpFilterTest {
    protected val guestUserIp: String = "232.176.138.178"

    @Value("\${app.admin.ip}")
    protected lateinit var adminUserIp: String

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    protected fun assertApiResult(
        path: String,
        requestIp: String,
        responseStatus: HttpStatusCode,
        responseBody: String
    ) {
        webTestClient.get().uri(path)
            .header("X-Forwarded-For", requestIp)
            .exchange()
            .expectStatus().isEqualTo(responseStatus)
            .expectBody(String::class.java).isEqualTo(responseBody)
    }
}