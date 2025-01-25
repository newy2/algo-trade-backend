package com.newy.algotrade.unit.notification_send.adapter.`in`.internal_system

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_send.adapter.`in`.internal_system.SendNotificationMessageEventListener
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SendNotificationMessageEventListenerTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `EventListener 는 Event 모델을 InPort 모델로 변환해야 한다`() = runTest {
        lateinit var inPortModel: SendNotificationMessageCommand
        val listener = SendNotificationMessageEventListener(
            backgroundScope = this,
            sendNotificationMessageInPort = { command ->
                inPortModel = command
            }
        )
        val eventModel = SendNotificationMessageEvent(
            userId = 1,
            message = "test message",
            isVerified = true,
        )

        listener.onReceiveMessage(eventModel)
        advanceUntilIdle() // wait for backgroundScope

        assertEquals(
            SendNotificationMessageCommand(
                userId = 1,
                message = "test message",
                isVerified = true
            ),
            inPortModel
        )
    }
}