package com.newy.algotrade.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppResponse
import com.newy.algotrade.notification_app.port.`in`.VerifyNotificationAppInPort
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.AdminUser
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
        @AdminUser currentUser: LoginUser,
        @RequestBody request: VerifyNotificationAppRequest
    ) = verifyNotificationAppInPort.verify(request.toInPortModel(currentUser.id)).let { isSuccess ->
        ResponseEntity.ok().body(
            VerifyNotificationAppResponse(isSuccess)
        )
    }
}