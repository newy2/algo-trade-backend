package com.newy.algotrade.notification_app.domain

import com.newy.algotrade.common.exception.ForbiddenException

data class DeletableNotificationApp(
    private val id: Long,
    private val userId: Long,
) {
    fun checkOwnership(userId: Long) {
        if (this.userId != userId) {
            throw ForbiddenException("다른 User 의 NotificationApp 을 삭제할 수 없습니다.")
        }
    }
}