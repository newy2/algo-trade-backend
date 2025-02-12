package com.newy.algotrade.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.model.DeleteNotificationAppRequest
import com.newy.algotrade.notification_app.port.`in`.DeleteNotificationAppInPort
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DeleteNotificationAppController(
    private val deleteNotificationAppInPort: DeleteNotificationAppInPort
) {
    @AdminOnly
    @DeleteMapping("/setting/notification/{notificationAppId}")
    suspend fun deleteNotificationApp(
        @LoginUserInfo loginUser: LoginUser,
        @PathVariable notificationAppId: Long
    ): ResponseEntity<Unit> {
        deleteNotificationAppInPort.deleteNotificationApp(
            DeleteNotificationAppRequest(
                userId = loginUser.id,
                notificationAppId = notificationAppId
            ).toInPortModel()
        )

        return ResponseEntity.noContent().build()
    }
}
