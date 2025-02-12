package com.newy.algotrade.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppResponse
import com.newy.algotrade.notification_app.port.`in`.VerifyNotificationAppInPort
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VerifyNotificationAppController(
    private val verifyNotificationAppInPort: VerifyNotificationAppInPort
) {
    @AdminOnly
    @PostMapping("/setting/notification/verify-code/verify")
    suspend fun verifyNotificationApp(
        @LoginUserInfo loginUser: LoginUser,
        @RequestBody request: VerifyNotificationAppRequest
    ): ResponseEntity<VerifyNotificationAppResponse> {
        val result = verifyNotificationAppInPort.verify(request.toInPortModel(loginUser.id))

        return ResponseEntity.ok().body(
            VerifyNotificationAppResponse(result)
        )
    }
}