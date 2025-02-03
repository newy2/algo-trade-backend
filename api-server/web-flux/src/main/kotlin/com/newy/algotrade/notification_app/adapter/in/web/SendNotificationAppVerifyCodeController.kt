package com.newy.algotrade.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.model.SendNotificationAppVerifyCodeRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.SendNotificationAppVerifyCodeResponse
import com.newy.algotrade.notification_app.port.`in`.SendNotificationAppVerifyCodeInPort
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SendNotificationAppVerifyCodeController(
    private val sendNotificationAppVerifyCodeInPort: SendNotificationAppVerifyCodeInPort
) {
    @AdminOnly
    @PostMapping("/setting/notification/verify-code/publish")
    suspend fun sendVerifyCode(
        @LoginUserInfo loginUser: LoginUser,
        @RequestBody request: SendNotificationAppVerifyCodeRequest
    ): ResponseEntity<SendNotificationAppVerifyCodeResponse> {
        val verifyCode = sendNotificationAppVerifyCodeInPort.sendVerifyCode(request.toInPortModel(loginUser.id))

        return ResponseEntity.ok().body(
            SendNotificationAppVerifyCodeResponse(
                webhookUrl = request.webhookUrl,
                verifyCode = verifyCode,
            )
        )
    }
}
