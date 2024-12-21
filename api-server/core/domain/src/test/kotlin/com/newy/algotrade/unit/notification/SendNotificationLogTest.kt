package com.newy.algotrade.unit.notification

import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.common.domain.consts.SendNotificationLogStatus
import com.newy.algotrade.common.domain.consts.SendNotificationLogStatus.*
import com.newy.algotrade.common.domain.consts.SlackNotificationRequestMessageFormat
import com.newy.algotrade.common.domain.exception.PreconditionError
import com.newy.algotrade.notification.domain.SendNotificationLog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private val domainEntity = SendNotificationLog(
    sendNotificationLogId = 1,
    notificationAppId = 1,
    notificationAppType = NotificationAppType.SLACK,
    url = "${NotificationAppType.SLACK.host}/111/222",
    requestMessage = "request message",
    status = REQUESTED,
)

@DisplayName("기본 기능 테스트")
class RequestedStatusSendNotificationLogTest {
    private val entity = domainEntity

    @Test
    fun `URL path 확인`() {
        assertEquals("/111/222", entity.getUrlPath())
    }

    @Test
    fun `http body 확인`() {
        assertEquals(
            SlackNotificationRequestMessageFormat.from("request message"),
            entity.getHttpRequestBody()
        )
    }

    @Test
    fun `PROCESSING 상태로 변경하기`() {
        assertEquals(entity.copy(status = PROCESSING), entity.statusProcessing())
    }
}

@DisplayName("응답 메세지 설정 테스트")
class ProcessingStatusSendNotificationLogTest {
    private val entity = domainEntity.copy(status = PROCESSING)

    @Test
    fun `성공 응답 메세지 변경하기`() {
        val successResponseMessage = "ok"
        assertEquals(
            entity.copy(status = SUCCEED, responseMessage = "ok"),
            entity.responseMessage(successResponseMessage)
        )
    }

    @Test
    fun `실패 응답 메세지 변경하기`() {
        val notSuccessResponseMessage = "invalid token"
        assertEquals(
            entity.copy(status = FAILED, responseMessage = "invalid token"),
            entity.responseMessage(notSuccessResponseMessage)
        )
    }
}

@DisplayName("에러 발생 테스트")
class ErrorSendNotificationLogTest {
    @Test
    fun `PROCESSING 상태로 변경하기 - 에러`() {
        SendNotificationLogStatus.values().filter { it != REQUESTED }.forEach {
            assertThrows<PreconditionError> {
                domainEntity.copy(status = it).statusProcessing()
            }.also { exception ->
                assertEquals("REQUESTED 상태만 변경 가능합니다. (status: ${it.name})", exception.message)
            }
        }
    }

    @Test
    fun `응답 메세지 변경하기`() {
        SendNotificationLogStatus.values().filter { it != PROCESSING }.forEach {
            assertThrows<PreconditionError> {
                domainEntity.copy(status = it).responseMessage("ok")
            }.also { exception ->
                assertEquals("PROCESSING 상태만 변경 가능합니다. (status: ${it.name})", exception.message)
            }
        }
    }
}